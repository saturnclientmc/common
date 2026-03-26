package org.saturnclient.mod.mods;

import java.util.function.Function;

import org.saturnclient.common.provider.GLFWProvider;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.FloatProperty;
import org.saturnclient.config.property.IntProperty;
import org.saturnclient.config.property.KeybindingProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.config.property.SelectProperty;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.ui.anim.Animation;

public class ZoomMod extends Mod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final BoolProperty toggle = Property.bool(false);
    public static final KeybindingProperty zoomKey = Property.keybinding(GLFWProvider.GLFW_KEY_C);
    public static final FloatProperty zoomLevel = Property.floatProp(3.0f);
    public static final IntProperty zoomInDuration = Property.integer(700);
    public static final IntProperty zoomOutDuration = Property.integer(300);
    public static final SelectProperty curve = Property.select(1, "Ease In Out Cubic", "Ease Out Cubic",
            "Ease In Out Back");

    public ZoomMod() {
        super(
                new ModSpec("Zoom", "zoom")
                        .description("Zoom in for a closer view")
                        .tags("Camera", "Utility")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                toggle.named("Toggle zoom"),
                zoomKey.named("Zoom Keybinding"),
                zoomLevel.named("Zoom Level"),
                zoomInDuration.named("Zoom In Duration"),
                zoomOutDuration.named("Zoom Out Duration"),
                curve.named("Curve"));
    }

    // ---------------------------------------------------------------
    // Mod fields
    // ---------------------------------------------------------------

    /** Zooming state */
    public static boolean isZooming = false;

    /** Animation state */
    private static float progress = 0f; // 0 → 1
    private static long lastTime = System.currentTimeMillis();
    private static boolean lastTarget = false; // remember previous zoom state

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
        // Update target zoom state
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
        return enabled.value && (isZooming || progress > 0f);
    }

    public static int getZoomDuration() {
        if (isZooming) {
            return zoomInDuration.value;
        } else {
            return zoomOutDuration.value;
        }
    }

    /** Returns the zoom divisor (e.g. 3.0 → one-third of normal FOV). */
    public static float getZoomLevel() {
        // Detect target change and reset timer
        if (isZooming != lastTarget) {
            lastTime = System.currentTimeMillis();
            lastTarget = isZooming;
        }

        long now = System.currentTimeMillis();
        float delta = (now - lastTime) / (float) getZoomDuration();
        lastTime = now;

        // Animate progress toward target
        if (isZooming) {
            progress = Math.min(1f, progress + delta);
        } else {
            progress = Math.max(0f, progress - delta);
        }

        // Apply curve
        Function<Double, Double> curveFn = Animation.getCurve(curve.value);
        double curved = curveFn.apply((double) progress);

        // Map to zoom level
        return 1.0f + (float) curved * (zoomLevel.value - 1f);
    }
}