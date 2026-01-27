package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ocelot.class)
public abstract class OcelotMixin {

    // Set ocelot tame chance -> 1/X
    @ModifyArg(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"), index = 0)
    private int injected(int x) {
        return 2;
    }

    // Ocelots don't need to be in a tempted state to be tamed
    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Ocelot$OcelotTemptGoal;isRunning()Z"))
    private boolean injected(Ocelot.OcelotTemptGoal instance) {
        return true;
    }

    // Ocelots don't need to be in a specific distance to be tamed
    @ModifyConstant(method = "mobInteract", constant = @Constant(doubleValue = 9.0))
    private double modifyTameDistance(double constant) {
        return 25.0;
    }

    // Copy of Partonetrain's ocelot to cat conversion code from https://github.com/Partonetrain/trains_tweaks
    // The code is an updated version of https://modrinth.com/mod/ocelotfix
    @Inject(method = "mobInteract", at = @At("RETURN"))
    private void trains_tweaks$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Ocelot ocelot = (Ocelot)(Object)this;
        if(ocelot.isTrusting())
        {
            Level level = ocelot.level();

            if (!level.isClientSide){
                Cat cat = ocelot.convertTo(EntityType.CAT, true);
                cat.tame(player); //this sets owner UUID
                cat.setOrderedToSit(true);
                cat.setPersistenceRequired();
                cat.setXRot(ocelot.getXRot());
                //this doesn't seem to actually work, it always faces south
                //but as far as I can tell this is only a rendering thing?
            }
        }
    }

    // Ocelots won't run away
    @Inject(
            method = "reassessTrustingGoals",
            at = @At("HEAD"),
            cancellable = true
    )
    private void reassessTrustingGoals(CallbackInfo ci) {
        if (ModConfig.disableOcelotAvoidingPlayer) {
            ci.cancel();
        }
    }

    // adjust ocelot avoid distance
    @ModifyConstant(method = "reassessTrustingGoals", constant = @Constant(floatValue = 16.0f))
    private float modifyAvoidMaxDistance(float constant) {
        return ModConfig.ocelotAvoidMaxDistance;
    }
}
