package org.saturnclient.cosmetics;

import org.saturnclient.client.ServiceClient;
import org.saturnclient.client.player.SaturnPlayer;
import org.saturnclient.common.ref.asset.IdentifierRef;

import java.util.*;

public class Cloaks {
    public static final String[] ALL_CLOAKS = {
            "glitch",
            "mercedes_flow",
            "crimson_mark",
            "bmw",
            "amg",
            "amg_petronas",
            "ferrari",
            "redbull",
            "black_hole_amethyst",
            "black_hole_flame",
            "black_hole_white",
            "albania_mark",
            "end",
            "spanish_empire",
            "spain_flag" };

    private static final String[] ANIMATED_CLOAKS = {
            "glitch",
            "black_hole_amethyst",
            "black_hole_flame",
            "black_hole_white" };

    public static final List<String> availableCloaks = new ArrayList<>();

    public static void initialize() {
        availableCloaks.add(0, "");
    }

    public static IdentifierRef getCurrentCloakTexture(String cloakName) {
        if (Arrays.asList(ANIMATED_CLOAKS).contains(cloakName)) {
            return IdentifierRef.ofSaturn("textures/cloaks/" + cloakName + ".gif");
        } else {
            return IdentifierRef.ofSaturn("textures/cloaks/" + cloakName + ".png");
        }
    }

    public static void setCloak(String cloakName) {
        if (availableCloaks.contains(cloakName)) {
            SaturnPlayer player = SaturnPlayer.get();

            if (player != null) {
                player.cloak = cloakName;
            }

            ServiceClient.setCloak(cloakName);
        }
    }
}