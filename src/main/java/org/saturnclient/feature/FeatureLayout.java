package org.saturnclient.feature;

import java.util.Map;

import org.saturnclient.config.property.Property;
import org.saturnclient.config.property.SelectProperty;
import org.saturnclient.config.property.ColorProperty;
import org.saturnclient.config.property.FloatProperty;
import org.saturnclient.config.property.IntProperty;
import org.saturnclient.config.property.NamedProperty;

public class FeatureLayout {
    public IntProperty x = Property.integer(0);
    public IntProperty y = Property.integer(0);
    public FloatProperty scale = Property.floatProp(1.0f);
    public ColorProperty bgColor = Property.color(0x00000000);
    public ColorProperty fgColor = Property.color(0xFFffffff);
    public IntProperty radius = Property.integer(0);
    public SelectProperty font = Property.font(0);
    public boolean renderBackground = true;

    public int width = 0;
    public int height = 0;

    public NamedProperty prop() {
        return Property.namespace(Map.of(
                "X", x,
                "Y", y,
                "Scale", scale,
                "Background Color", bgColor,
                "Foreground Color", fgColor,
                "Corner Radius", radius,
                "Font", font)).named("In-Game Display");
    }

    public FeatureLayout() {
    }

    public FeatureLayout(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public FeatureLayout(int x, int y, int width, int height) {
        this.x.value = x;
        this.y.value = y;
        this.width = width;
        this.height = height;
    }

    public FeatureLayout(int x, int y, int width, int height, float scale) {
        this.x.value = x;
        this.y.value = y;
        this.scale.value = scale;
        this.width = width;
        this.height = height;
    }
}
