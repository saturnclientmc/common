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
 * HealthDisplayFeature renders the player's current health as a HUD
 * element, with configurable display modes (raw health vs. hearts) and
 * decimal precision.
 */
public class HealthDisplayFeature extends Feature implements HudFeature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Integer> displayMode = Property.select(0, "Health", "Hearts");
    private static final Property<Integer> decimals = Property.select(0, "0", "1", "2");
    private static final FeatureLayout layout = new FeatureLayout(40, 18);

    private final ModuleProvider modules;

    public HealthDisplayFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Health Display", "health")
                        .description("Displays your current health")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                displayMode.named("Display mode"),
                decimals.named("Decimals"),
                layout.prop());

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // HudFeature
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        float health = modules.player().getHealth();
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
