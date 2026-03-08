package org.saturnclient.cosmetics;

import org.saturnclient.client.ServiceClient;
import org.saturnclient.client.player.SaturnPlayer;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.asset.IdentifierRef;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.saturnclient.cosmetics.utils.AnimatedCloakData;
import org.saturnclient.cosmetics.utils.GifDecoder;
// import org.saturnclient.cosmetics.utils.IdentifierUtils;

/**
 * Manages the cloak system for Saturn Client.
 * Handles loading and caching of player cloaks.
 * Originally created by IIpho3nix and modified for Saturn Client by leo.
 */
public class Cloaks {
    public static final String[] ALL_CLOAKS = { "glitch", "mercedes_flow", "crimson_mark", "bmw", "amg", "amg_petronas",
            "ferrari", "redbull", "black_hole_amethyst", "black_hole_flame", "black_hole_white" };
    private static final String[] ANIMATED_CLOAKS = { "glitch", "black_hole_amethyst", "black_hole_flame",
            "black_hole_white" };

    private static final String CLOAKS_RESOURCE_PATH = "assets/saturnclient/textures/cloaks/";
    public static final List<String> availableCloaks = new ArrayList<>();
    public static IdentifierRef cloakCacheIdentifier = null;
    public static final Map<UUID, List<AnimatedCloakData>> animatedCloaks = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastFrameTime = new ConcurrentHashMap<>();

    private static final ExecutorService CLOAK_LOADER_EXECUTOR = Executors.newFixedThreadPool(
            Math.min(4, Runtime.getRuntime().availableProcessors()),
            r -> {
                Thread t = new Thread(r, "cloak-loader");
                t.setDaemon(true);
                t.setPriority(Thread.NORM_PRIORITY - 1);
                return t;
            });

    private static final ConcurrentHashMap<String, List<AnimatedCloakData>> CLOAK_CACHE = new ConcurrentHashMap<>();

    /**
     * Initializes the cloak system.
     * Loads cloak textures from resources.
     */
    public static void initialize() {
        availableCloaks.add(0, "");
    }

    /**
     * Sets cloak of the current player
     * 
     * @param cloakName Name of the cloak file to load
     */
    public static void setCloak(String cloakName) {
        if (availableCloaks.contains(cloakName)) {
            setCloak(ServiceClient.uuid, cloakName);
            ServiceClient.setCloak(cloakName);
        }
    }

    /**
     * Handles loading and caching of a new cloak texture, without connecting to the
     * server.
     * 
     * @param cloakName Name of the cloak file to load
     */
    public static void setCloak(UUID uuid, String cloakName) {
        SaturnPlayer player = SaturnPlayer.get(uuid);

        if (player != null) {
            player.cloak = cloakName;
        }

        loadCloak(uuid);
    }

    /**
     * Handles loading and caching of a new cloak texture, without connecting to the
     * server.
     * 
     * @param cloakName Name of the cloak file to load
     */
    public static void loadCloak(UUID uuid) {
        SaturnPlayer player = SaturnPlayer.get(uuid);

        if (player != null && player.cloak != null) {
            if (!player.cloak.isEmpty()) {
                if (Arrays.asList(ANIMATED_CLOAKS).contains(player.cloak)) {
                    loadAnimatedCloakAsync(uuid, player.cloak)
                            .exceptionally(throwable -> {
                                Providers.saturn.logError("Failed to load animated cloak for player: " + player.cloak,
                                        throwable);
                                return null;
                            });

                } else {
                    Providers.saturn.getClient().executeOnThread(() -> loadStaticCloak(player.cloak + ".png"));
                }
            }
        }
    }

    /**
     * Loads and processes a static cloak from a PNG file in resources.
     * 
     * @param fileName Name of the PNG file to load
     */
    private static void loadStaticCloak(String fileName) {
        try {
            String resourcePath = CLOAKS_RESOURCE_PATH + fileName;
            InputStream inputStream = Cloaks.class.getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);

                // Add debug logging for static cloak
                Providers.saturn.logInfo("Static cloak dimensions: " + image.getWidth() + "x" + image.getHeight());

                String safeFileName = fileName.toLowerCase(Locale.ROOT)
                        .replace(' ', '_')
                        .replaceAll("[^a-z0-9/._-]", "");

                cloakCacheIdentifier = IdentifierRef.ofSaturn("cloaks_" + safeFileName);
                Providers.saturn.registerBufferedImageTexture(cloakCacheIdentifier, image);
            }
        } catch (IOException e) {
            Providers.saturn.logError("Failed to load static cloak from resources: " + fileName, e);
        }
    }

    public static CompletableFuture<Void> loadAnimatedCloakAsync(UUID uuid, String cloakName) {
        return CompletableFuture.runAsync(() -> {
            String fileName = cloakName + ".gif";

            // Check cache first
            List<AnimatedCloakData> cached = CLOAK_CACHE.get(fileName);
            if (cached != null) {
                animatedCloaks.put(uuid, cached);
                lastFrameTime.put(uuid, System.currentTimeMillis());
                return;
            }

            String resourcePath = CLOAKS_RESOURCE_PATH + fileName;

            try (InputStream inputStream = Cloaks.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    Providers.saturn.logError("Cloak resource not found: " + resourcePath);
                    return;
                }

                // Read all bytes at once - more efficient than multiple reads
                byte[] data = inputStream.readAllBytes();
                GifDecoder.GifImage gif = GifDecoder.read(data);

                int frameCount = gif.getFrameCount();
                if (frameCount == 0) {
                    Providers.saturn.logError("No frames found in animated cloak: " + fileName);
                    return;
                }

                // Pre-allocate collections with known size
                List<AnimatedCloakData> animatedFrames = new ArrayList<>(frameCount);
                String baseFrameId = fileName.replace(".gif", "");

                // Process frames in batch
                for (int i = 0; i < frameCount; i++) {
                    BufferedImage frame = gif.getFrame(i);
                    int delay = Math.max(gif.getDelay(i) * 10, 50); // Minimum 50ms delay

                    String frameId = baseFrameId + "_frame_" + i;
                    IdentifierRef frameIdentifier = IdentifierRef.ofSaturn("cloaks_" + frameId);

                    try {
                        Providers.saturn.registerBufferedImageTexture(frameIdentifier, frame);
                        animatedFrames.add(new AnimatedCloakData(frameIdentifier, delay));
                    } catch (Exception e) {
                        Providers.saturn.logError("Failed to register frame " + i + " for cloak: " + fileName, e);
                        // Continue with other frames instead of failing completely
                    }
                }

                if (!animatedFrames.isEmpty()) {
                    // Cache the result for future use
                    CLOAK_CACHE.put(fileName, animatedFrames);
                    animatedCloaks.put(uuid, animatedFrames);
                    lastFrameTime.put(uuid, System.currentTimeMillis());

                    Providers.saturn.logInfo("Loaded " + animatedFrames.size() + " frames for animated cloak: "
                            + fileName + " (cached)");
                } else {
                    Providers.saturn.logError("No valid frames could be loaded for cloak: " + fileName);
                }

            } catch (IOException e) {
                Providers.saturn.logError("Failed to load animated cloak from resources: " + fileName, e);
            } catch (Exception e) {
                Providers.saturn.logError("Unexpected error loading animated cloak: " + fileName, e);
            }
        }, CLOAK_LOADER_EXECUTOR);
    }

    public static IdentifierRef getCurrentCloakTexture(UUID uuid) {
        SaturnPlayer player = SaturnPlayer.get(uuid);

        if (player == null || player.cloak.isEmpty()) {
            return null;
        }

        String cloakName = player.cloak;

        if (Arrays.asList(ANIMATED_CLOAKS).contains(cloakName)) {
            List<AnimatedCloakData> frames = animatedCloaks.get(uuid);
            if (frames == null || frames.isEmpty()) {
                return null;
            }

            long currentTime = System.currentTimeMillis();
            long lastTime = lastFrameTime.getOrDefault(uuid, currentTime);
            int currentFrame = 0;

            long elapsedTime = currentTime - lastTime;
            long totalTime = 0;
            for (int i = 0; i < frames.size(); i++) {
                totalTime += frames.get(i).getDelayMs();
                if (elapsedTime < totalTime) {
                    currentFrame = i;
                    break;
                }
            }

            if (elapsedTime >= totalTime) {
                lastFrameTime.put(uuid, currentTime);
                currentFrame = 0;
            }

            IdentifierRef identifier = frames.get(currentFrame).getTextureId();

            if (identifier == null) {
                return IdentifierRef.ofSaturn("textures/cloaks/" + cloakName + ".png");
            }

            return identifier;
        } else {
            return IdentifierRef.ofSaturn("textures/cloaks/" + cloakName + ".png");
        }
    }
}