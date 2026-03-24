package org.saturnclient.mod.mods;

import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Fonts;
import org.saturnclient.config.property.BoolProperty;
import org.saturnclient.config.property.Property;
import org.saturnclient.mod.HudMod;
import org.saturnclient.mod.Mod;
import org.saturnclient.mod.ModLayout;
import org.saturnclient.mod.ModSpec;

/**
 * TpsMod estimates the server's ticks-per-second by measuring the
 * elapsed real time between consecutive world-time packets.
 *
 * TPS calculation is event-driven: the networking layer (or mixin)
 * calls {@link #onTimePacket(long)} whenever a time-update packet
 * arrives. No FeatureProvider is required because there is no
 * per-frame polling of the engine.
 */
public class TpsMod extends Mod implements HudMod {

    private static final BoolProperty enabled = Property.bool(false);
    private static final ModLayout layout = new ModLayout(60, Fonts.getHeight());

    // ---------------------------------------------------------------
    // TPS sampling state (static — shared across all instances)
    // ---------------------------------------------------------------

    private static final int SAMPLE_COUNT = 2;
    private static final double[] samples = new double[SAMPLE_COUNT];

    static {
        java.util.Arrays.fill(samples, 20.0);
    }

    private static long lastWorldAge = -1L;
    private static long lastRealTime = -1L;
    private static double currentTps = 20.0;
    private static int sampleIndex = 0;
    private static int samplesFilled = SAMPLE_COUNT;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public TpsMod() {
        super(
                new ModSpec("TPS Display", "tps")
                        .description("Displays the server's ticks per second")
                        .version("v0.2.0")
                        .tags("Utility"),
                enabled.named("Enabled"),
                layout.prop());
    }

    // ---------------------------------------------------------------
    // Packet hook (called by the networking mixin)
    // ---------------------------------------------------------------

    /**
     * Must be called every time the client receives a world-time packet
     * carrying the updated {@code worldAge} value.
     */
    public static void onTimePacket(long worldAge) {
        long now = System.currentTimeMillis();

        if (lastWorldAge != -1L && lastRealTime != -1L) {
            long tickDelta = worldAge - lastWorldAge;
            long timeDelta = now - lastRealTime;

            if (timeDelta > 0 && tickDelta > 0) {
                double sample = Math.min((tickDelta / (double) timeDelta) * 1000.0, 20.0);

                samples[sampleIndex % SAMPLE_COUNT] = sample;
                sampleIndex++;
                samplesFilled = Math.min(samplesFilled + 1, SAMPLE_COUNT);

                double sum = 0;
                for (int i = 0; i < samplesFilled; i++)
                    sum += samples[i];
                currentTps = sum / samplesFilled;
            }
        }

        lastWorldAge = worldAge;
        lastRealTime = now;
    }

    /** Resets TPS history (e.g. on server disconnect). */
    public static void reset() {
        lastWorldAge = -1L;
        lastRealTime = -1L;
        currentTps = 20.0;
        sampleIndex = 0;
        samplesFilled = SAMPLE_COUNT;
        java.util.Arrays.fill(samples, 20.0);
    }

    // ---------------------------------------------------------------
    // HudMod
    // ---------------------------------------------------------------

    @Override
    public void renderHud(RenderScope scope) {
        renderTps(currentTps, scope);
    }

    @Override
    public void renderDummy(RenderScope scope) {
        renderTps(20.0, scope);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void renderTps(double tps, RenderScope scope) {
        String text = String.format("%.1f TPS", tps);
        scope.drawText(text, 0, 0, layout.font.value, layout.fgColor.value);
        layout.width = Fonts.getWidth(text, layout.font.value);
    }

    // ---------------------------------------------------------------
    // Mod contract
    // ---------------------------------------------------------------

    @Override
    public ModLayout getDimensions() {
        return layout;
    }

    @Override
    public boolean isEnabled() {
        return enabled.value;
    }

    @Override
    public void onEnabled(boolean e) {
        enabled.value = e;
    }
}
