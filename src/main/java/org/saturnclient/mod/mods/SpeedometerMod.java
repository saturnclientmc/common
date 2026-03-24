package org.saturnclient.mod.mods;

import org.saturnclient.common.feature.PlayerFeature;
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
 * SpeedometerMod displays the player's current movement speed as
 * a HUD element. Velocity data comes from {@link PlayerFeature}.
 */
public class SpeedometerMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final SelectProperty axis = Property.select(1, "Absolute", "Horizontal", "Vertical");
    private static final SelectProperty unitText = Property.select(0, "None", "Blocks/s", "blocks/s", "b/s");
    private static final BoolProperty showLabel = Property.bool(true);
    private static final ModLayout layout = new ModLayout(40, 18);

    private double cachedSpeed = 0.0;

    public SpeedometerMod() {
        super(
                new ModSpec("Speedometer", "speed")
                        .description("Displays your current movement speed")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                axis.named("Speed type"),
                unitText.named("Unit text"),
                showLabel.named("Show speed label"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        PlayerFeature player = Providers.feature.player();
        PlayerFeature.Velocity v = player.getVelocity();
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
