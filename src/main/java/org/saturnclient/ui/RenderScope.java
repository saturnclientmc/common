package org.saturnclient.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.asset.SpriteRef;
import org.saturnclient.common.ref.game.ItemStackRef;
import org.saturnclient.common.ref.render.MatrixStackRef;
import org.saturnclient.ui.resources.SvgTexture;

public interface RenderScope {

    // ----------------------------
    // Matrix & Transform
    // ----------------------------
    MatrixStackRef getMatrixStack();

    // ----------------------------
    // Color & Opacity
    // ----------------------------
    void setOpacity(float alpha);

    int getColor(int color);

    // ----------------------------
    // Shapes
    // ----------------------------
    void drawRectangle(int x, int y, int width, int height, int color);

    default void drawRoundedRectangle(int x, int y, int width, int height, int radius, int color) {
        if (color == 0)
            return;

        radius = Math.min(radius, width / 2);
        radius = Math.min(radius, height / 2);

        String svg = String.format(
                "<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d'>" +
                        "<rect width='%d' height='%d' rx='%d' ry='%d' fill='white'/>" +
                        "</svg>",
                width, height,
                width, height,
                radius, radius);

        InputStream svgStream = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));

        drawTexture(SvgTexture.getSvg(svgStream,
                IdentifierRef.ofSaturn(
                        "textures/gui/tmp_rect/rounded_" + width + "_" + height + "_" + radius),
                width * 4, height * 4), x, y, 0, 0, width, height, color);
    }

    void drawBorder(int x, int y, int width, int height, int color);

    void fill(int x1, int y1, int x2, int y2, int color);

    // ----------------------------
    // Text
    // ----------------------------
    void drawText(String text, int x, int y, int font, int color);

    void drawText(float scale, String text, int x, int y, int font, int color);

    // ----------------------------
    // Textures & Sprites
    // ----------------------------
    void drawTexture(IdentifierRef sprite, int x, int y, float u, float v, int width, int height);

    void drawTexture(IdentifierRef sprite, int x, int y, float u, float v, int width, int height, int color);

    void drawTexture(IdentifierRef sprite, int x, int y, float u, float v, int width, int height,
            int regionWidth, int regionHeight, int textureWidth, int textureHeight);

    void drawTexture(IdentifierRef sprite, int x, int y, float u, float v, int width, int height,
            int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color);

    void drawSpriteStretched(SpriteRef sprite, int x, int y, int width, int height);

    void drawSpriteStretched(SpriteRef sprite, int x, int y, int width, int height, int color);

    // ----------------------------
    // Items
    // ----------------------------
    void drawItem(ItemStackRef item, int x, int y);

    // ----------------------------
    // Scissor / Clipping
    // ----------------------------
    void enableScissor(int x1, int y1, int x2, int y2);

    void disableScissor();

    boolean scissorContains(int x, int y);

    // ----------------------------
    // Window Info / Draw
    // ----------------------------
    int getScaledWindowWidth();

    int getScaledWindowHeight();

    void draw();
}