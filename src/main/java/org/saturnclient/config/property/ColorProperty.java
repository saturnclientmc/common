package org.saturnclient.config.property;

public class ColorProperty extends Property {
    public int value;
    public final int defaultValue;

    public ColorProperty(int defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public static int parseHexToInt(String hex) {

        hex = hex.replace("#", "");

        if (hex.length() == 6)
            hex = "FF" + hex;

        if (hex.length() != 8)
            throw new IllegalArgumentException(
                    "Hex must be 6 or 8 chars long, got '" + hex + "'");

        return (int) Long.parseLong(hex, 16);
    }
}
