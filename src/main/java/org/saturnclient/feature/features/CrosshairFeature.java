package org.saturnclient.feature.features;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.Property;
import org.saturnclient.common.module.RenderModule;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Textures;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * CrosshairFeature renders an alternative crosshair texture when the
 * player is aiming at a living entity.
 */
public class CrosshairFeature extends Feature {

    public static final Property<Boolean> enabled = Property.bool(false);

    private static final int CROSSHAIR_SIZE = 15;

    public CrosshairFeature() {
        super(
                new FeatureDetails("Crosshair", "crosshair")
                        .description("Changes the crosshair when aiming at an entity")
                        .tags("Visuals", "Utility")
                        .version("v0.2.0"),
                enabled.named("Enabled"));
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public void render(RenderScope scope) {
        if (!enabled.value || !Providers.module.entity().isTargetingLivingEntity()) {
            return;
        }

        RenderModule render = Providers.module.render();
        int x = (render.getScaledWindowWidth() - CROSSHAIR_SIZE) / 2;
        int y = (render.getScaledWindowHeight() - CROSSHAIR_SIZE) / 2;

        scope.drawTexture(
                Textures.CROSSHAIR_RANGE,
                x, y,
                0.0F, 0.0F,
                CROSSHAIR_SIZE, CROSSHAIR_SIZE);
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
