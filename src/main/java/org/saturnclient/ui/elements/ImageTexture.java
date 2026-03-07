package org.saturnclient.ui.elements;

import org.saturnclient.common.MinecraftProvider;
import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.game.MinecraftClientRef;
import org.saturnclient.ui.Element;
import org.saturnclient.ui.ElementContext;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.SvgTexture;

public class ImageTexture extends Element {
    IdentifierRef sprite;

    public ImageTexture(IdentifierRef sprite) {
        this.sprite = sprite;
    }

    @Override
    public void render(RenderScope renderScope, ElementContext ctx) {
        if (sprite.toString().endsWith(".svg")) {
            MinecraftClientRef client = MinecraftProvider.PROVIDER.getClient();

            int renderWidth = (int) (width * client.getWindow().getFramebufferWidth() / client.getWindow().getWidth());
            int renderHeight = (int) (height * client.getWindow().getFramebufferHeight()
                    / client.getWindow().getHeight());

            IdentifierRef pngId = SvgTexture.getSvg(client, sprite, renderWidth * 2, renderHeight * 2);

            this.sprite = pngId;
            if (pngId == null) {
                System.err.println("Failed to render SVG: " + sprite);
                return;
            }
        }

        renderScope.drawTexture(this.sprite, 0, 0, 0, 0, width, height);
    }
}
