package org.saturnclient.common.module;

/**
 * EntityModule provides queries about entities in the current world.
 *
 * Designed to be thin so that feature logic is not polluted with
 * engine-specific entity types.
 */
public interface EntityModule {

    // ---------------------------------------------------------------
    // Crosshair / targeting
    // ---------------------------------------------------------------

    /** Returns true when the player's crosshair is aimed at a living entity. */
    boolean isTargetingLivingEntity();

    // ---------------------------------------------------------------
    // Nametag entity state
    // ---------------------------------------------------------------

    /**
     * Represents the minimal observable state of an entity needed for
     * nametag rendering.  Implementations are created per-entity each
     * frame; they are intentionally not cached.
     */
    interface EntityState {

        /** The entity's display name, or {@code null} if it has none. */
        String getCustomName();

        float getHealth();
        float getMaxHealth();
        EntityType getEntityType();
    }

    enum EntityType {
        PLAYER,
        HOSTILE,
        PASSIVE,
        OTHER
    }
}
