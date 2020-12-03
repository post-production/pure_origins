package io.github.post_production.pure_origins

import io.github.post_production.pure_origins.renderers.ObeliskEntityRenderer
import io.github.post_production.pure_origins.renderers.PureCampfireEntityRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.minecraft.client.render.RenderLayer

class PureOriginsClientMod: ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(PureOriginsMod.OBELISK_ENTITY_TYPE) {
            ObeliskEntityRenderer(it)
        }
        BlockEntityRendererRegistry.INSTANCE.register(PureOriginsMod.PURE_CAMPFIRE_ENTITY_TYPE) {
            PureCampfireEntityRenderer(it)
        }

        // Transparency
        BlockRenderLayerMap.INSTANCE.putBlock(PureOriginsMod.PURE_FRUIT_BUSH_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(PureOriginsMod.PURE_CAMPFIRE_BLOCK, RenderLayer.getCutout())
    }
}