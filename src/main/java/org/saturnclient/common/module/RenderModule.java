package org.saturnclient.common.module;

/**
 * RenderModule provides access to render-time information that does
 * not belong to the player or world, such as the current window
 * dimensions, camera perspective, and the frame's FPS counter.
 */
public interface RenderModule {

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
