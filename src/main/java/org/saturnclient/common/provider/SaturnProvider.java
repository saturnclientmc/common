package org.saturnclient.common.provider;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.game.MinecraftClientRef;

import java.awt.image.BufferedImage;
import java.util.UUID;

public interface SaturnProvider {
    public MinecraftClientRef getClient();

    public void playEmote(UUID fromPlayerUuid, String emoteIdOrNull);

    public String getAccessToken();

    public UUID getUuid();

    public String getUsername();

    public void onClientStopping(Runnable handler);

    public void logInfo(String message);

    public void logError(String message);

    public void logError(String message, Throwable throwable);

    public void stop();

    public void registerBufferedImageTexture(IdentifierRef i, BufferedImage bi);
}
