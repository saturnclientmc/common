package org.saturnclient.feature;

import java.util.ArrayList;
import java.util.List;

import org.saturnclient.feature.features.*;

public class FeatureManager {
    public static List<Feature> ENABLED_MODS = new ArrayList<>();
    public static Feature[] MODS = {};

    public static void init() {
        updateEnabledModules();
    }

    public static synchronized void updateEnabledModules() {
        ENABLED_MODS.clear();
        for (Feature m : MODS) {
            if (m.isEnabled()) {
                ENABLED_MODS.add(m);
            }
        }
    }
}
