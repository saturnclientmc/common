package org.saturnclient.feature.features;

import java.util.List;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.common.ref.asset.SpriteRef;
import org.saturnclient.config.manager.Property;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;

/**
 * StatusEffectsFeature displays the player's active potion effects as
 * a HUD overlay.
 *
 * The effect list is supplied by the platform through an interface
 * returned by a future {@code PlayerModule#getActiveEffects()} call.
 * Until that is wired up, the existing {@link StatusEffectsInterface}
 * bridge is used, accessed via {@link ModuleProvider}.
 */
public class StatusEffectsFeature extends Feature implements HudFeature {

    public static final Property<Boolean> enabled = Property.bool(false);
    private static final FeatureLayout layout = new FeatureLayout(60, 0);

    private final ModuleProvider modules;

    public StatusEffectsFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Status Effects", "effect")
                        .description("Displays active status effects")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                layout.prop());

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // Effect data contract (inner interface keeps things self-contained)
    // ---------------------------------------------------------------

    /**
     * Minimal view of a single active status effect.
     * Implementations are provided by the platform bridge.
     */
    public interface EffectView {
        boolean shouldShowIcon();

        SpriteRef getIcon();

        boolean isInfinite();

        String getInfiniteText();

        int getDurationSeconds();
    }

    // ---------------------------------------------------------------
    // HudFeature
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderEffects(scope, modules.player().getActiveEffects());
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderEffects(scope, modules.player().getDummyEffects());
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderEffects(RenderScope scope, List<? extends EffectView> effects) {
        int row = 0;

        for (EffectView effect : effects) {
            if (!effect.shouldShowIcon())
                continue;

            scope.drawSpriteStretched(effect.getIcon(), 0, 18 * row, 16, 16);
            scope.drawText(0.5f, durationString(effect),
                    18, 18 * row + 3,
                    layout.font.value, -1);
            row++;
        }

        layout.height = 18 * row;
    }

    private static String durationString(EffectView effect) {
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
