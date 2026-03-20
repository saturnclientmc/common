package org.saturnclient.feature.features;

import org.saturnclient.common.module.EntityModule;
import org.saturnclient.config.property.Property;
import org.saturnclient.feature.Feature;
import org.saturnclient.feature.FeatureDetails;

/**
 * NametagsFeature modifies the nametags displayed above entities.
 *
 * Actual rendering is injected via a mixin; this class owns the
 * configuration and exposes two static query methods the mixin uses:
 * {@link #shouldReplaceName()} and
 * {@link #getNametagString(EntityModule.EntityState)}.
 *
 * No ModuleProvider is required here because the mixin passes the
 * already-resolved {@link EntityModule.EntityState} directly.
 */
public class NametagsFeature extends Feature {

    // ---------------------------------------------------------------
    // Configuration
    // ---------------------------------------------------------------

    private static final Property<Boolean> enabled = Property.bool(false);
    private static final Property<Boolean> healthDisplay = Property.bool(false);
    private static final Property<Boolean> players = Property.bool(true);
    private static final Property<Boolean> hostile = Property.bool(false);
    private static final Property<Boolean> passive = Property.bool(false);
    private static final Property<Boolean> heartEmoji = Property.bool(true);
    private static final Property<Boolean> obfuscate = Property.bool(false);

    private static final Property<Integer> unit = Property.select(0, "Health", "Hearts");
    private static final Property<Integer> format = Property.select(0, "Value", "Value / Total", "%");

    private static final Property<Integer> nameColor = Property.select(15,
            "Black", "Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple",
            "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow", "White");
    private static final Property<Integer> healthColor = Property.select(12,
            "Black", "Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple",
            "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow", "White");

    private static final String[] COLOR_CODES = {
            "§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7",
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"
    };

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public NametagsFeature() {
        super(
                new FeatureDetails("Nametags", "nametags")
                        .description("Allows you to modify entity nametags")
                        .tags("Camera", "Utility")
                        .version("v0.2.0"),
                enabled.named("Enabled"),
                healthDisplay.named("Display health"),
                players.named("Players"),
                hostile.named("Hostile"),
                passive.named("Passive"),
                heartEmoji.named("Heart emoji"),
                obfuscate.named("Obfuscate enemy names"),
                unit.named("Unit"),
                format.named("Format"),
                nameColor.named("Name color"),
                healthColor.named("Health color"));
    }

    // ---------------------------------------------------------------
    // Mixin query API
    // ---------------------------------------------------------------

    /** Returns {@code true} when nametag replacement is active. */
    public static boolean shouldReplaceName() {
        return enabled.value;
    }

    /**
     * Builds the replacement nametag string for the given entity state,
     * or {@code null} if this entity should not have its tag replaced.
     */
    public static String getNametagString(EntityModule.EntityState state) {
        if (state.getCustomName() == null)
            return null;
        if (!healthDisplay.value)
            return state.getCustomName();

        float health = state.getHealth();
        float maxHealth = state.getMaxHealth();

        EntityModule.EntityType type = state.getEntityType();
        if (type == EntityModule.EntityType.PASSIVE && !passive.value)
            return null;
        if (type == EntityModule.EntityType.HOSTILE && !hostile.value)
            return null;
        if (type == EntityModule.EntityType.PLAYER && !players.value)
            return null;
        if (type == EntityModule.EntityType.OTHER)
            return null;

        if (health < 0f || maxHealth < 0f || health > maxHealth)
            return null;

        String hpText = buildHealthString(health, maxHealth);
        String hpClr = COLOR_CODES[healthColor.value];
        String nameClr = COLOR_CODES[nameColor.value];
        String obfCode = obfuscate.value ? "§k" : "";
        String heart = heartEmoji.value ? " ❤" : "";

        return nameClr + obfCode + state.getCustomName() + "§r "
                + hpClr + hpText + heart;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static String buildHealthString(float health, float maxHealth) {
        float divisor = unit.value + 1;
        return switch (format.value) {
            case 1 -> String.format("%.1f", health / divisor)
                    + " / " + String.format("%.1f", maxHealth / divisor);
            case 2 -> String.format("%.0f%%", (health / maxHealth) * 100f);
            default -> String.format("%.1f", health / divisor);
        };
    }

    // ---------------------------------------------------------------
    // Feature contract
    // ---------------------------------------------------------------

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
    }
}
