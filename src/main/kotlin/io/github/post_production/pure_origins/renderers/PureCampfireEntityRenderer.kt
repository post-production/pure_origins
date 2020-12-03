package io.github.post_production.pure_origins.renderers

import io.github.post_production.pure_origins.entities.PureCampfireEntity
import net.minecraft.block.CampfireBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction

class PureCampfireEntityRenderer(dispatcher: BlockEntityRenderDispatcher): BlockEntityRenderer<PureCampfireEntity> (
    dispatcher
) {
    override fun render(
        blockEntity: PureCampfireEntity,
        f: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider?,
        i: Int,
        j: Int
    ) {
        val direction = blockEntity.cachedState.get(CampfireBlock.FACING) as Direction
        val defaultedList = blockEntity.getItemsBeingCooked()
        for (k in defaultedList.indices) {
            val itemStack = defaultedList[k]
            if (itemStack != ItemStack.EMPTY) {
                matrixStack.push()
                matrixStack.translate(0.5, 0.44921875, 0.5)
                val direction2 = Direction.fromHorizontal((k + direction.horizontal) % 4)
                val g = -direction2.asRotation()
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(g))
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f))
                matrixStack.translate(-0.3125, -0.3125, 0.0)
                matrixStack.scale(0.375f, 0.375f, 0.375f)
                MinecraftClient.getInstance().itemRenderer.renderItem(
                    itemStack,
                    ModelTransformation.Mode.FIXED,
                    i,
                    j,
                    matrixStack,
                    vertexConsumerProvider
                )
                matrixStack.pop()
            }
        }
    }
}