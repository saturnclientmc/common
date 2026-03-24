package org.saturnclient.mod.mods;

import org.saturnclient.common.feature.NetworkFeature;
import org.saturnclient.common.feature.PlayerFeature;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;

/**
 * AutoSprintMod automatically enables sprinting whenever the
 * player moves forward and no sprint-blocking condition is active.
 */
public class AutoSprintMod extends Mod {

    private static final BoolProperty enabled = Property.bool(false);

    public AutoSprintMod() {
        super(
                new ModSpec("Auto Sprint", "sprint")
                        .description("Makes the player always sprint")
                        .tags("Movement")
                        .version("v0.2.0"),
                enabled.named("Enabled"));
    }

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
        PlayerFeature player = Providers.feature.player();
        NetworkFeature network = Providers.feature.network();

        if (!player.hasPlayer() || !network.hasNetwork()) {
            return;
        }

        if (player.isForwardPressed()
                && !player.isBackPressed()
                && !player.isSneaking()
                && !player.hasHorizontalCollision()
                && !player.isUsingItem()) {

            player.setSprinting(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
    }
}
