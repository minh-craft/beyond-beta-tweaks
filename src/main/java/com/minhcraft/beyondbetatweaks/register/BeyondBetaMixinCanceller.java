package com.minhcraft.beyondbetatweaks.register;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;
import java.util.Set;

public class BeyondBetaMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return Set.of(
                    // Disable Diversity's mixin ModdedEntityTamedNoWanderingMixin which seems to be causing a bit of lag according to spark
                    "xyz.faewulf.diversity.mixin.entity.preventSaddledMobMovement.ModdedEntityTamedNoWanderingMixin"
                ).contains(mixinClassName);
    }
}
