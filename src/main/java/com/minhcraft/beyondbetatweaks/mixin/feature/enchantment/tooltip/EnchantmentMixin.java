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

    @Shadow public abstract int getMaxLevel();

    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$modifyEnchantTooltip(int level, CallbackInfoReturnable<Component> cir) {
        MutableComponent mutableComponent = Component.translatable(this.getDescriptionId());
        if (this.isCurse()) {
            mutableComponent.withStyle(ChatFormatting.RED);
        } else {
            if ((Object) this instanceof ProtectionEnchantment) {
                switch (((ProtectionEnchantment)(Object)this).type) {
                    case FALL -> mutableComponent.withStyle(ChatFormatting.YELLOW);
                    case FIRE -> mutableComponent.withStyle(ChatFormatting.RED);
                    case EXPLOSION -> mutableComponent.withStyle(ChatFormatting.GOLD);
                }
            } else if ((Object) this instanceof SwiftSneakEnchantment) {
                  mutableComponent.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA0522D)));

            } else if ((Object) this instanceof UntouchingEnchantment) {
                mutableComponent.withStyle(ChatFormatting.LIGHT_PURPLE);
            } else {
                mutableComponent.withStyle(ChatFormatting.GRAY);
            }
        }

        if (level != 1 || this.getMaxLevel() != 1) {
            mutableComponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }

        cir.setReturnValue(mutableComponent);
    }
}