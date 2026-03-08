package org.saturnclient.feature.features;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.common.module.RenderModule;
import org.saturnclient.common.provider.GLFWProvider;
import org.saturnclient.config.manager.Property;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * FreelookFeature lets the player look around freely (in third-person)
 * without changing the direction their character walks.
 *
 * Camera perspective is controlled through {@link RenderModule} so
 * that the feature itself contains no direct Minecraft API calls.
 */
public class FreelookFeature extends Feature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Boolean> toggle = Property.bool(true);
    public static final Property<Integer> freelookKey = Property.keybinding(GLFWProvider.GLFW_KEY_H);

    /** Whether freelook is currently active (read by the camera mixin). */
    public static boolean isFreeLooking = false;
    private static boolean wasFirstPerson = false;

    private final ModuleProvider modules;

    public FreelookFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Freelook", "freelook")
                        .description("Look around freely without moving your character")
                        .tags("Movement", "Camera")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                toggle.named("Toggle freelook"),
                freelookKey.named("Freelook Keybinding"));

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
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

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void startFreelook() {
        RenderModule render = modules.render();
        wasFirstPerson = render.isFirstPerson();
        render.setThirdPersonBack();
        isFreeLooking = true;
    }

    private void stopFreelook() {
        if (wasFirstPerson) {
            modules.render().setFirstPerson();
        }
        isFreeLooking = false;
    }
}
