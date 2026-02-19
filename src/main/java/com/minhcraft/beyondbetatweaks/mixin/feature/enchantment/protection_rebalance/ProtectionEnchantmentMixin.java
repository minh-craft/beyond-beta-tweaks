package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.protection_rebalance;

import com.minhcraft.beyondbetatweaks.interfaces.IFractionalProtectionEnchantment;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin implements IFractionalProtectionEnchantment {

    @Shadow @Final public ProtectionEnchantment.Type type;

    @Override
    @Unique
    public float beyond_beta_tweaks$getFloatDamageProtection(int level, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        } else if (this.type == ProtectionEnchantment.Type.ALL) {
            return level;
        } else if (this.type == ProtectionEnchantment.Type.FIRE && source.is(DamageTypeTags.IS_FIRE)) {
            return level * 5; // original is 2
        } else if (this.type == ProtectionEnchantment.Type.FALL && source.is(DamageTypeTags.IS_FALL)) {
            return level * 3.75F; // original is 3
        } else if (this.type == ProtectionEnchantment.Type.EXPLOSION && source.is(DamageTypeTags.IS_EXPLOSION)) {
            return level * 3.75F; // original is 2
        } else {
            return this.type == ProtectionEnchantment.Type.PROJECTILE && source.is(DamageTypeTags.IS_PROJECTILE) ? level * 2 : 0;
        }
    }
}
