package org.saturnclient.feature.features;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.Property;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Fonts;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;

/**
 * FpsFeature displays the current frames-per-second as a HUD element.
 * FPS is obtained from {@link RenderModule} rather than cached in the feature.
 */
public class FpsFeature extends Feature implements HudFeature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final FeatureLayout layout = new FeatureLayout(60, Fonts.getHeight());

    public FpsFeature() {
        super(
                new FeatureDetails("FPS Display", "fps")
                        .description("Displays current FPS")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudFeature
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderFps(Providers.module.render().getFps(), scope);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderFps(369, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderFps(int fps, RenderScope scope) {
        String text = fps + " FPS";
        scope.drawText(text, 0, 0, layout.font.value, layout.fgColor.value);
        layout.width = Fonts.getWidth(text, layout.font.value);
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
