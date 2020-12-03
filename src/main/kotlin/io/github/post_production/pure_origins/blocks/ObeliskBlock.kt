package io.github.post_production.pure_origins.blocks

import io.github.apace100.origins.Origins
import io.github.apace100.origins.component.OriginComponent
import io.github.apace100.origins.origin.Origin
import io.github.apace100.origins.origin.OriginLayer
import io.github.apace100.origins.origin.OriginLayers
import io.github.apace100.origins.origin.OriginRegistry
import io.github.apace100.origins.registry.ModComponents
import io.github.post_production.pure_origins.PureOriginsMod
import io.github.post_production.pure_origins.effects.PureStatusEffect
import io.github.post_production.pure_origins.entities.ObeliskEntity
import io.github.post_production.pure_origins.items.HollowCrystal
import io.github.post_production.pure_origins.items.PureCrystal
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry.ITEM
import net.minecraft.world.BlockView
import net.minecraft.world.World


val OBELISK_SETTINGS: AbstractBlock.Settings = FabricBlockSettings.of(Material.STONE)
    .breakByTool(FabricToolTags.PICKAXES, 1)
    .requiresTool()
    .hardness(4.0f)
    .luminance { 10 }
    .emissiveLighting { _, _, _ -> true }


class ObeliskBlock: BlockWithEntity(OBELISK_SETTINGS) {
    companion object {
        const val MAX_HEIGHT: Int = 5
    }
    override fun createBlockEntity(world: BlockView?): BlockEntity = ObeliskEntity()

    // If we don't override this, the block is invisible
    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

    // Manual use
    override fun onUse(
        blockState: BlockState?,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        hand: Hand?,
        blockHitResult: BlockHitResult
    ): ActionResult {
        // Only do work on the server
        if (world.isClient) return ActionResult.SUCCESS

        val blockEntity = world.getBlockEntity(blockPos) as ObeliskEntity? ?: return ActionResult.FAIL
        val side = blockHitResult.side
        val index = blockEntity.getIndex(side)
        val held = player.getStackInHand(hand)
        val isPure: Boolean = player.statusEffects.find { it.effectType is PureStatusEffect } != null

        // Fail if we are trying to click on a non-insertable face
        if (index < 0) return ActionResult.FAIL

        // Get the stack of the actual entry
        val stack = blockEntity.getStack(index)

        // We always allow inserting, then we check if we can change, otherwise we remove
        when {
            blockEntity.canInsert(index, held, side) -> {
                // Insert a crystal we are holding, if possible
                blockEntity.setStack(index, ItemStack(held.item, 1))
                held.decrement(1)
                blockEntity.markDirty()
            }
            isPure && !stack.isEmpty -> {
                // Make sure it is primed
                var isPrimed = false
                var currentPos = blockPos
                for (offset in 0 until MAX_HEIGHT) {
                    // Do not iterate on invalid blocks
                    val block = world.getBlockState(currentPos).block
                    if (block !is ObeliskBlock && block !is PureStoneBlock) break

                    // Offset and check
                    currentPos = currentPos.offset(Direction.UP)
                    if (world.isSkyVisible(currentPos)) {
                        isPrimed = true
                        break
                    }
                }

                if (!world.isNight || !isPrimed) {
                    player.sendMessage(TranslatableText(PureOriginsMod.ID_MISSING_POWER_MSG.toString()), false)
                    return ActionResult.SUCCESS
                }

                var layer: OriginLayer? = null
                var origin: Origin? = null

                // Change the player's origin and (optionally) embue a
                //   hollow crystal with it, if necessary
                var component: OriginComponent = ModComponents.ORIGIN.get(player)
                if (stack.item is HollowCrystal) {
                    val playerOrigin: Origin = component.origins.values.first()
                        ?: throw Error("Player does not have a main origin!")
                    val pureCrystal = ITEM[Identifier(
                        PureOriginsMod.MOD_ID,
                        playerOrigin.identifier.path + "_crystal"
                    )]

                    // Perfect the hollow crystal
                    blockEntity.setStack(index, ItemStack(pureCrystal, 1))
                    blockEntity.markDirty()

                    // Drain the player to a human
                    // TODO: Allow for specifying which origin is the human
                    layer = OriginLayers.getLayer(Identifier(Origins.MODID, "origin"))
                    origin = OriginRegistry.get(Identifier(Origins.MODID, "human"))
                } else {
                    val crystal = stack.item as PureCrystal
                    origin = OriginRegistry.get(crystal.origin)
                    layer = OriginLayers.getLayer(crystal.layer)
                }

                // Sync the changes
                component.setOrigin(layer, origin)
                component.sync()

                // Show some lightning!
                val strike = LightningEntity(EntityType.LIGHTNING_BOLT, world)
                strike.setPos(player.pos.x, player.pos.y, player.pos.z)
                strike.setOnFireFor(0)
                world.spawnEntity(strike)

                // Finally, strip the player of the pure status effect
                player.removeStatusEffect(PureOriginsMod.PURE_STATUS_EFFECT)
            }
            held.isEmpty -> {
                // Remove from obelisk
                blockEntity.setStack(index, ItemStack.EMPTY)

                // Give to player
                player.setStackInHand(hand, ItemStack(stack.item, 1))

                blockEntity.markDirty()
            }
            else -> {
                return ActionResult.FAIL
            }
        }

        return ActionResult.SUCCESS
    }

    //This method will drop all items onto the ground when the block is broken
    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is ObeliskEntity) {
                ItemScatterer.spawn(world, pos, blockEntity as ObeliskEntity?)

                // update comparators
                world.updateComparators(pos, this)
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }
}