package com.minhcraft.beyondbetatweaks.mixin.item;

import com.google.common.collect.ImmutableMultimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwordItem.class)
public abstract class SwordItemMixin {
    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"),
            remap = false
    )
    private void beyond_beta_tweaks$injectConstructor(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties, CallbackInfo ci, @Local ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
        builder.put(ReachEntityAttributes.REACH, new AttributeModifier("Sword Reach Extension", ModConfig.swordRangeIncrease, AttributeModifier.Operation.ADDITION));
        builder.put(ReachEntityAttributes.ATTACK_RANGE, new AttributeModifier("Sword Reach Extension", ModConfig.swordRangeIncrease, AttributeModifier.Operation.ADDITION));
    }
}
