package org.saturnclient.config.property;

public class BoolProperty extends Property {
    public final boolean value;
    public final boolean defaultValue;

    public BoolProperty(boolean defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
