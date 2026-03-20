package org.saturnclient.feature.features;

import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
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

    private static final BoolProperty enabled = Property.bool(false);

    public NoFogFeature() {
        super(
                new FeatureDetails("No Fog", "nofog")
                        .description("Stops fog from rendering")
                        .tags("Camera")
                        .version("v0.2.0"),
                enabled.named("Enabled"));
    }

    /** Returns {@code true} when fog should be suppressed. */
    public static boolean isActive() {
        return enabled.value;
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
