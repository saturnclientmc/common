package org.saturnclient.cosmetics.utils;

import org.saturnclient.common.ref.asset.IdentifierRef;

/**
 * Represents a single frame of an animated cloak.
 */
public class AnimatedCloakData {

    private final IdentifierRef textureId;
    private final int delayMs;

    /**
     * Creates a new animated cloak frame.
     *
     * @param textureId The texture identifier for this frame
     * @param delayMs   The delay in milliseconds before the next frame
     */
    public AnimatedCloakData(IdentifierRef textureId, int delayMs) {
        this.textureId = textureId;
        this.delayMs = delayMs;
    }

    /**
     * @return The texture identifier for this frame
     */
    public IdentifierRef getTextureId() {
        return textureId;
    }

    /**
     * @return The delay in milliseconds before the next frame
     */
    public int getDelayMs() {
        return delayMs;
    }
}