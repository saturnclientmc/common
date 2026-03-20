package org.saturnclient.config.property;

public class ColorProperty extends Property {
    public final int value;
    public final int defaultValue;

    public ColorProperty(int defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
