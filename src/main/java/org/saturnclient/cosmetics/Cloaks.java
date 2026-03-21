package org.saturnclient.cosmetics;

import org.saturnclient.client.ServiceClient;
import org.saturnclient.client.player.SaturnPlayer;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.asset.IdentifierRef;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.saturnclient.cosmetics.utils.AnimatedCloakData;
import org.saturnclient.cosmetics.utils.GifDecoder;
// import org.saturnclient.cosmetics.utils.IdentifierUtils;

/**
 * Manages the cloak system for Saturn Client.
 * Handles loading and caching of player cloaks.
 * Originally created by IIpho3nix and modified for Saturn Client by leo.
 */
public class Cloaks {
    public static final String[] ALL_CLOAKS = { "glitch", "mercedes_flow", "crimson_mark", "bmw", "amg", "amg_petronas", "ferrari", 
            "redbull", "black_hole_amethyst", "black_hole_flame", "black_hole_white", "albania_mark", "end", "spanish_empire", "spain_flag" };
    private static final String[] ANIMATED_CLOAKS = { "glitch", "black_hole_amethyst", "black_hole_flame",
            "black_hole_white" };

    private static final String CLOAKS_RESOURCE_PATH = "assets/saturnclient/textures/cloaks/";
    public static final List<String> availableCloaks = new ArrayList<>();
    public static IdentifierRef cloakCacheIdentifier = null;
    public static final Map<String, List<AnimatedCloakData>> animatedCloaks = new ConcurrentHashMap<>();
    private static final Map<String, Long> lastFrameTime = new ConcurrentHashMap<>();

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

        loadCloak(cloakName);
    }

    /**
     * Handles loading and caching of a new cloak texture, without connecting to the
     * server.
     * 
     * @param cloakName Name of the cloak file to load
     */
    public static void loadCloak(String cloakName) {
        if (cloakName != null && !cloakName.isEmpty()) {
            if (Arrays.asList(ANIMATED_CLOAKS).contains(cloakName)) {
                Providers.saturn.getClient().executeOnThread(() -> loadAnimatedCloak(cloakName));
            }
        }
    }

    public static void loadAnimatedCloak(String cloakName) {
        String fileName = cloakName + ".gif";

        // Check cache first
        List<AnimatedCloakData> cached = CLOAK_CACHE.get(fileName);
        if (cached != null) {
            animatedCloaks.put(cloakName, cached);
            lastFrameTime.put(cloakName, System.currentTimeMillis());
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
                animatedCloaks.put(cloakName, animatedFrames);
                lastFrameTime.put(cloakName, System.currentTimeMillis());

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
    }

    public static IdentifierRef getCurrentCloakTexture(String cloakName) {
        if (Arrays.asList(ANIMATED_CLOAKS).contains(cloakName)) {
            List<AnimatedCloakData> frames = animatedCloaks.get(cloakName);
            if (frames == null || frames.isEmpty()) {
                return null;
            }

            long currentTime = System.currentTimeMillis();
            long lastTime = lastFrameTime.getOrDefault(cloakName, currentTime);
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
                lastFrameTime.put(cloakName, currentTime);
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