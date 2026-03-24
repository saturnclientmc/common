package org.saturnclient.mod.mods;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.KeybindingProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.common.feature.RenderFeature;
import org.saturnclient.common.provider.GLFWProvider;

/**
 * FreelookMod lets the player look around freely (in third-person)
 * without changing the direction their character walks.
 *
 * Camera perspective is controlled through {@link RenderFeature} so
 * that the feature itself contains no direct Minecraft API calls.
 */
public class FreelookMod extends Mod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final BoolProperty toggle = Property.bool(true);
    public static final KeybindingProperty freelookKey = Property.keybinding(GLFWProvider.GLFW_KEY_H);

    /** Whether freelook is currently active (read by the camera mixin). */
    public static boolean isFreeLooking = false;
    private static boolean wasFirstPerson = false;

    public FreelookMod() {
        super(
                new ModSpec("Freelook", "freelook")
                        .description("Look around freely without moving your character")
                        .tags("Movement", "Camera")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                toggle.named("Toggle freelook"),
                freelookKey.named("Freelook Keybinding"));
    }

    // ---------------------------------------------------------------
    // Mod contract
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
        RenderFeature render = Providers.feature.render();
        wasFirstPerson = render.isFirstPerson();
        render.setThirdPersonBack();
        isFreeLooking = true;
    }

    private void stopFreelook() {
        if (wasFirstPerson) {
            Providers.feature.render().setFirstPerson();
        }
        isFreeLooking = false;
    }
}
