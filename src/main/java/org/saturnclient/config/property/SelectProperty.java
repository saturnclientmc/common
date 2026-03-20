package org.saturnclient.config.property;

public class SelectProperty extends Property {
    public int value;
    public final int defaultValue;
    public final String[] availableValues;

    public SelectProperty(int defaultValue, String... availableValues) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.availableValues = availableValues;
    }

    public void next() {
        int i = (Integer) value;
        this.value = (i < availableValues.length - 1) ? i + 1 : 0;
    }

    public void prev() {
        int i = (Integer) value;
        this.value = (i > 0) ? i - 1 : availableValues.length - 1;
    }

    public void setSelection(int selection) {
        if (selection >= 0 && selection < availableValues.length) {
            this.value = selection;
        }
    }

    public String getSelection() {
        return availableValues[(Integer) value];
    }
}
