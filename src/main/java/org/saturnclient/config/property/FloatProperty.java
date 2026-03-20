package org.saturnclient.config.property;

public class FloatProperty {
    public final float value;
    public final float defaultValue;

    public FloatProperty(float defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
