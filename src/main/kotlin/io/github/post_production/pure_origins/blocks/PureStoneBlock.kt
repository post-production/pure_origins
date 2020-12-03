package io.github.post_production.pure_origins.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction


val PURE_STONE_SETTINGS: AbstractBlock.Settings = FabricBlockSettings.of(Material.STONE)
    .sounds(BlockSoundGroup.STONE)
    .breakByTool(FabricToolTags.PICKAXES, 1)
    .hardness(4.0f)
    .requiresTool()
    .luminance { 10 }
    .emissiveLighting { _, _, _ -> true }

class PureStoneBlock: FacingBlock(PURE_STONE_SETTINGS) {
    init {
        defaultState = this.stateManager.defaultState.with(Properties.FACING, Direction.UP)
    }

    override fun appendProperties(stateManager: StateManager.Builder<Block?, BlockState?>) {
        stateManager.add(Properties.FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.side) as BlockState
    }
}