package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.tooltip;

import net.minecraft.network.chat.*;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.item.enchantment.SwiftSneakEnchantment;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow public abstract String getDescriptionId();

    @Shadow public abstract boolean isCurse();

    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$modifyEnchantTooltip(int level, CallbackInfoReturnable<Component> cir) {
        MutableComponent mutableComponent = Component.translatable(this.getDescriptionId());
        if (this.isCurse()) {
            mutableComponent.withStyle(ChatFormatting.RED);
        } else {
            if ((Object) this instanceof ProtectionEnchantment) {
                switch (((ProtectionEnchantment)(Object)this).type) {
                    case FALL -> {
                        mutableComponent = Component.literal("+15% ").append(mutableComponent);
                        mutableComponent.withStyle(ChatFormatting.YELLOW);
                    }
                    case FIRE -> {
                        mutableComponent = Component.literal("+20% ").append(mutableComponent);
                        mutableComponent.withStyle(ChatFormatting.RED);
                    }
                    case EXPLOSION -> {
                        mutableComponent = Component.literal("+15% ").append(mutableComponent);
                        mutableComponent.withStyle(ChatFormatting.GOLD);
                    }
                }
            } else if ((Object) this instanceof SwiftSneakEnchantment) {
                mutableComponent = Component.literal("+15% ").append(mutableComponent);
                mutableComponent.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA0522D)));

            } else if ((Object) this instanceof UntouchingEnchantment) {
                mutableComponent.withStyle(ChatFormatting.LIGHT_PURPLE);
            } else {
                mutableComponent.withStyle(ChatFormatting.GRAY);
            }
        }

        cir.setReturnValue(mutableComponent);
    }
}