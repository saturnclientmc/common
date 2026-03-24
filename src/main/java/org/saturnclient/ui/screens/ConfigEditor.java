package org.saturnclient.ui.screens;

import java.util.Map;

import org.saturnclient.config.ConfigManager;
import org.saturnclient.config.Theme;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.ColorProperty;
import org.saturnclient.config.property.FloatProperty;
import org.saturnclient.config.property.IntProperty;
import org.saturnclient.config.property.KeybindingProperty;
import org.saturnclient.config.property.NamespaceProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.config.property.SelectProperty;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.SaturnScreen;
import org.saturnclient.ui.components.HudModPreview;
import org.saturnclient.ui.components.Sidebar;
import org.saturnclient.ui.components.inputs.FloatInput;
import org.saturnclient.ui.components.inputs.ColorInput;
import org.saturnclient.ui.components.inputs.IntInput;
import org.saturnclient.ui.components.inputs.KeybindingSelector;
import org.saturnclient.ui.components.inputs.Select;
import org.saturnclient.ui.components.inputs.Toggle;
import org.saturnclient.ui.elements.Scroll;
import org.saturnclient.ui.elements.Text;
import org.saturnclient.ui.resources.Fonts;

public class ConfigEditor extends SaturnScreen {
    private ConfigManager config;
    private HudFeature hudFeature;
    private HudModPreview preview;

    public ConfigEditor(ConfigManager config) {
        super("Config Editor");

        this.config = config;
    }

    public ConfigEditor(ConfigManager config, HudFeature hudFeature) {
        super("Config Editor");

        this.config = config;
        this.hudFeature = hudFeature;
    }

    @Override
    public void ui() {
        int p = 10;
        int g = 10;

        Scroll scroll = new Scroll(p);

        drawProperties(scroll, config.getProperties(), 0, 0, 480 + 10 + (g * 2));

        int scrollWidth = 480 + 10 + (g * 2) + (p * 2);

        if (this.hudFeature != null) {
            this.preview = new HudModPreview(this.hudFeature);
        }

        draw(scroll.dimensions(scrollWidth, 350).center(width, height));

        draw(new Sidebar(1, this.provider::close).center(width, height, -(scrollWidth / 2 + 20), 0));
    }

    private int drawProperties(Scroll configScroll, Map<String, Property> properties, int row, int col, int w) {
        for (Map.Entry<String, Property> propEntry : properties.entrySet()) {
            Property prop = propEntry.getValue();
            String propName = propEntry.getKey();
            boolean full = isFull(prop);

            // Always reset to col 0 if full-width component needs to be drawn mid-row
            if (full && col != 0) {
                col = 0;
                row++;
            }

            int modX = (w / 2) * col; // two-column layout
            int modY = 25 * row;

            if (prop instanceof NamespaceProperty) {
                configScroll.draw(
                        new Text(propName)
                                .position(Fonts.centerX(520, propName, Theme.FONT.value), modY + 1)
                                .scale(0.7f));
            } else {
                configScroll.draw(
                        new Text(propName)
                                .position(modX, modY + 1)
                                .scale(0.7f));
            }

            // instanceof dispatch
            if (prop instanceof BoolProperty p) {
                configScroll.draw(
                        new Toggle(p)
                                .position(modX + (w / 2) - 40, modY)
                                .scale(0.5f));
            }

            else if (prop instanceof ColorProperty p) {
                configScroll.draw(
                        new ColorInput(p)
                                .position(w / 2, modY));
            }

            else if (prop instanceof IntProperty p) {
                configScroll.draw(
                        new IntInput(p)
                                .position(w / 2, modY));
            }

            else if (prop instanceof FloatProperty p) {
                configScroll.draw(
                        new FloatInput(p)
                                .position(w / 2, modY));
            }

            else if (prop instanceof NamespaceProperty p) {
                Map<String, Property> nestedProperties = p.value;
                row = drawProperties(configScroll, nestedProperties, row + 1, col, w);
            }

            else if (prop instanceof SelectProperty p) {
                configScroll.draw(
                        new Select(p)
                                .position(w / 2, modY));
            }

            else if (prop instanceof KeybindingProperty p) {
                configScroll.draw(
                        new KeybindingSelector(p)
                                .position(w / 2, modY));
            }

            // STRING → intentionally no component (same as before)

            if (full) {
                row++; // full-width takes a whole row
                col = 0;
            } else {
                col++;
                if (col > 1) { // max 2 columns
                    col = 0;
                    row++;
                }
            }
        }

        return row;
    }

    public static boolean isFull(Property prop) {
        return !(prop instanceof BoolProperty);
    }

    @Override
    public void render(RenderScope renderScope, int mouseX, int mouseY, float delta, long elapsed) {
        if (this.preview != null) {
            FeatureLayout layout = hudFeature.getDimensions();
            draw(this.preview
                    .dimensions(layout.width, (int) (layout.height * layout.scale.value * 2.0f))
                    .scale(1.0f) // Because the centering won't work properly
                    .center(width, height, (530 / 2) + 40, 0)
                    .scale(layout.scale.value * 2.0f));
        }

        super.render(renderScope, mouseX, mouseY, delta, elapsed);
    }
}
