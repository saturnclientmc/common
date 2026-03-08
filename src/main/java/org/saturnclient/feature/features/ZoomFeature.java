package org.saturnclient.feature.features;

import org.saturnclient.common.provider.GLFWProvider;
import org.saturnclient.config.manager.Property;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * ZoomFeature multiplies the camera's field-of-view to create an
 * optical-zoom effect.
 *
 * The FOV override is applied inside a mixin that polls
 * {@link #shouldZoom()} and {@link #getZoomLevel()} each frame.
 * No ModuleProvider is needed — there is no per-tick engine query.
 */
public class ZoomFeature extends Feature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Boolean> toggle = Property.bool(false);
    public static final Property<Integer> zoomKey = Property.keybinding(GLFWProvider.GLFW_KEY_C);
    public static final Property<Float> zoomLevel = Property.floatProp(3.0f);

    /** Whether zoom is currently active (read by the FOV mixin). */
    public static boolean isZooming = false;

    public ZoomFeature() {
        super(
                new FeatureDetails("Zoom", "zoom")
                        .description("Zoom in for a closer view")
                        .tags("Camera", "Utility")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                toggle.named("Toggle zoom"),
                zoomKey.named("Zoom Keybinding"),
                zoomLevel.named("Zoom Level"));
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
        if (toggle.value) {
            if (zoomKey.wasKeyPressed()) {
                isZooming = !isZooming;
            }
        } else {
            isZooming = zoomKey.isKeyPressed();
        }
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
        if (!e)
            isZooming = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    // ---------------------------------------------------------------
    // Mixin query API
    // ---------------------------------------------------------------

    /** Returns {@code true} when the FOV override should be applied. */
    public static boolean shouldZoom() {
        return enabled.value && isZooming;
    }

    /** Returns the zoom divisor (e.g. 3.0 → one-third of normal FOV). */
    public static float getZoomLevel() {
        return zoomLevel.value;
    }
}
