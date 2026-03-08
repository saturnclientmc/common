package org.saturnclient.feature.features;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.common.module.PlayerModule;
import org.saturnclient.config.manager.Property;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Fonts;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;

/**
 * SpeedometerFeature displays the player's current movement speed as
 * a HUD element. Velocity data comes from {@link PlayerModule}.
 */
public class SpeedometerFeature extends Feature implements HudFeature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Integer> axis = Property.select(1, "Absolute", "Horizontal", "Vertical");
    private static final Property<Integer> unitText = Property.select(0, "None", "Blocks/s", "blocks/s", "b/s");
    private static final Property<Boolean> showLabel = Property.bool(true);
    private static final FeatureLayout layout = new FeatureLayout(40, 18);

    private double cachedSpeed = 0.0;

    private final ModuleProvider modules;

    public SpeedometerFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Speedometer", "speed")
                        .description("Displays your current movement speed")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                axis.named("Speed type"),
                unitText.named("Unit text"),
                showLabel.named("Show speed label"),
                layout.prop());

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // HudFeature
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        PlayerModule player = modules.player();
        PlayerModule.Velocity v = player.getVelocity();
        boolean onGround = player.isOnGround();

        double effectiveY = onGround ? 0.0 : v.y;

        cachedSpeed = switch (axis.value) {
            case 0 -> Math.sqrt(v.x * v.x + effectiveY * effectiveY + v.z * v.z); // absolute 3-D
            case 2 -> Math.abs(effectiveY); // vertical only
            default -> Math.sqrt(v.x * v.x + v.z * v.z); // horizontal
        };

        renderSpeed(cachedSpeed * 20.0, scope); // blocks/tick → blocks/s
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderSpeed(2.71, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderSpeed(double bps, RenderScope scope) {
        String text = String.format("%.2f ", bps);

        text += switch (unitText.value) {
            case 1 -> "Blocks/s";
            case 2 -> "blocks/s";
            case 3 -> "b/s";
            default -> "";
        };

        if (showLabel.value) {
            text = "Speed: " + text;
        }

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
