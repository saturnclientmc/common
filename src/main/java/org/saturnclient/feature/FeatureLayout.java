package org.saturnclient.feature;

import java.util.Map;

import org.saturnclient.config.property.Property;
import org.saturnclient.config.property.Property.NamedProperty;

public class FeatureLayout {
    public Property<Integer> x = Property.integer(0);
    public Property<Integer> y = Property.integer(0);
    public Property<Float> scale = Property.floatProp(1.0f);
    public Property<Integer> bgColor = Property.color(0x00000000);
    public Property<Integer> fgColor = Property.color(0xFFffffff);
    public Property<Integer> radius = Property.integer(0);
    public Property<Integer> font = Property.font(0);
    public boolean renderBackground = true;

    public int width = 0;
    public int height = 0;

    public NamedProperty<Map<String, Property<?>>> prop() {
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
