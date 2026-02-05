package com.minhcraft.beyondbetatweaks.register;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class BeyondBetaMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        // Disable Diversity's mixin ModdedEntityTamedNoWanderingMixin which seems to be causing a bit of lag according to spark
        return mixinClassName.equals("xyz.faewulf.diversity.mixin.entity.preventSaddledMobMovement.ModdedEntityTamedNoWanderingMixin");
    }
}
