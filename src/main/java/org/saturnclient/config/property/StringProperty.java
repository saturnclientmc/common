package org.saturnclient.config.property;

public class StringProperty {
    public final String value;
    public final String defaultValue;

    public StringProperty(String defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }
}
