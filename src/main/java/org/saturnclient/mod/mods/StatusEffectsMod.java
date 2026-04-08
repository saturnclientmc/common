package org.saturnclient.mod.mods;

import java.util.List;

import org.saturnclient.common.provider.FeatureProvider;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.asset.SpriteRef;
import org.saturnclient.common.ref.game.EffectRef;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.HudMod;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModLayout;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.ui.RenderScope;

/**
 * StatusEffectsMod displays the player's active potion effects as
 * a HUD overlay.
 *
 * The effect list is supplied by the platform through an interface
 * returned by a future {@code PlayerFeature#getActiveEffects()} call.
 * Until that is wired up, the existing {@link StatusEffectsInterface}
 * bridge is used, accessed via {@link FeatureProvider}.
 */
public class StatusEffectsMod extends Mod implements HudMod {

    public static final BoolProperty enabled = Property.bool(false);
    private static final ModLayout layout = new ModLayout(60, 0);

    public StatusEffectsMod() {
        super(
                new ModSpec("Status Effects", "effect")
                        .description("Displays active status effects")
                        .version("v0.2.0")
                        .tags("Utility")
                        .requires(Providers.feature::player),
                enabled.named("Enabled"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderEffects(scope, Providers.feature.player().getActiveEffects());
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderEffects(scope, Providers.feature.player().getDummyEffects());
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderEffects(RenderScope scope, List<? extends EffectRef> effects) {
        int row = 0;

        for (EffectRef effect : effects) {
            if (!effect.shouldShowIcon())
                continue;

            SpriteRef sprite = effect.getIcon();
            if (sprite == null) {
                scope.drawTexture(effect.getIconId(), 0, 18 * row, 0, 0, 16, 16);
            } else {
                scope.drawSpriteStretched(sprite, 0, 18 * row, 16, 16);
            }
            scope.drawText(0.5f, durationString(effect),
                    18, 18 * row + 3,
                    layout.font.value, -1);
            row++;
        }

        layout.height = 18 * row;
    }

    private static String durationString(EffectRef effect) {
        if (effect.isInfinite())
            return effect.getInfiniteText();

        int totalSeconds = effect.getDurationSeconds();
        if (totalSeconds >= 3600) {
            return (totalSeconds / 3600) + "h";
        } else if (totalSeconds >= 60) {
            return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
        } else {
            return totalSeconds + "s";
        }
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
