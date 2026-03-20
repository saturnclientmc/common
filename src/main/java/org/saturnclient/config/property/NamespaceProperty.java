package org.saturnclient.config.property;

import java.util.Map;

public class NamespaceProperty extends Property {
    public final Map<String, Property> value;

    public NamespaceProperty(Map<String, Property> value) {
        this.value = value;
    }

    public Map<String, Property> getNamespaceMap() {
        return this.value;
    }
}
