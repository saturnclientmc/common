package org.saturnclient.mod.mods;

import org.saturnclient.common.feature.RenderFeature;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Textures;

/**
 * CrosshairMod renders an alternative crosshair texture when the
 * player is aiming at a living entity.
 */
public class CrosshairMod extends Mod {

    public static final BoolProperty enabled = Property.bool(false);

    private static final int CROSSHAIR_SIZE = 15;

    public CrosshairMod() {
        super(
                new ModSpec("Crosshair", "crosshair")
                        .description("Changes the crosshair when aiming at an entity")
                        .tags("Visuals", "Utility")
                        .version("v0.2.0"),
                enabled.named("Enabled"));
    }

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public void render(RenderScope scope) {
        if (!enabled.value || !Providers.feature.entity().isTargetingLivingEntity()) {
            return;
        }

        RenderFeature render = Providers.feature.render();
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
