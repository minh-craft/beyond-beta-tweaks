package com.minhcraft.beyondbetatweaks.mixin.compat_archery_exp;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BowItem.class, priority = 1500)
public abstract class BowItemMixinSquared {

    @TargetHandler(
            mixin = "org.infernalstudios.archeryexp.mixin.item.BowItemMixin",
            name = "archeryexp$applyRecoil"
    )
    @WrapMethod(
            method = "@MixinSquared:Handler"
    )
    private void beyond_beta_tweaks$disableBowRecoil(Player user, double amount, Operation<Void> original) {
        // no-op
    }
}
