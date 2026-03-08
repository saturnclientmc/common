package org.saturnclient.common.provider;

import org.saturnclient.common.module.EntityModule;
import org.saturnclient.common.module.NetworkModule;
import org.saturnclient.common.module.PlayerModule;
import org.saturnclient.common.module.RenderModule;
import org.saturnclient.common.module.WorldModule;

/**
 * ModuleProvider is the single entry point for features to access
 * all common engine abstractions. Implementations are supplied by
 * the platform-specific client bootstrap (e.g. the Fabric/Forge mod).
 *
 * Features should receive a ModuleProvider in their constructor and
 * store it; they must NOT cache individual sub-modules across ticks
 * because the provider may swap implementations at runtime (e.g. on
 * world change).
 */
public interface ModuleProvider {

    /** Access to the local player and movement state. */
    PlayerModule player();

    /** Access to the game world (time, TPS packets, etc.). */
    WorldModule world();

    /** Access to entity / nametag queries. */
    EntityModule entity();

    /** Access to rendering utilities (window size, textures, etc.). */
    RenderModule render();

    /** Access to the network / server connection. */
    NetworkModule network();
}
