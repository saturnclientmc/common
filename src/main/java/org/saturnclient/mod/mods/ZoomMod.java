package org.saturnclient.mod.mods;

import org.saturnclient.common.provider.GLFWProvider;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.FloatProperty;
import org.saturnclient.config.property.KeybindingProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;

/**
 * ZoomMod multiplies the camera's field-of-view to create an
 * optical-zoom effect.
 *
 * The FOV override is applied inside a mixin that polls
 * {@link #shouldZoom()} and {@link #getZoomLevel()} each frame.
 * No FeatureProvider is needed — there is no per-tick engine query.
 */
public class ZoomMod extends Mod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final BoolProperty toggle = Property.bool(false);
    public static final KeybindingProperty zoomKey = Property.keybinding(GLFWProvider.GLFW_KEY_C);
    public static final FloatProperty zoomLevel = Property.floatProp(3.0f);

    /** Whether zoom is currently active (read by the FOV mixin). */
    public static boolean isZooming = false;

    public ZoomMod() {
        super(
                new ModSpec("Zoom", "zoom")
                        .description("Zoom in for a closer view")
                        .tags("Camera", "Utility")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                toggle.named("Toggle zoom"),
                zoomKey.named("Zoom Keybinding"),
                zoomLevel.named("Zoom Level"));
    }

    // ---------------------------------------------------------------
    // Mod contract
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
