package org.saturnclient.modules.interfaces;

import org.saturnclient.common.ref.game.ItemStackRef;

public interface ArmorDisplayInterface {

    ItemStackRef getMainHand();

    ItemStackRef getHelmet();

    ItemStackRef getChestplate();

    ItemStackRef getLeggings();

    ItemStackRef getBoots();

    ItemStackRef getDummyMainHand();

    ItemStackRef getDummyHelmet();

    ItemStackRef getDummyChestplate();

    ItemStackRef getDummyLeggings();

    ItemStackRef getDummyBoots();
}