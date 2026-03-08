package org.saturnclient.common.module;

import java.util.List;

import org.saturnclient.common.ref.game.ItemStackRef;
import org.saturnclient.feature.features.StatusEffectsFeature;

/**
 * PlayerModule provides access to the state of the local player.
 *
 * All methods return safe defaults (false / 0 / empty) when no player
 * is present so that features do not need to null-check everywhere.
 */
public interface PlayerModule {

    // ---------------------------------------------------------------
    // Presence
    // ---------------------------------------------------------------

    /** Returns true when a local player entity exists. */
    boolean hasPlayer();

    // ---------------------------------------------------------------
    // Position
    // ---------------------------------------------------------------

    int getX();

    int getY();

    int getZ();

    // ---------------------------------------------------------------
    // Movement / input
    // ---------------------------------------------------------------

    boolean isForwardPressed();

    boolean isBackPressed();

    boolean isLeftPressed();

    boolean isRightPressed();

    boolean isJumpPressed();

    boolean isAttackPressed();

    boolean isUsePressed();

    boolean isSneaking();

    boolean isUsingItem();

    boolean hasHorizontalCollision();

    boolean isOnGround();

    void setSprinting(boolean sprinting);

    // ---------------------------------------------------------------
    // Velocity
    // ---------------------------------------------------------------

    Velocity getVelocity();

    /** Immutable snapshot of the player's velocity components. */
    final class Velocity {
        public final double x;
        public final double y;
        public final double z;

        public Velocity(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    // ---------------------------------------------------------------
    // Stats
    // ---------------------------------------------------------------

    float getHealth();

    // ---------------------------------------------------------------
    // Effects / Potions
    // ---------------------------------------------------------------

    /**
     * Returns a list of the player’s currently active effects.
     * Safe default: empty list if no player is present.
     */
    List<StatusEffectsFeature.EffectView> getActiveEffects();

    /**
     * Returns a list of dummy effects used in HUD editor / preview mode.
     * Safe default: empty list.
     */
    List<StatusEffectsFeature.EffectView> getDummyEffects();

    // ---------------------------------------------------------------
    // Inventory / equipment
    // ---------------------------------------------------------------

    ItemStackRef getMainHand();

    ItemStackRef getHelmet();

    ItemStackRef getChestplate();

    ItemStackRef getLeggings();

    ItemStackRef getBoots();

    // ---------------------------------------------------------------
    // Dummy / preview data (used in HUD editor)
    // ---------------------------------------------------------------

    ItemStackRef getDummyMainHand();

    ItemStackRef getDummyHelmet();

    ItemStackRef getDummyChestplate();

    ItemStackRef getDummyLeggings();

    ItemStackRef getDummyBoots();
}
