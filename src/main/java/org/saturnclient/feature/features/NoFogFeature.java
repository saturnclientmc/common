package org.saturnclient.feature.features;

import org.saturnclient.config.manager.Property;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * NoFogFeature suppresses distance fog and (optionally) liquid fog.
 *
 * Like {@link FullbrightFeature}, the actual suppression is applied
 * inside a mixin. This class owns the configuration and provides
 * static query methods the mixin calls.
 */
public class NoFogFeature extends Feature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Boolean> liquids = Property.bool(false);

    public NoFogFeature() {
        super(
                new FeatureDetails("No Fog", "nofog")
                        .description("Stops fog from rendering")
                        .tags("Camera")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                liquids.named("See through liquids"));
    }

    // ---------------------------------------------------------------
    // Mixin query API
    // ---------------------------------------------------------------

    /** Returns {@code true} when fog should be suppressed. */
    public static boolean isActive() {
        return enabled.value;
    }

    /** Returns {@code true} when liquid-fog suppression is also active. */
    public static boolean liquids() {
        return liquids.value;
    }

    // ---------------------------------------------------------------
    // Feature contract
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
