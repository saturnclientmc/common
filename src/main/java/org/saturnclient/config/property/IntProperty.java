package org.saturnclient.config.property;

public class IntProperty extends Property {
    public int value;
    public final int defaultValue;

    public IntProperty(int defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
