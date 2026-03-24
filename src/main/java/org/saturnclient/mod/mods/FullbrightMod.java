package org.saturnclient.mod.mods;

import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.IntProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;

/**
 * FullbrightMod overrides the game's gamma/brightness to let the
 * player see in complete darkness.
 *
 * Actual brightness injection happens in a mixin; this class owns only
 * the configuration and the two static query methods the mixin calls.
 *
 * No FeatureProvider is needed — this feature has no per-tick engine
 * interaction; the mixin polls the static helpers directly.
 */
public class FullbrightMod extends Mod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final IntProperty brightness = Property.integer(100);

    public FullbrightMod() {
        super(
                new ModSpec("Fullbright", "fullbright")
                        .description("Allows you to see in the dark")
                        .tags("Camera")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                brightness.named("Brightness %"));
    }

    // ---------------------------------------------------------------
    // Mixin query API (static so the mixin can call without an instance)
    // ---------------------------------------------------------------

    /** Returns {@code true} when the brightness override should be applied. */
    public static boolean shouldOverrideBrightness() {
        return enabled.value;
    }

    /**
     * Returns the brightness multiplier to apply.
     * The property stores a percentage (0–100+); the mixin expects a
     * normalised float where 1.0 ≈ 100 %.
     */
    public static float getBrightnessValue() {
        return brightness.value / 10.0f;
    }

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
    }
}
