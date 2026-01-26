package com.minhcraft.mixin.inventory;

import com.minhcraft.util.GlassHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CartographyTableMenu.class)
public abstract class CartographyTableMenuMixin extends AbstractContainerMenu {

    @Shadow
    @Final
    private ResultContainer resultContainer;

    protected CartographyTableMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(
            method = "quickMoveStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private void onQuickMoveStack(Player player, int index, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot, ItemStack itemStack2) {
        if (GlassHelper.isGlassPaneOrBlock(itemStack2) && !itemStack2.is(Items.GLASS_PANE)) {
            if (!this.moveItemStackTo(itemStack2, 1, 2, false)) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemStack2.getCount() == itemStack.getCount()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }

            slot.onTake(player, itemStack2);
            this.broadcastChanges();
            cir.setReturnValue(itemStack);
        }
    }

    @Inject(
            method = "method_17382",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private void onSetupResultSlot(ItemStack mapStack, ItemStack firstSlotStack, ItemStack resultSlotStack, Level level, BlockPos blockPos, CallbackInfo ci, MapItemSavedData mapItemSavedData) {
        if (GlassHelper.isGlassPaneOrBlock(firstSlotStack) && !firstSlotStack.is(Items.GLASS_PANE) && !mapItemSavedData.locked) {
            ItemStack newMap = mapStack.copyWithCount(1);
            newMap.getOrCreateTag().putBoolean("map_to_lock", true);
            this.resultContainer.setItem(2, newMap);
            this.broadcastChanges();
            ci.cancel();
        }
    }
}
