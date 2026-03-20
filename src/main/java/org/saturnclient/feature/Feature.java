package org.saturnclient.feature;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.config.ConfigManager;
import org.saturnclient.config.property.Property.NamedProperty;
import org.saturnclient.ui.RenderScope;
import org.saturnclient.ui.resources.Textures;

public abstract class Feature {
    private final FeatureDetails details;
    private final ConfigManager configManager;

    public Feature(FeatureDetails details, NamedProperty<?>... props) {
        this.details = details;
        configManager = new ConfigManager(details.name);
        for (NamedProperty<?> prop : props) {
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
        FeatureManager.updateEnabledModules();
    }

    public abstract void onEnabled(boolean e);

    public final String getName() {
        return details.name;
    }

    public IdentifierRef getIconTexture() {
        return Textures.getModIcon(details.namespace);
    }

    public ConfigManager getConfig() {
        return configManager;
    }

    public String getDescription() {
        return details.description;
    }

    public String[] getTags() {
        return details.tags;
    }

    public String getVersion() {
        return details.version;
    }
}
