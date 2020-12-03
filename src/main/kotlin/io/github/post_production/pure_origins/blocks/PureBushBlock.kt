package io.github.post_production.pure_origins.blocks

import io.github.post_production.pure_origins.PureOriginsMod
import io.github.post_production.pure_origins.interfaces.LunarGlow
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

val BUSH_SETTINGS = AbstractBlock.Settings.of(Material.PLANT)
    .ticksRandomly()
    .sounds(BlockSoundGroup.SWEET_BERRY_BUSH)
    .nonOpaque()
    .noCollision()

class PureBushBlock: SweetBerryBushBlock(BUSH_SETTINGS), LunarGlow {
    // We can only grow a pure berry bush at night and with a direct view of the moon
    override fun canGrow(
        world: World,
        random: Random?,
        pos: BlockPos,
        state: BlockState?
    ): Boolean {
        return world.isNight && world.isSkyVisible(pos.offset(Direction.UP))
    }

    // Make sure to drop pure fruit
    @Environment(EnvType.CLIENT)
    override fun getPickStack(world: BlockView?, pos: BlockPos?, state: BlockState?): ItemStack {
        return ItemStack(PureOriginsMod.PURE_FRUIT_BUSH_ITEM)
    }

    // Make sure to drop pure fruit
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult? {
        val i = state.get(AGE) as Int
        val bl = i == 3

        return if (!bl && player.getStackInHand(hand).item === Items.BONE_MEAL) {
            ActionResult.PASS
        } else if (i > 1) {
            val j = 1 + world.random.nextInt(2)
            dropStack(world, pos, ItemStack(PureOriginsMod.PURE_FRUIT_BUSH_ITEM, j + if (bl) 1 else 0))
            world.playSound(
                null as PlayerEntity?,
                pos,
                SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH,
                SoundCategory.BLOCKS,
                1.0f,
                0.8f + world.random.nextFloat() * 0.4f
            )
            world.setBlockState(pos, state.with(AGE, 1) as BlockState, 2)
            ActionResult.success(world.isClient)
        } else {
            super.onUse(state, world, pos, player, hand, hit)
        }
    }
}