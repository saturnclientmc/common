package org.saturnclient.mod.mods;

import java.util.function.Function;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.provider.GLFWProvider;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.FloatProperty;
import org.saturnclient.config.property.IntProperty;
import org.saturnclient.config.property.KeybindingProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.config.property.SelectProperty;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.common.feature.RenderFeature;
import org.saturnclient.ui.anim.Animation;

public class FreelookMod extends Mod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final BoolProperty toggle = Property.bool(true);
    public static final KeybindingProperty freelookKey = Property.keybinding(GLFWProvider.GLFW_KEY_H);

    /** Zoom properties for freelook */
    public static final FloatProperty zoomLevel = Property.floatProp(2.0f); // 2x zoom out
    public static final IntProperty zoomInDuration = Property.integer(500); // ms
    public static final IntProperty zoomOutDuration = Property.integer(300); // ms
    public static final SelectProperty curve = Property.select(1, "Ease In Out Cubic", "Ease Out Cubic",
            "Ease In Out Back");

    /** Whether freelook is currently active */
    public static boolean isFreeLooking = false;
    private static boolean wasFirstPerson = false;

    /** Animation state */
    private static long animationStartTime = System.currentTimeMillis();

    public FreelookMod() {
        super(
                new ModSpec("Freelook", "freelook")
                        .description("Look around freely without moving your character")
                        .tags("Movement", "Camera")
                        .version("v0.3.1")
                        .requires(Providers.feature::render),
                enabled.named("Enabled"),
                toggle.named("Toggle freelook"),
                freelookKey.named("Freelook Keybinding"),
                zoomLevel.named("Zoom Level"),
                zoomInDuration.named("Zoom In Duration"),
                zoomOutDuration.named("Zoom Out Duration"),
                curve.named("Curve"));
    }

    @Override
    public void tick() {
        // Only update freelook state — do NOT touch animation here
        if (toggle.value) {
            if (freelookKey.wasKeyPressed()) {
                if (isFreeLooking)
                    stopFreelook();
                else
                    startFreelook();
            }
        } else if (freelookKey.isKeyPressed()) {
            if (!isFreeLooking)
                startFreelook();
        } else if (isFreeLooking) {
            stopFreelook();
        }
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
        if (!e && isFreeLooking)
            stopFreelook();
    }

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    private void startFreelook() {
        RenderFeature render = Providers.feature.render();
        wasFirstPerson = render.isFirstPerson();
        render.setThirdPersonBack();
        isFreeLooking = true;
        animationStartTime = System.currentTimeMillis();
    }

    private void stopFreelook() {
        if (wasFirstPerson) {
            Providers.feature.render().setFirstPerson();
        }
        isFreeLooking = false;
        animationStartTime = System.currentTimeMillis();
    }

    /** Returns true if we should apply zoom out effect */
    public static boolean shouldZoomOut() {
        return enabled.value && (isFreeLooking || getProgress() > 0f);
    }

    /** Returns the current zoom divisor (1 = normal, >1 = zoomed out) */
    public static float getZoomLevel() {
        float progress = getProgress();

        // Apply curve
        Function<Double, Double> curveFn = Animation.getCurve(curve.value);
        double curved = curveFn.apply((double) progress);

        // Map 0 → zoomLevel, 1 → normal FOV
        return zoomLevel.value - (float) curved * (zoomLevel.value - 1f);
    }

    /** Returns animation progress 0 → 1 based on system time */
    private static float getProgress() {
        boolean target = isFreeLooking;
        int duration = target ? zoomInDuration.value : zoomOutDuration.value;

        long now = System.currentTimeMillis();
        float rawProgress = (now - animationStartTime) / (float) duration;

        if (target) {
            return clamp(rawProgress, 0f, 1f);
        } else {
            // Reverse for zoom out
            return clamp(1f - rawProgress, 0f, 1f);
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}