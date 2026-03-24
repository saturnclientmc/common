package org.saturnclient.mod;

import java.util.function.Supplier;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.config.ConfigManager;
import org.saturnclient.config.property.NamedProperty;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Textures;

public abstract class Mod {
    private final ModSpec spec;
    private final ConfigManager configManager;

    public Mod(ModSpec spec, NamedProperty... props) {
        this.spec = spec;
        configManager = new ConfigManager(spec.name);
        for (NamedProperty prop : props) {
            configManager.property(prop.name, prop.prop);
        }
    }

    public void render(RenderScope scope) {
    }

    public void tick() {
    }

    public abstract boolean isEnabled();

    public final void setEnabled(boolean e) {
        onEnabled(e);
        ModManager.updateEnabledModules();
    }

    public abstract void onEnabled(boolean e);

    public final String getName() {
        return spec.name;
    }

    public IdentifierRef getIconTexture() {
        return Textures.getModIcon(spec.namespace);
    }

    public ConfigManager getConfig() {
        return configManager;
    }

    public String getDescription() {
        return spec.description;
    }

    public String[] getTags() {
        return spec.tags;
    }

    public String getVersion() {
        return spec.version;
    }

    public boolean isSupported() {
        for (Supplier<?> supplier : spec.requiresFeature) {
            if (supplier.get() == null) {
                return false;
            }
        }

        return true;
    }
}
