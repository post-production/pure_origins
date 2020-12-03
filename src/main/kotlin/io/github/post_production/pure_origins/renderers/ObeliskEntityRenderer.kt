package io.github.post_production.pure_origins.renderers

import io.github.post_production.pure_origins.entities.ObeliskEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3d
import net.minecraft.client.util.math.Vector3f


class ObeliskEntityRenderer(dispatcher: BlockEntityRenderDispatcher): BlockEntityRenderer<ObeliskEntity>(
    dispatcher
) {
    override fun render(
        entity: ObeliskEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        // Mandatory GL matrix push to start
        matrices.push()

        // Render the different faces
        for ((face, index) in entity.mappings) {
            // Get the crystal to render, skip empty ones
            val stack = entity.getStack(index)
            if (stack.isEmpty) continue

            // Push so that we can easily undo translations and such
            matrices.push()

            // Get offset from center
            val offset = face.unitVector
            offset.multiplyComponentwise(0.55f, 1f, 0.55f)
            offset.add(Vector3f(0.5f, 0.4f, 0.5f))
            val offsetDouble = Vector3d(offset.x.toDouble(), offset.y.toDouble(), offset.z.toDouble())

            // Push a little off of face
            matrices.translate(offsetDouble.x, offsetDouble.y, offsetDouble.z)

            // Rotate, if necessary
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(index.toFloat() * 90))

            // Render the crystal with correct lighting
            val correctLight = WorldRenderer.getLightmapCoordinates(
                entity.world,
                entity.pos.offset(face)
            )
            MinecraftClient.getInstance().itemRenderer.renderItem(
                stack,
                ModelTransformation.Mode.GROUND,
                correctLight,
                overlay,
                matrices,
                vertexConsumers
            )

            // Undo the transformation
            matrices.pop()
        }

        // Mandatory GL matrix pop at end
        matrices.pop()
    }
}