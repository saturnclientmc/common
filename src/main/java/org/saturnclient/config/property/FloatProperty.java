package org.saturnclient.config.property;

public class FloatProperty extends Property {
    public float value;
    public final float defaultValue;

    public FloatProperty(float defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
