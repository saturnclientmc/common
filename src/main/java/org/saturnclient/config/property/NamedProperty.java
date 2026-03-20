package org.saturnclient.config.property;

public class NamedProperty {
    public String name;
    public Property prop;

    public NamedProperty(String name, Property value) {
        this.name = name;
        this.prop = value;
    }
}