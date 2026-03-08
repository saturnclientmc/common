package org.saturnclient.config;

import java.io.File;

import org.saturnclient.common.provider.GLFWProvider;
import org.saturnclient.common.provider.Providers;
import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.config.manager.ConfigManager;
import org.saturnclient.config.manager.Property;
import org.saturnclient.ui.resources.Textures;

public class Config {
    public static ConfigManager config;

    public static Property<Boolean> realisticLogo = Property.bool(false);
    public static Property<Boolean> saturnTitleScreen = Property.bool(true);
    public static Property<Boolean> cloakPhysics = Property.bool(true);
    public static Property<Integer> openEmoteWheel = Property.keybinding(GLFWProvider.GLFW_KEY_B);
    public static Property<Boolean> stagger = Property.bool(true);

    public static IdentifierRef getLogo() {
        return realisticLogo.value ? Textures.REALISTIC_LOGO : Textures.LOGO;
    }

    public static void init() {
        File configFile = new File(Providers.saturn.getClient().getRunDirectory(), "saturn.json");
        config = new ConfigManager(configFile, "Saturn Client");

        config.property("Realistic logo", realisticLogo);
        config.property("Saturn client title screen", saturnTitleScreen);
        config.property("Open Emote Wheel", openEmoteWheel);
        config.property("Cloak Physics", cloakPhysics);
        config.property("Stagger Animations", stagger);

        // Initialize a sub namespace for theme
        Theme.init(config);

        // Initialize a sub namespace for animations
        AnimationConfig.init(config);
    }
}
