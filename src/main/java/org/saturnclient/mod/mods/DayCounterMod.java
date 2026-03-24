package org.saturnclient.mod.mods;

import org.saturnclient.common.feature.WorldFeature;
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
 * DayCounterMod shows how many in-game days have elapsed.
 * Day count is derived from the world age via {@link WorldFeature}.
 */
public class DayCounterMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final ModLayout layout = new ModLayout(40, 18);

    public DayCounterMod() {
        super(
                new ModSpec("Day Counter", "day")
                        .description("Displays the number of in-game days elapsed")
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
        renderDay(Providers.feature.world().getDay(), scope);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderDay(271L, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderDay(long day, RenderScope scope) {
        String text = "Day: " + day;

        scope.drawText(text, 0, 0, layout.font.value, layout.fgColor.value);
        layout.width = Fonts.getWidth(text, layout.font.value);
        layout.height = 18 * text.split("\n").length;
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
