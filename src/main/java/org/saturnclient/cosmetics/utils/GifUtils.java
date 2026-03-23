package org.saturnclient.cosmetics.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.image.BufferedImage;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.asset.IdentifierRef;

public class GifUtils {
    public static Map<String, Optional<GifData>> loadedCache = new ConcurrentHashMap<>();

    private static final Queue<IdentifierRef> QUEUE = new ConcurrentLinkedQueue<>();
    private static volatile boolean RUNNING = false;
    private static Thread WORKER;

    public static IdentifierRef get(IdentifierRef gif) {
        String g = gif.toString();

        if (!loadedCache.containsKey(g)) {
            loadedCache.put(g, Optional.empty());
        }

        Optional<GifData> loadedData = loadedCache.get(g);

        if (loadedData.isEmpty()) {
            return IdentifierRef.of(g.replace(".gif", ".png"));
        }

        long now = System.currentTimeMillis();

        GifData data = loadedData.get();

        long t = now % data.totalDuration;

        int accumulated = 0;

        for (GifFrame frame : data.frames) {
            accumulated += frame.getDelayMs();
            if (t < accumulated) {
                return frame.getTextureId();
            }
        }

        return data.frames.get(data.frames.size() - 1).getTextureId();
    }

    public static GifData getData(IdentifierRef id) {
        String fileName = id.toString();

        try (InputStream inputStream = Providers.saturn.getClient().getResource(id)) {
            if (inputStream == null) {
                Providers.saturn.logError("Cloak resource not found: " + fileName);
                return null;
            }

            // Read all bytes at once - more efficient than multiple reads
            byte[] data = inputStream.readAllBytes();
            GifDecoder.GifImage gif = GifDecoder.read(data);

            int frameCount = gif.getFrameCount();
            if (frameCount == 0) {
                Providers.saturn.logError("No frames found in animated cloak: " + fileName);
                return null;
            }

            // Pre-allocate collections with known size
            List<GifFrame> animatedFrames = new ArrayList<>(frameCount);
            String baseFrameId = fileName.replace(".gif", "");

            // Process frames in batch
            for (int i = 0; i < frameCount; i++) {
                BufferedImage frame = gif.getFrame(i);
                int delay = Math.max(gif.getDelay(i) * 10, 50); // Minimum 50ms delay

                String frameId = baseFrameId + "_frame_" + i;
                IdentifierRef frameIdentifier = IdentifierRef.ofSaturn("cloaks_" + frameId);

                try {
                    Providers.saturn.registerBufferedImageTexture(frameIdentifier, frame);
                    animatedFrames.add(new GifFrame(frameIdentifier, delay));
                } catch (Exception e) {
                    Providers.saturn.logError("Failed to register frame " + i + " for cloak: " + fileName);
                    e.printStackTrace();
                }
            }

            if (!animatedFrames.isEmpty()) {
                Providers.saturn.logInfo(
                        "Loaded " + animatedFrames.size() + " frames for animated cloak: " + fileName + " (cached)");

                return new GifData(animatedFrames);
            } else {
                Providers.saturn.logError("No valid frames could be loaded for cloak: " + fileName);
            }

        } catch (Exception e) {
            Providers.saturn.logError("Failed to load animated cloak from resources: " + fileName, e);
        }

        return null;
    }

    public static synchronized void startPlayerThread() {
        if (RUNNING)
            return;

        RUNNING = true;

        WORKER = new Thread(() -> {
            long lastWorkTime = System.currentTimeMillis();

            while (true) {
                IdentifierRef id = QUEUE.poll();

                if (id != null) {
                    try {
                        GifData data = getData(id);

                        // Apply on main thread
                        Providers.saturn.getClient().executeOnThread(() -> {
                            loadedCache.put(id.toString(), Optional.of(data));
                        });

                    } catch (Exception e) {
                        Providers.saturn.logError("Failed to load gif " + id, e);
                    }

                    lastWorkTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - lastWorkTime > 5000) {
                        break;
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            RUNNING = false;
        }, "SaturnPlayer-Worker");

        WORKER.setDaemon(true);
        WORKER.start();
    }
}
