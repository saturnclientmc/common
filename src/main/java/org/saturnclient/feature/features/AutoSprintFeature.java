package org.saturnclient.feature.features;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.Property;
import org.saturnclient.common.module.NetworkModule;
import org.saturnclient.common.module.PlayerModule;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * AutoSprintFeature automatically enables sprinting whenever the
 * player moves forward and no sprint-blocking condition is active.
 */
public class AutoSprintFeature extends Feature {

    private static final Property<Boolean> enabled = Property.bool(false);

    public AutoSprintFeature() {
        super(
                new FeatureDetails("Auto Sprint", "sprint")
                        .description("Makes the player always sprint")
                        .tags("Movement")
                        .version("v0.2.0"),
                enabled.named("Enabled"));
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
        PlayerModule player = Providers.module.player();
        NetworkModule network = Providers.module.network();

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
