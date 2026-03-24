package org.saturnclient.mod.mods;

import org.saturnclient.common.provider.Providers;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.HudMod;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModLayout;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Fonts;

/**
 * PingMod displays the current server round-trip time in
 * milliseconds as a HUD element.
 */
public class PingMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final ModLayout layout = new ModLayout(60, Fonts.getHeight());

    public PingMod() {
        super(
                new ModSpec("Ping Display", "ping")
                        .description("Displays ping to the server")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderPing(Providers.feature.network().getPing(), scope);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderPing(10, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderPing(int ping, RenderScope scope) {
        String text = ping + " ms";
        scope.drawText(text, 0, 0, layout.font.value, layout.fgColor.value);
        layout.width = Fonts.getWidth(text, layout.font.value);
    }

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public ModLayout getDimensions() {
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
}
