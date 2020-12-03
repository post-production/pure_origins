package io.github.post_production.pure_origins.effects

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffectType
import net.minecraft.entity.effect.StatusEffects

// This is in RGB
const val EFFECT_COLOR = 0xffffff

class PureStatusEffect: StatusEffect(StatusEffectType.BENEFICIAL, EFFECT_COLOR) {
    // You can always get the effect
    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean = true

    // Makes the user glow if it is nighttime
    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
        //
    }

    // Purity removes all harmful status effects when applied
    override fun onApplied(entity: LivingEntity?, attributes: AttributeContainer?, amplifier: Int) {
        super.onApplied(entity, attributes, amplifier)

        for (status in entity!!.activeStatusEffects.values) {
            if (status.effectType.type == StatusEffectType.HARMFUL) {
                entity.removeStatusEffect(status.effectType)
            }
        }
    }
}