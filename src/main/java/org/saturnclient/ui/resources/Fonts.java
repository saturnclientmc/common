package org.saturnclient.ui.resources;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.render.TextRef;

public class Fonts {
    public static final IdentifierRef INTER = IdentifierRef.ofSaturn("inter");
    public static final IdentifierRef INTER_BOLD = IdentifierRef.ofSaturn("inter_bold");
    public static final IdentifierRef DEFAULT = IdentifierRef.ofVanilla("default");

    public static IdentifierRef getFont(int font) {
        switch (font) {
            case 0:
                return DEFAULT;
            case 1:
                return INTER;
            default:
                return INTER_BOLD;
        }
    }

    public static TextRef setFont(String text, IdentifierRef font) {
        return TextRef.literal(text).withFont(font);
    }

    public static TextRef setFont(String text, int font) {
        return TextRef.literal(text).withFont(getFont(font));
    }

    public static int getWidth(String text, int font) {
        int w = 0;
        for (String line : text.split("\n")) {
            w = Math.max(w, setFont(line, font).getWidth());
        }

        if (font == 0) {
            w *= 2;
        }

        return w;
    }

    public static int getHeight() {
        return 18;
    }

    public static int centerX(int w, String text, int font) {
        return (w - Fonts.getWidth(text, font)) / 2;
    }

    public static int centerY(int h) {
        return (h - 18) / 2;
    }
}
