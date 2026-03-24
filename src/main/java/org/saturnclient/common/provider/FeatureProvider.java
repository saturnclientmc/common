package org.saturnclient.common.provider;

import org.saturnclient.common.feature.EntityFeature;
import org.saturnclient.common.feature.NetworkFeature;
import org.saturnclient.common.feature.PlayerFeature;
import org.saturnclient.common.feature.RenderFeature;
import org.saturnclient.common.feature.WorldFeature;

/**
 * FeatureProvider is the single entry point for features to access
 * all common engine abstractions. Implementations are supplied by
 * the platform-specific client bootstrap (e.g. the Fabric/Forge mod).
 *
 * Mods should receive a FeatureProvider in their constructor and
 * store it; they must NOT cache individual sub-modules across ticks
 * because the provider may swap implementations at runtime (e.g. on
 * world change).
 */
public interface FeatureProvider {

    /** Access to the local player and movement state. */
    PlayerFeature player();

    /** Access to the game world (time, TPS packets, etc.). */
    WorldFeature world();

    /** Access to entity / nametag queries. */
    EntityFeature entity();

    /** Access to rendering utilities (window size, textures, etc.). */
    RenderFeature render();

    /** Access to the network / server connection. */
    NetworkFeature network();
}
