package com.minhcraft.beyondbetatweaks.mixin.feature.innate_enchantments;

import com.minhcraft.beyondbetatweaks.config.InnateEnchantmentConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract void enchant(net.minecraft.world.item.enchantment.Enchantment enchantment, int level);

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;I)V", at = @At("RETURN"))
    private void beyond_beta_tweaks$addInnateEnchantments(CallbackInfo ci) {
        Item item = this.getItem();

        if (InnateEnchantmentConfig.hasEnchantments(item)) {
            for (InnateEnchantmentConfig.EnchantmentEntry entry : InnateEnchantmentConfig.getEnchantments(item)) {
                this.enchant(entry.enchantment, entry.level);
            }
        }
    }
}