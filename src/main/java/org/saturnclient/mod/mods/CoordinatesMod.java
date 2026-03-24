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
 * CoordinatesMod displays the player's current XYZ position as a
 * HUD element with multiple configurable display formats.
 */
public class CoordinatesMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final SelectProperty displayMethod = Property.select(0,
            "Flat",
            "Flat annotated",
            "Newline",
            "Newline annotated");
    private static final ModLayout layout = new ModLayout(40, 18);

    public CoordinatesMod() {
        super(
                new ModSpec("Coordinates", "coords")
                        .description("Displays your current coordinates")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                displayMethod.named("Display Method"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderCoords(
                Providers.feature.player().getX(),
                Providers.feature.player().getY(),
                Providers.feature.player().getZ(),
                scope);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderCoords(532, 69, 253, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderCoords(int x, int y, int z, RenderScope scope) {
        String fmt = switch (displayMethod.value) {
            case 1 -> "X: %d Y: %d Z: %d";
            case 2 -> "%d\n%d\n%d";
            case 3 -> "X: %d\nY: %d\nZ: %d";
            default -> "%d %d %d";
        };

        String text = String.format(fmt, x, y, z);
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
