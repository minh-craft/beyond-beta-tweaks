package com.minhcraft.beyondbetatweaks.mixin.inventory;

import com.minhcraft.beyondbetatweaks.util.GlassHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.CartographyTableMenu$4")
public abstract class CartographyTableMenuSlotMixin {

    @Inject(
            method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (GlassHelper.isGlassPaneOrBlock(stack)) {
            cir.setReturnValue(true);
        }
    }
}
