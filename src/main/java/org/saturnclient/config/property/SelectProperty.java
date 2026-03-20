package org.saturnclient.config.property;

import org.saturnclient.config.property.Property.PropertyType;

public class SelectProperty extends Property {
    public final int value;
    public final int defaultValue;
    public final String[] availableValues;

    public SelectProperty(int defaultValue, String... availableValues) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.availableValues = availableValues;
    }

    public void next() {
        int i = (Integer) value;
        setValue((i < availableValues.length - 1) ? i + 1 : 0);
    }

    public void prev() {
        if (type == PropertyType.SELECT) {
            int i = (Integer) value;
            setValue((i > 0) ? i - 1 : availableValues.length - 1);
        }
    }

    public void setSelection(int selection) {
        if (type == PropertyType.SELECT && selection >= 0 && selection < availableValues.length) {
            setValue(selection);
        }
    }

    public String getSelection() {
        return (type == PropertyType.SELECT) ? availableValues[(Integer) value] : null;
    }
}
