package org.saturnclient.common.feature;

/**
 * NetworkFeature provides access to the current server connection.
 *
 * Methods return safe defaults (false / -1) when not connected.
 */
public interface NetworkFeature {

    /** Returns true when a server connection is active. */
    boolean hasNetwork();

    /**
     * Returns the current ping to the server in milliseconds.
     * Returns {@code -1} when unavailable.
     */
    int getPing();
}
