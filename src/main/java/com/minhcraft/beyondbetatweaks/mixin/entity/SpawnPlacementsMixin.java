package com.minhcraft.beyondbetatweaks.mixin.entity;

import com.minhcraft.beyondbetatweaks.entity.GiantSpawnRules;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SpawnPlacements.class)
public abstract class SpawnPlacementsMixin {

    // Modify giant spawn rules
    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SpawnPlacements;register(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/SpawnPlacements$Type;Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/entity/SpawnPlacements$SpawnPredicate;)V"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntityType;GIANT:Lnet/minecraft/world/entity/EntityType;"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntityType;GLOW_SQUID:Lnet/minecraft/world/entity/EntityType;")
            ),
            index = 3
    )
    private static SpawnPlacements.SpawnPredicate<? extends Monster> beyond_beta_tweaks$modifyGiantSpawnRules(SpawnPlacements.SpawnPredicate<? extends Monster> decoratorPredicate) {
        return GiantSpawnRules::checkGiantSpawnRules;
    }
}
