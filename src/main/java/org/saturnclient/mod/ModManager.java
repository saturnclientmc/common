package org.saturnclient.mod;

import java.util.ArrayList;
import java.util.List;

import org.saturnclient.mod.mods.*;

public class ModManager {
    public static List<Mod> ENABLED_MODS = new ArrayList<>();
    public static List<Mod> MODS = new ArrayList<>(List.of(
            new ArmorDisplayMod(), new DayCounterMod(), new KeystrokesMod(), new StatusEffectsMod(),
            new AutoSprintMod(), new FpsMod(), new NametagsMod(), new TpsMod(),
            new ClockMod(), new FreelookMod(), new NoFogMod(), new ZoomMod(),
            new CoordinatesMod(), new FullbrightMod(), new PingMod(),
            new CrosshairMod(), new HealthDisplayMod(), new SpeedometerMod()));

    public static void init() {
        MODS.removeIf(mod -> !mod.isSupported());
        updateEnabledModules();
    }

    public static synchronized void updateEnabledModules() {
        ENABLED_MODS.clear();
        for (Mod m : MODS) {
            if (m.isEnabled()) {
                ENABLED_MODS.add(m);
            }
        }
    }
}
