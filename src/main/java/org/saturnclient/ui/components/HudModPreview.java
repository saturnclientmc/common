package org.saturnclient.ui.components;

import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;
import org.saturnclient.ui.Element;
import org.saturnclient.ui.ElementContext;
import org.saturnclient.ui.RenderScope;

public class HudModPreview extends Element {
    HudFeature feature;

    public HudModPreview(HudFeature feature) {
        this.feature = feature;
    }

    @Override
    public void render(RenderScope scope, ElementContext ctx) {
        FeatureLayout layout = feature.getDimensions();

        if (layout.renderBackground) {
            scope.drawRoundedRectangle(0, 0, layout.width, layout.height,
                    layout.radius.value, layout.bgColor.value);
        }

        feature.renderDummy(scope);
    }
}
