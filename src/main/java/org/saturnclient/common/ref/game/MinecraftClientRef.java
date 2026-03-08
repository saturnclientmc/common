package org.saturnclient.common.ref.game;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.render.WindowRef;
import org.saturnclient.ui.SaturnScreen;

public interface MinecraftClientRef {
    public static enum MinecraftScreen {
        SelectWorld,
        Multiplayer,
        Options
    }

    public File getRunDirectory();

    public InputStream getResource(IdentifierRef identifier);

    public WindowRef getWindow();

    public void setScreen(SaturnScreen screen);

    public void setScreen(MinecraftScreen screen);

    public String getAccessToken();

    public UUID getUuid();

    public String getUsername();

    public void onClientStopping(Runnable handler);

    public void scheduleStop();
}
