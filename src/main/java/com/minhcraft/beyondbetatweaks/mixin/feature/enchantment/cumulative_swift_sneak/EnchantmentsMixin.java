package com.minhcraft.beyondbetatweaks.mixin.feature.enchantment.cumulative_swift_sneak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SwiftSneakEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Enchantments.class)
public abstract class EnchantmentsMixin {
    @Shadow
    private static Enchantment register(String identifier, Enchantment enchantment) {
        throw new AssertionError();
    }
    @Shadow
    @Final
    private static EquipmentSlot[] ARMOR_SLOTS;

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/enchantment/Enchantments;register(Ljava/lang/String;Lnet/minecraft/world/item/enchantment/Enchantment;)Lnet/minecraft/world/item/enchantment/Enchantment;"
            ),
            slice = @Slice(

                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/item/enchantment/Enchantments;SOUL_SPEED:Lnet/minecraft/world/item/enchantment/Enchantment;"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/enchantment/Enchantments;SWIFT_SNEAK:Lnet/minecraft/world/item/enchantment/Enchantment;")
            )
    )
    // change swift sneak to be enchantable on all armor slots, not just leggings
    private static Enchantment beyond_beta_tweaks$swiftSneakOverride(String identifier, Enchantment enchantment, Operation<Enchantment> original) {
        return register("swift_sneak", new SwiftSneakEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
    }
}
