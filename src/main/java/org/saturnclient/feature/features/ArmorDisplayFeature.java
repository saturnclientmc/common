package org.saturnclient.feature.features;

import org.saturnclient.common.provider.ModuleProvider;
import org.saturnclient.common.module.PlayerModule;
import org.saturnclient.common.ref.game.ItemStackRef;
import org.saturnclient.config.manager.Property;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;
import org.saturnclient.feature.FeatureLayout;
import org.saturnclient.feature.HudFeature;
import org.saturnclient.ui.RenderScope;

/**
 * ArmorDisplayFeature renders the durability of the player's equipped
 * armor pieces (and optionally the main-hand item) as a HUD overlay.
 *
 * Engine access is provided exclusively through {@link ModuleProvider}.
 */
public class ArmorDisplayFeature extends Feature implements HudFeature {

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Boolean> useMainHand = Property.bool(true);
    private static final FeatureLayout layout = new FeatureLayout(40, 75);

    private final ModuleProvider modules;

    public ArmorDisplayFeature(ModuleProvider modules) {
        super(
                new FeatureDetails("Armor Display", "armor")
                        .description("Displays armor durability")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                useMainHand.named("Use Main Hand"),
                layout.prop());

        this.modules = modules;
    }

    // ---------------------------------------------------------------
    // HudFeature
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        PlayerModule player = modules.player();
        renderArmor(scope,
                player.getMainHand(),
                player.getHelmet(),
                player.getChestplate(),
                player.getLeggings(),
                player.getBoots());
    }

    @Override
    public void renderDummy(RenderScope scope) {
        PlayerModule player = modules.player();
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
    // Feature contract
    // ---------------------------------------------------------------

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
}
