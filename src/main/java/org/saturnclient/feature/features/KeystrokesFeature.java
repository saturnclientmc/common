package org.saturnclient.feature.features;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.common.module.PlayerModule;
import org.saturnclient.config.manager.Property;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;

/**
 * KeystrokesFeature renders the WASD / mouse / space key states as an
 * on-screen overlay.
 */
public class KeystrokesFeature extends Feature implements HudFeature {

    // ---------------------------------------------------------------
    // Configuration
    // ---------------------------------------------------------------

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Boolean> showMouse = Property.bool(true);
    private static final Property<Boolean> showSpace = Property.bool(false);

    private static final Property<Integer> clickBg = Property.color(0xFFCCCCCC);
    private static final Property<Integer> clickFg = Property.color(0xFFFFFFFF);

    private static final FeatureLayout layout = new FeatureLayout(78, 54);

    // ---------------------------------------------------------------
    // Layout constants
    // ---------------------------------------------------------------

    private static final int KEY_SIZE = 24;
    private static final int KEY_SPACING = 3;
    private static final int MOUSE_WIDTH = 38;
    private static final int MOUSE_H = 24;
    private static final int SPACE_H = 19;

    // ---------------------------------------------------------------
    // Cached key states (updated in tick)
    // ---------------------------------------------------------------

    private boolean w, a, s, d, lmb, rmb, space;

    private final ModuleProvider modules;

    public KeystrokesFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Keystrokes", "keystrokes")
                        .description("Displays movement keystrokes")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                showMouse.named("Show mouse clicks"),
                showSpace.named("Show space"),
                layout.prop(),
                clickFg.named("Clicked fg"),
                clickBg.named("Clicked bg"));

        this.modules = modules;
        layout.renderBackground = false;
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public void tick() {
        PlayerModule player = modules.player();
        w = player.isForwardPressed();
        a = player.isLeftPressed();
        s = player.isBackPressed();
        d = player.isRightPressed();
        lmb = player.isAttackPressed();
        rmb = player.isUsePressed();
        space = player.isJumpPressed();
        refreshHeight();
    }

    @Override
    public void renderHud(RenderScope scope) {
        render(scope, true);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        render(scope, false);
    }

    @Override
    public FeatureLayout getDimensions() {
        return layout;
    }

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void render(RenderScope scope, boolean live) {
        int yOffset = 0;

        // W (top centre)
        renderKey(scope, live && w, 'W', KEY_SIZE + KEY_SPACING, yOffset);
        yOffset += KEY_SIZE + KEY_SPACING;

        // A S D
        renderKey(scope, live && a, 'A', 0, yOffset);
        renderKey(scope, live && s, 'S', KEY_SIZE + KEY_SPACING, yOffset);
        renderKey(scope, live && d, 'D', (KEY_SIZE + KEY_SPACING) * 2, yOffset);
        yOffset += KEY_SIZE + KEY_SPACING;

        // Spacebar (optional)
        if (showSpace.value) {
            renderSpaceKey(scope, live && space, 0, yOffset);
            yOffset += SPACE_H + KEY_SPACING;
        }

        // Mouse buttons (optional)
        if (showMouse.value) {
            renderMouseKey(scope, live && lmb, "LMB", 0, yOffset);
            renderMouseKey(scope, live && rmb, "RMB", MOUSE_WIDTH + KEY_SPACING, yOffset);
        }
    }

    private void renderKey(RenderScope scope, boolean pressed, char c, int x, int y) {
        int bg = pressed ? clickBg.value : layout.bgColor.value;
        int fg = pressed ? clickFg.value : layout.fgColor.value;
        scope.drawRoundedRectangle(x, y, KEY_SIZE, KEY_SIZE, layout.radius.value, bg);
        scope.drawText(0.6f, String.valueOf(c), x + 9, y + 7, layout.font.value, fg);
    }

    private void renderMouseKey(RenderScope scope, boolean pressed, String label, int x, int y) {
        int bg = pressed ? clickBg.value : layout.bgColor.value;
        int fg = pressed ? clickFg.value : layout.fgColor.value;
        scope.drawRoundedRectangle(x, y, MOUSE_WIDTH, MOUSE_H, layout.radius.value, bg);
        scope.drawText(0.6f, label, x + 9, y + 7, layout.font.value, fg);
    }

    private void renderSpaceKey(RenderScope scope, boolean pressed, int x, int y) {
        int bg = pressed ? clickBg.value : layout.bgColor.value;
        int fg = pressed ? clickFg.value : layout.fgColor.value;
        scope.drawRoundedRectangle(x, y, layout.width, SPACE_H, layout.radius.value, bg);
        int lineY = y + SPACE_H / 2;
        scope.drawRect(x + (layout.width - 30) / 2, lineY, 30, 1, fg);
    }

    private void refreshHeight() {
        int h = KEY_SIZE * 2 + KEY_SPACING;
        if (showSpace.value)
            h += SPACE_H + KEY_SPACING;
        if (showMouse.value)
            h += MOUSE_H + KEY_SPACING;
        layout.height = h;
    }
}
