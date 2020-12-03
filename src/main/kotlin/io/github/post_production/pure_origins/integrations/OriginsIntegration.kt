package io.github.post_production.pure_origins.integrations

import io.github.apace100.origins.Origins
import io.github.post_production.pure_origins.PureOriginsMod
import io.github.post_production.pure_origins.items.PureCrystal
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class OriginsIntegration {
    companion object {
        fun register() {
            // TODO: Make this depend on the origins currently loaded
            val origins = arrayOf(
                Pair("avian", Identifier("minecraft:feather")),
                Pair("arachnid", Identifier("minecraft:cobweb")),
                Pair("blazeborn", Identifier("minecraft:blaze_powder")),
                Pair("elytrian", Identifier("minecraft:cobweb")),
                Pair("enderian", Identifier("minecraft:cobweb")),
                Pair("feline", Identifier("minecraft:cobweb")),
                Pair("merling", Identifier("minecraft:cobweb")),
                Pair("phantom", Identifier("minecraft:cobweb")),
                Pair("shulk", Identifier("minecraft:cobweb"))
            )

            // Add the possible crystals
            for (origin in origins) {
                Registry.register(
                    Registry.ITEM,
                    Identifier(PureOriginsMod.MOD_ID, "${origin.first}_crystal"),
                    object: PureCrystal(
                        Identifier(Origins.MODID, origin.first),
                        Identifier(Origins.MODID, "origin"),
                        origin.second
                    ) {}
                )
            }

            // Add a void one
            // TODO: Make this configurable
            Registry.register(
                Registry.ITEM,
                Identifier(PureOriginsMod.MOD_ID, "void_crystal"),
                object: PureCrystal(
                    Identifier(Origins.MODID, "human"),
                    Identifier(Origins.MODID, "origin"),
                    Identifier(PureOriginsMod.MOD_ID, "void_crystal")
                ) {}
            )

            // Listen for resources
            // TODO: Wait for Origins to add an event handler
        }
    }
}