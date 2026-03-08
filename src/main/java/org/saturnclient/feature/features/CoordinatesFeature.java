package org.saturnclient.feature.features;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.config.manager.Property;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Fonts;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;

/**
 * CoordinatesFeature displays the player's current XYZ position as a
 * HUD element with multiple configurable display formats.
 */
public class CoordinatesFeature extends Feature implements HudFeature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Integer> displayMethod = Property.select(0,
            "Flat",
            "Flat annotated",
            "Newline",
            "Newline annotated");
    private static final FeatureLayout layout = new FeatureLayout(40, 18);

    private final ModuleProvider modules;

    public CoordinatesFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Coordinates", "coords")
                        .description("Displays your current coordinates")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                displayMethod.named("Display Method"),
                layout.prop());

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // HudFeature
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderCoords(
                modules.player().getX(),
                modules.player().getY(),
                modules.player().getZ(),
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
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public FeatureLayout getDimensions() {
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
