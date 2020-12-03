package io.github.post_production.pure_origins.items

import io.github.post_production.pure_origins.PureOriginsMod
import io.github.post_production.pure_origins.interfaces.ObeliskCrystal
import net.minecraft.item.Item
import net.minecraft.util.Rarity

val HOLLOW_CRYSTAL_SETTINGS: Item.Settings = Item.Settings()
    .fireproof()
    .group(PureOriginsMod.PURE_ITEM_GROUP)
    .rarity(Rarity.RARE)

class HollowCrystal: Item(HOLLOW_CRYSTAL_SETTINGS), ObeliskCrystal {
}