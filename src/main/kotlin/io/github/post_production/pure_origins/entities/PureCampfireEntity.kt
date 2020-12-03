package io.github.post_production.pure_origins.entities

import io.github.post_production.pure_origins.PureOriginsMod
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Clearable
import net.minecraft.util.ItemScatterer
import net.minecraft.util.Tickable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class PureCampfireEntity: BlockEntity(PureOriginsMod.PURE_CAMPFIRE_ENTITY_TYPE), Clearable, Tickable,
    BlockEntityClientSerializable {
    private val itemsBeingCooked: DefaultedList<ItemStack> = DefaultedList.ofSize(4, ItemStack.EMPTY)
    private val cookingTimes: IntArray = IntArray(4)
    private val cookingTotalTimes: IntArray = IntArray(4)

    override fun tick() {
        val isLit: Boolean = this.cachedState.get(CampfireBlock.LIT)
        val isClient: Boolean = this.world!!.isClient
        if (isClient) {
            if (isLit) {
                spawnSmokeParticles()
            }
        } else {
            if (isLit) {
                updateItemsBeingCooked()
            } else {
                for (i in itemsBeingCooked.indices) {
                    if (cookingTimes[i] > 0) {
                        cookingTimes[i] = MathHelper.clamp(
                            cookingTimes[i] - 2, 0,
                            cookingTotalTimes[i]
                        )
                    }
                }
            }
        }
    }

    private fun updateItemsBeingCooked() {
        for (i in itemsBeingCooked.indices) {
            val itemStack = itemsBeingCooked[i]
            if (!itemStack.isEmpty) {
                // Increment the cooking time
                cookingTimes[i]++

                // If we have finished cooking, pop off the finished item
                if (cookingTimes[i] >= cookingTotalTimes[i]) {
                    val itemStack2 = ItemStack(PureOriginsMod.PURE_STONE_ITEM)
                    val blockPos: BlockPos = this.getPos()
                    ItemScatterer.spawn(
                        this.world,
                        blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(),
                        itemStack2
                    )

                    itemsBeingCooked[i] = ItemStack.EMPTY
                    updateListeners()
                }
            }
        }
    }

    private fun spawnSmokeParticles() {
        val world: World? = this.getWorld()
        if (world != null) {
            val blockPos: BlockPos = this.getPos()
            val random = world.random
            val j = (this.cachedState.get(CampfireBlock.FACING) as Direction).horizontal
            for (k in itemsBeingCooked.indices) {
                if (!itemsBeingCooked[k].isEmpty && random.nextFloat() < 0.2f) {
                    val direction = Direction.fromHorizontal(Math.floorMod(k + j, 4))
                    val d =
                        blockPos.x.toDouble() + 0.5 - (direction.offsetX.toFloat() * 0.3125f).toDouble() + (direction.rotateYClockwise().offsetX
                            .toFloat() * 0.3125f).toDouble()
                    val e = blockPos.y.toDouble() + 0.5
                    val g =
                        blockPos.z.toDouble() + 0.5 - (direction.offsetZ.toFloat() * 0.3125f).toDouble() + (direction.rotateYClockwise().offsetZ
                            .toFloat() * 0.3125f).toDouble()
                    for (l in 0..3) {
                        world.addParticle(
                            ParticleTypes.AMBIENT_ENTITY_EFFECT,
                            d, e, g,
                            1.0, 1.0, 1.0
                        )
                    }
                }
            }
        }
    }

    fun getItemsBeingCooked(): DefaultedList<ItemStack> = itemsBeingCooked

    override fun fromTag(state: BlockState?, tag: CompoundTag) {
        super.fromTag(state, tag)
        itemsBeingCooked.clear()
        Inventories.fromTag(tag, itemsBeingCooked)
        var js: IntArray
        if (tag.contains("CookingTimes", 11)) {
            js = tag.getIntArray("CookingTimes")
            System.arraycopy(
                js, 0,
                cookingTimes, 0, Math.min(cookingTotalTimes.size, js.size)
            )
        }
        if (tag.contains("CookingTotalTimes", 11)) {
            js = tag.getIntArray("CookingTotalTimes")
            System.arraycopy(
                js, 0,
                cookingTotalTimes, 0, Math.min(cookingTotalTimes.size, js.size)
            )
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        saveInitialChunkData(tag)
        tag.putIntArray("CookingTimes", cookingTimes)
        tag.putIntArray("CookingTotalTimes", cookingTotalTimes)
        return tag
    }

    private fun saveInitialChunkData(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        Inventories.toTag(tag, itemsBeingCooked, true)
        return tag
    }

    override fun toInitialChunkDataTag(): CompoundTag {
        return saveInitialChunkData(CompoundTag())
    }

    fun addItem(item: ItemStack, integer: Int): Boolean {
        for (i in itemsBeingCooked.indices) {
            if (itemsBeingCooked[i].isEmpty) {
                cookingTotalTimes[i] = integer
                cookingTimes[i] = 0
                itemsBeingCooked[i] = item.split(1)
                updateListeners()
                return true
            }
        }
        return false
    }

    private fun updateListeners() {
        this.markDirty()
        this.getWorld()!!
            .updateListeners(this.getPos(), this.cachedState, this.cachedState, 3)
        sync()
    }

    override fun clear() {
        itemsBeingCooked.clear()
    }

    fun spawnItemsBeingCooked() {
        if (this.world != null) {
            if (!this.world!!.isClient) {
                ItemScatterer.spawn(this.world, this.getPos(), getItemsBeingCooked())
            }
            updateListeners()
        }
    }

    override fun fromClientTag(tag: CompoundTag) {
        // First argument isn't even used...
        fromTag(null, tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        return toTag(tag)
    }
}