package io.github.post_production.pure_origins.entities

import io.github.post_production.pure_origins.PureOriginsMod
import io.github.post_production.pure_origins.interfaces.ImplementedInventory
import io.github.post_production.pure_origins.interfaces.ObeliskCrystal
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.EnderEyeItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction


class ObeliskEntity: BlockEntity(PureOriginsMod.OBELISK_ENTITY_TYPE), ImplementedInventory, SidedInventory, BlockEntityClientSerializable {
    private val _items: DefaultedList<ItemStack?> = DefaultedList.ofSize(4, ItemStack.EMPTY)
    val mappings = mapOf(
        Direction.NORTH to 2,
        Direction.EAST to 1,
        Direction.SOUTH to 0,
        Direction.WEST to 3
    )

    fun getIndex(side: Direction?) = mappings[side] ?: -1

    // Inventory methods
    override fun getItemsImpl(): DefaultedList<ItemStack?> = _items
    override fun getAvailableSlots(side: Direction?): IntArray {
        val index: Int = getIndex(side)
        if (index < 0) return IntArray(0)

        return IntArray(1, init = { index })
    }
    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        val index = getIndex(dir)

        return index == slot && items[index].isEmpty && stack.item is ObeliskCrystal
    }
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        val index = getIndex(dir)

        return index == slot && stack.item is ObeliskCrystal
    }

    // Inventory saving
    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        Inventories.fromTag(tag, items)
    }
    override fun toTag(tag: CompoundTag?): CompoundTag? {
        Inventories.toTag(tag, items)
        return super.toTag(tag)
    }

    override fun markDirty() {
        super<BlockEntity>.markDirty()
        sync()
    }

    override fun fromClientTag(tag: CompoundTag?) {
        // First clear what we have so that we can resync
        //   Apparently, without this Minecraft won't clear out spaces
        //   that have been deleted...
        items.clear()

        Inventories.fromTag(tag, items)
    }

    override fun toClientTag(tag: CompoundTag?): CompoundTag {
        return Inventories.toTag(tag, items)
    }
}