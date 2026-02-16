package com.minhcraft.beyondbetatweaks.mixin.feature.map_atlas_recipe_book;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MapItem.class)
public abstract class FilledMapItemMixin {

    // Make sure that ghost map item in atlas recipe doesn't show "Unknown map" tooltip
    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$suppressUnknownMapTooltipForAtlasRecipeDisplay(ItemStack stack, Level level,
                                                 List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        CompoundTag display = stack.getTagElement("display");
        if (display != null && display.contains("Lore")) {
            ci.cancel();
        }
    }
}
