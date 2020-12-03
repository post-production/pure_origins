package io.github.post_production.pure_origins

import io.github.post_production.pure_origins.blocks.ObeliskBlock
import io.github.post_production.pure_origins.blocks.PureBushBlock
import io.github.post_production.pure_origins.blocks.PureCampfire
import io.github.post_production.pure_origins.blocks.PureStoneBlock
import io.github.post_production.pure_origins.effects.PureStatusEffect
import io.github.post_production.pure_origins.entities.ObeliskEntity
import io.github.post_production.pure_origins.entities.PureCampfireEntity
import io.github.post_production.pure_origins.integrations.OriginsIntegration
import io.github.post_production.pure_origins.items.HollowCrystal
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.*
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

class PureOriginsMod: ModInitializer {
    companion object {
        // Identifiers
        const val MOD_ID = "pure_origins"
        val ID_PURE_STATUS_EFFECT: Identifier = Identifier(MOD_ID, "pure_status_effect")
        val ID_PURE_FRUIT_BUSH: Identifier = Identifier(MOD_ID, "pure_fruit")
        val ID_PURE_CAMPFIRE: Identifier = Identifier(MOD_ID, "pure_campfire")
        val ID_HOLLOW_CRYSTAL: Identifier = Identifier(MOD_ID, "hollow_crystal")
        val ID_PURE_STONE: Identifier = Identifier(MOD_ID, "pure_stone")
        val ID_OBELISK: Identifier = Identifier(MOD_ID, "obelisk")

        // Translatable IDs
        val ID_MISSING_POWER_MSG: Identifier = Identifier(MOD_ID, "missing_power_msg")

        // Blocks and items
        lateinit var PURE_STATUS_EFFECT: PureStatusEffect
        lateinit var PURE_FRUIT_BUSH_BLOCK: PureBushBlock
        lateinit var PURE_FRUIT_BUSH_ITEM: Item
        lateinit var PURE_CAMPFIRE_BLOCK: PureCampfire
        lateinit var PURE_CAMPFIRE_ITEM: Item
        lateinit var HOLLOW_CRYSTAL_ITEM: HollowCrystal
        lateinit var PURE_STONE_BLOCK: PureStoneBlock
        lateinit var PURE_STONE_ITEM: Item
        lateinit var OBELISK_BLOCK: ObeliskBlock
        lateinit var OBELISK_ITEM: Item

        // Entities
        lateinit var OBELISK_ENTITY_TYPE: BlockEntityType<ObeliskEntity>
        lateinit var PURE_CAMPFIRE_ENTITY_TYPE: BlockEntityType<PureCampfireEntity>

        // Groups
        val PURE_ITEM_GROUP: ItemGroup = FabricItemGroupBuilder
            .create(Identifier("pure_origins", "items"))
            .icon { ItemStack(PURE_FRUIT_BUSH_ITEM) }
            .build()
    }
    override fun onInitialize() {
        PURE_STATUS_EFFECT = Registry.register(
            Registry.STATUS_EFFECT,
            ID_PURE_STATUS_EFFECT,
            PureStatusEffect()
        )
        PURE_FRUIT_BUSH_BLOCK = Registry.register(
            Registry.BLOCK,
            ID_PURE_FRUIT_BUSH,
            PureBushBlock()
        )

        // Food info for the fruit
        val pureDuration = 200 // Around 10 seconds
        val pureFruitSettings = FabricItemSettings().rarity(Rarity.UNCOMMON).group(
            PURE_ITEM_GROUP
        ).food(
            FoodComponent.Builder()
                .alwaysEdible()
                .snack()
                .hunger(2)
                .statusEffect(StatusEffectInstance(PURE_STATUS_EFFECT, pureDuration), 0.8f)
                .build()
        )
        PURE_FRUIT_BUSH_ITEM = Registry.register(
            Registry.ITEM, ID_PURE_FRUIT_BUSH, BlockItem(
                PURE_FRUIT_BUSH_BLOCK, pureFruitSettings
            )
        )

        PURE_CAMPFIRE_BLOCK = Registry.register(Registry.BLOCK, ID_PURE_CAMPFIRE, PureCampfire())
        PURE_CAMPFIRE_ITEM = Registry.register(
            Registry.ITEM, ID_PURE_CAMPFIRE, BlockItem(
                PURE_CAMPFIRE_BLOCK,
                FabricItemSettings().rarity(Rarity.UNCOMMON).group(PURE_ITEM_GROUP)
            )
        )

        HOLLOW_CRYSTAL_ITEM = Registry.register(Registry.ITEM, ID_HOLLOW_CRYSTAL, HollowCrystal())
        PURE_STONE_BLOCK = Registry.register(Registry.BLOCK, ID_PURE_STONE, PureStoneBlock())
        PURE_STONE_ITEM = Registry.register(Registry.ITEM, ID_PURE_STONE, BlockItem(PURE_STONE_BLOCK,
            Item.Settings()
                .group(PURE_ITEM_GROUP)
                .rarity(Rarity.UNCOMMON)
        ))

        OBELISK_BLOCK = Registry.register(Registry.BLOCK, ID_OBELISK, ObeliskBlock())
        OBELISK_ITEM = Registry.register(Registry.ITEM, ID_OBELISK, BlockItem(OBELISK_BLOCK,
            Item.Settings()
                .group(PURE_ITEM_GROUP)
                .rarity(Rarity.UNCOMMON)
        ))

        // Init the entity types
        OBELISK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, ID_OBELISK, BlockEntityType.Builder
            .create(Supplier{ ObeliskEntity() }, OBELISK_BLOCK).build(null)
        )
        PURE_CAMPFIRE_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, ID_PURE_CAMPFIRE, BlockEntityType.Builder
            .create(Supplier{ PureCampfireEntity() }, PURE_CAMPFIRE_BLOCK).build(null)
        )

        // Hook into the origins
        OriginsIntegration.register()
    }
}


