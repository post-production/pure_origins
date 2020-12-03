package io.github.post_production.pure_origins.items

import io.github.post_production.pure_origins.PureOriginsMod
import io.github.post_production.pure_origins.interfaces.ObeliskCrystal
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

val PURE_CRYSTAL_SETTINGS = Item.Settings()
    .fireproof()
    .group(PureOriginsMod.PURE_ITEM_GROUP)
    .rarity(Rarity.EPIC)

// TODO: Implement custom rendering of the icon
open class PureCrystal(val origin: Identifier, val layer: Identifier, val icon: Identifier): Item(PURE_CRYSTAL_SETTINGS), ObeliskCrystal {
    override fun hasGlint(stack: ItemStack?): Boolean = true
}