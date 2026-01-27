package com.minhcraft.beyondbetatweaks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Unique;

public abstract class GiantSpawnRules {

    // Giant spawn rule is the same as monster with additional conditions:
    // 1) moon phase must be 4 (new moon)
    // 2) can only spawn on the surface
    @Unique
    public static boolean checkGiantSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type,level,spawnType, pos, random)
                && level.getMoonPhase() == 4
                && level.canSeeSky(pos);
    }
}
