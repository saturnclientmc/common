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

    // --- Properties ---
    private static final BoolProperty enabled = Property.bool(false);
    private static final BoolProperty toggle = Property.bool(true);
    private static final KeybindingProperty freelookKey = Property.keybinding(GLFWProvider.GLFW_KEY_H);

    public static final FloatProperty zoomLevel = Property.floatProp(2.0f);
    public static final IntProperty zoomInDuration = Property.integer(500);
    public static final IntProperty zoomOutDuration = Property.integer(300);
    public static final SelectProperty curve = Property.select(1, "Ease In Out Cubic", "Ease Out Cubic", "Ease In Out Back");

    // --- State Variables ---
    private static boolean isFreeLooking = false;
    private static boolean targetFreeLook = false;
    private static boolean wasFirstPerson = false;

    // --- Animation State ---
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

    // --- Lifecycle Methods ---

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
        if (!e && isFreeLooking) {
            stopFreelook();
        }
    }

    @Override
    public void tick() {
        handleInput();

        // If we are returning to normal and animation finished, snap back to 1st person
        if (!targetFreeLook && wasFirstPerson && getProgress() <= 0.0f) {
            Providers.feature.render().setFirstPerson();
            wasFirstPerson = false;
        }
    }

    // --- Internal Logic ---

    private void handleInput() {
        if (toggle.value) {
            if (freelookKey.wasKeyPressed()) {
                targetFreeLook = !targetFreeLook;
                toggleFreelookState(targetFreeLook);
            }
        } else {
            boolean isDown = freelookKey.isKeyPressed();
            if (isDown != targetFreeLook) { // state changed
                targetFreeLook = isDown;
                toggleFreelookState(targetFreeLook);
            }
        }
    }

    private void toggleFreelookState(boolean active) {
        if (active) {
            startFreelook();
        } else {
            stopFreelook();
        }
    }

    private void startFreelook() {
        RenderFeature render = Providers.feature.render();
        wasFirstPerson = render.isFirstPerson();
        render.setThirdPersonBack();
        isFreeLooking = true;
        animationStartTime = System.currentTimeMillis();
    }

    private void stopFreelook() {
        if (isFreeLooking) {
            isFreeLooking = false;
            animationStartTime = System.currentTimeMillis();
        }
    }

    // --- Math Utilities ---

    private static float getProgress() {
        if (!wasFirstPerson) return 0.0f;

        int duration = isFreeLooking ? zoomInDuration.value : zoomOutDuration.value;
        long now = System.currentTimeMillis();
        float rawProgress = (now - animationStartTime) / (float) duration;

        return isFreeLooking ? clamp(rawProgress, 0.0f, 1.0f) : clamp(1.0f - rawProgress, 0.0f, 1.0f);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    // --- Public API / Getters for External Hooks ---

    public static boolean isFreeLooking() {
        return isFreeLooking || getProgress() > 0;
    }

    public static boolean shouldZoomOut() {
        return enabled.value && wasFirstPerson && isFreeLooking();
    }

    public static float getZoomLevel() {
        float progress = getProgress();

        Function<Double, Double> curveFn = Animation.getCurve(curve.value);
        double curved = curveFn.apply((double) progress);

        return zoomLevel.value - (float) curved * (zoomLevel.value - 1.0f);
    }
}