package org.saturnclient.common.module;

/**
 * WorldModule provides access to world-level information that is not
 * tied to the local player (time of day, total age, etc.).
 *
 * All methods return safe defaults when no world is loaded.
 */
public interface WorldModule {

    /** Returns true when a world is currently loaded. */
    boolean hasWorld();

    /**
     * Returns the total world age in ticks.
     * Used by {@link org.saturnclient.feature.TpsFeature} to compute TPS.
     */
    long getWorldAge();

    /**
     * Returns the number of in-game days that have passed.
     * Computed as {@code worldAge / 24000}.
     */
    default long getDay() {
        return getWorldAge() / 24_000L;
    }
}
