package org.saturnclient.mod.mods;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.config.property.SelectProperty;
import org.saturnclient.mod.HudMod;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModLayout;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Fonts;

/**
 * HealthDisplayMod renders the player's current health as a HUD
 * element, with configurable display modes (raw health vs. hearts) and
 * decimal precision.
 */
public class HealthDisplayMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final SelectProperty displayMode = Property.select(0, "Health", "Hearts");
    private static final SelectProperty decimals = Property.select(0, "0", "1", "2");
    private static final ModLayout layout = new ModLayout(40, 18);

    public HealthDisplayMod() {
        super(
                new ModSpec("Health Display", "health")
                        .description("Displays your current health")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                displayMode.named("Display mode"),
                decimals.named("Decimals"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        float health = Providers.feature.player().getHealth();
        if (displayMode.value == 1)
            health /= 2f; // convert to hearts
        renderHealth(health, scope);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderHealth(10f, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderHealth(float health, RenderScope scope) {
        String fmt = switch (decimals.value) {
            case 1 -> "%.1f ";
            case 2 -> "%.2f ";
            default -> "%.0f ";
        };

        String text = String.format(fmt, health);
        scope.drawText(text, 0, 0, layout.font.value, layout.fgColor.value);

        layout.width = Fonts.getWidth(text, layout.font.value);
        layout.height = 18 * text.split("\n").length;
    }

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public ModLayout getDimensions() {
        return layout;
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
