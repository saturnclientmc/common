package org.saturnclient.cosmetics;

import java.util.ArrayList;
import java.util.List;

import org.saturnclient.client.ServiceClient;
import org.saturnclient.client.player.SaturnPlayer;

public class Hats {
    public static final String[] ALL_HATS = { "horns_black", "horns_white", "halo_white", "halo_black", "horns_end",
            "halo_end", "bucket_black", "bucket_end" };
    public static List<String> availableHats = new ArrayList<>();

    public static void initialize() {
        availableHats.add(0, "");
    }

    public static void setHat(String hatName) {
        if (availableHats.contains(hatName)) {
            SaturnPlayer player = SaturnPlayer.get();

            if (player != null) {
                player.hat = hatName;
            }

            ServiceClient.setHat(hatName);
        }
    }
}
