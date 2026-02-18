package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.protection_rebalance;

import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin {

    @ModifyConstant(
            method = "getDamageProtection",
            constant = @Constant(intValue = 2, ordinal = 0)
    )
    private int beyond_beta_tweaks$modifyFireProtectionLevel(int constant) {
        // Update Fire Protection I so that it provides 5 damage protection levels or 20% damage reduction
        // so that 4 pieces of Fire Protection I armor provide the max 80% damage reduction
        return 5;
    }

    @ModifyConstant(
            method = "getDamageProtection",
            constant = @Constant(intValue = 2, ordinal = 1)
    )
    private int beyond_beta_tweaks$modifyBlastProtectionLevel(int constant) {
        // Update Blast Protection I so that it provides 4 damage protection levels or 16% damage reduction
        // so that 4 pieces of Blast Protection I armor provide 64% damage reduction
        return 4;
    }

    @ModifyConstant(
            method = "getDamageProtection",
            constant = @Constant(intValue = 3, ordinal = 0)
    )
    private int beyond_beta_tweaks$modifyFeatherFallingProtectionLevel(int constant) {
        // Update Feather Falling I so that it provides 4 damage protection levels or 16% damage reduction
        // so that 4 pieces of Feather Falling I armor provide 64% damage reduction
        return 4;
    }
}
