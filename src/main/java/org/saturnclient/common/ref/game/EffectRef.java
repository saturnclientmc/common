package org.saturnclient.common.ref.game;

import org.saturnclient.common.ref.asset.IdentifierRef;
import org.saturnclient.common.ref.asset.SpriteRef;

public interface EffectRef {
    boolean shouldShowIcon();

    SpriteRef getIcon();

    default IdentifierRef getIconId() {
        return null;
    }

    boolean isInfinite();

    String getInfiniteText();

    int getDurationSeconds();
}
