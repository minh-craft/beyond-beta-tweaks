package com.minhcraft.mixin.compat_scholar;

import com.minhcraft.util.ScholarFormattingState;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpreadBookEditScreen.class)
public abstract class SpreadBookEditScreenMixin {

    @Shadow
    @Final
    protected ItemStack bookStack;

    @Shadow
    @Final
    protected InteractionHand hand;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void loadFormattingStateOnInit(CallbackInfo ci) {
        ScholarFormattingState.loadFromBook(bookStack);
    }

    @Inject(method = "onClose", at = @At("HEAD"), remap = true)
    private void saveFormattingStateOnClose(CallbackInfo ci) {
        ScholarFormattingState.saveToServer(hand);
    }
}
