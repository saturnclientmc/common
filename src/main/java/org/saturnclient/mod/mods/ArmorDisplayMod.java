package org.saturnclient.mod.mods;

import org.saturnclient.common.feature.PlayerFeature;
import org.saturnclient.common.provider.FeatureProvider;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.game.ItemStackRef;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.HudMod;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModLayout;
import org.saturnclient.mod.ModSpec;
import org.saturnclient.ui.RenderScope;

/**
 * ArmorDisplayMod renders the durability of the player's equipped
 * armor pieces (and optionally the main-hand item) as a HUD overlay.
 *
 * Engine access is provided exclusively through {@link FeatureProvider}.
 */
public class ArmorDisplayMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final BoolProperty useMainHand = Property.bool(true);
    private static final ModLayout layout = new ModLayout(40, 75);

    public ArmorDisplayMod() {
        super(
                new ModSpec("Armor Display", "armor")
                        .description("Displays armor durability")
                        .version("v0.2.0")
                        .tags("Utility")
                        .requires(Providers.feature::player),
                enabled.named("Enabled"),
                useMainHand.named("Use Main Hand"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        PlayerFeature player = Providers.feature.player();
        renderArmor(scope,
                player.getMainHand(),
                player.getHelmet(),
                player.getChestplate(),
                player.getLeggings(),
                player.getBoots());
    }

    @Override
    public void renderDummy(RenderScope scope) {
        PlayerFeature player = Providers.feature.player();
        renderArmor(scope,
                player.getDummyMainHand(),
                player.getDummyHelmet(),
                player.getDummyChestplate(),
                player.getDummyLeggings(),
                player.getDummyBoots());
    }

    // ---------------------------------------------------------------
    // Rendering helpers
    // ---------------------------------------------------------------

    private void renderArmor(RenderScope scope,
            ItemStackRef mainHand,
            ItemStackRef helmet,
            ItemStackRef chestplate,
            ItemStackRef leggings,
            ItemStackRef boots) {

        int row = 0;

        if (useMainHand.value && !mainHand.isEmpty()) {
            row = renderSlot(scope, mainHand, row);
        }
        if (!helmet.isEmpty())
            row = renderSlot(scope, helmet, row);
        if (!chestplate.isEmpty())
            row = renderSlot(scope, chestplate, row);
        if (!leggings.isEmpty())
            row = renderSlot(scope, leggings, row);
        if (!boots.isEmpty())
            row = renderSlot(scope, boots, row);
    }

    /**
     * Renders a single equipment slot and returns the next row index.
     */
    private int renderSlot(RenderScope scope, ItemStackRef item, int row) {
        scope.drawItem(item, 0, 15 * row);

        if (item.getMaxDamage() > 0) {
            int remaining = item.getMaxDamage() - item.getDamage();
            scope.drawText(0.5f, String.valueOf(remaining),
                    17, (15 * row) + 3,
                    layout.font.value, layout.fgColor.value);
        }

        return row + 1;
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
