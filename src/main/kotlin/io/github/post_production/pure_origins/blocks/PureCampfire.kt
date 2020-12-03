package io.github.post_production.pure_origins.blocks

import io.github.post_production.pure_origins.entities.PureCampfireEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.state.property.Properties
import net.minecraft.tag.ItemTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

val PURE_CAMPFIRE_SETTINGS = FabricBlockSettings.of(Material.WOOD)
    .nonOpaque()
    .luminance { if (it[Properties.LIT]) 10 else 0 }
    .emissiveLighting { state, world, pos -> true }
    .ticksRandomly()
    .sounds(BlockSoundGroup.WOOD)
    .breakByTool(FabricToolTags.AXES, 0)
    .hardness(2.0f)

class PureCampfire: CampfireBlock(true, 10, PURE_CAMPFIRE_SETTINGS) {
    override fun createBlockEntity(world: BlockView?): BlockEntity = PureCampfireEntity()

    @Environment(EnvType.CLIENT)
    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (state.get(LIT) as Boolean) {
            if (random.nextInt(10) == 0) {
                world.playSound(
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
                    SoundCategory.BLOCKS,
                    0.5f + random.nextFloat(),
                    random.nextFloat() * 0.7f + 0.6f,
                    false
                )
            }
            if (random.nextInt(5) == 0) {
                for (i in 0 until random.nextInt(1) + 1) {
                    world.addParticle(
                        ParticleTypes.AMBIENT_ENTITY_EFFECT,
                        pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5,
                        1.0, 1.0, 1.0
                    )
                }
            }
        }
    }

    override fun onUse(
        state: BlockState?,
        world: World,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        val blockEntity = world.getBlockEntity(pos)

        if (blockEntity is PureCampfireEntity) {
            val itemStack = player.getStackInHand(hand)

            if (itemStack.item.isIn(ItemTags.LOGS_THAT_BURN)) {
                if (!world.isClient && blockEntity.addItem(
                        if (player.abilities.creativeMode) itemStack.copy() else itemStack,
                        600 // 30 seconds
                    )
                ) {
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE)
                    return ActionResult.SUCCESS
                }
                return ActionResult.CONSUME
            }
        }

        return ActionResult.PASS
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (!state.isOf(newState.block)) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is PureCampfireEntity) {
                blockEntity.spawnItemsBeingCooked()
            }

            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }
}