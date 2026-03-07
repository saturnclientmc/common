package org.saturnclient.common;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.game.MinecraftClientRef;
import org.saturnclient.ui.SaturnScreen;

import java.awt.image.BufferedImage;

public abstract class MinecraftProvider {
    public static MinecraftProvider PROVIDER;

    public static enum MinecraftScreen {
        SelectWorld,
        Multiplayer,
        Options
    }

    public abstract MinecraftClientRef getClient();

    public abstract void setScreen(SaturnScreen screen);

    public abstract void setScreen(MinecraftScreen screen);

    public abstract Object createIdentifier(String namespace, String path);

    public abstract boolean isKeyPressed(int key);

    public abstract int getWidth(String text, int font);

    public abstract void registerBufferedImageTexture(IdentifierRef i, BufferedImage bi);

    public abstract String getKeyName(int key);

    public abstract void stop();
}
