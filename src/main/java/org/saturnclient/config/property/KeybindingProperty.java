package org.saturnclient.config.property;

import org.saturnclient.common.provider.Providers;

public class KeybindingProperty extends Property {
    public int value;
    public final int defaultValue;
    public boolean wasPressedLastTick = false;

    public KeybindingProperty(int defaultValue) {
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public boolean isKeyPressed() {

        return (Integer) value != -1 &&
                Providers.GLFW.isKeyPressed((Integer) value);
    }

    public boolean wasKeyPressed() {
        boolean pressed = isKeyPressed();

        boolean result = pressed && !wasPressedLastTick;

        wasPressedLastTick = pressed;

        return result;
    }

    // ---------- HEX ----------

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
