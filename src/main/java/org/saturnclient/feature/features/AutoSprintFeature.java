package org.saturnclient.feature.features;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.common.module.NetworkModule;
import org.saturnclient.common.module.PlayerModule;
import org.saturnclient.config.manager.Property;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * AutoSprintFeature automatically enables sprinting whenever the
 * player moves forward and no sprint-blocking condition is active.
 */
public class AutoSprintFeature extends Feature {

    private static final Property<Boolean> enabled = Property.bool(false);

    private final ModuleProvider modules;

    public AutoSprintFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Auto Sprint", "sprint")
                        .description("Makes the player always sprint")
                        .tags("Movement")
                        .version("v0.2.0"),
                enabled.named("Enabled"));

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
        PlayerModule player = modules.player();
        NetworkModule network = modules.network();

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
