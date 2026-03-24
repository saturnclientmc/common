package org.saturnclient.common.feature;

/**
 * RenderFeature provides access to render-time information that does
 * not belong to the player or world, such as the current window
 * dimensions, camera perspective, and the frame's FPS counter.
 */
public interface RenderFeature {

    // ---------------------------------------------------------------
    // Window
    // ---------------------------------------------------------------

    int getScaledWindowWidth();

    int getScaledWindowHeight();

    // ---------------------------------------------------------------
    // Performance
    // ---------------------------------------------------------------

    int getFps();

    // ---------------------------------------------------------------
    // Camera perspective
    // ---------------------------------------------------------------

    boolean isFirstPerson();

    void setFirstPerson();

    void setThirdPersonBack();
}
