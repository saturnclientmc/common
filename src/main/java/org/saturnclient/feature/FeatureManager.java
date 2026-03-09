package org.saturnclient.feature;

import java.util.ArrayList;
import java.util.List;

import org.saturnclient.feature.features.*;

public class FeatureManager {
    public static List<Feature> ENABLED_MODS = new ArrayList<>();
    public static Feature[] MODS = {
            new ArmorDisplayFeature(), new DayCounterFeature(), new KeystrokesFeature(), new StatusEffectsFeature(),
            new AutoSprintFeature(), new FpsFeature(), new NametagsFeature(), new TpsFeature(),
            new ClockFeature(), new FreelookFeature(), new NoFogFeature(), new ZoomFeature(),
            new CoordinatesFeature(), new FullbrightFeature(), new PingFeature(),
            new CrosshairFeature(), new HealthDisplayFeature(), new SpeedometerFeature(),
    };

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
