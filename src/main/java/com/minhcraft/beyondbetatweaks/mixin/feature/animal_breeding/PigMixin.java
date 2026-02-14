package com.minhcraft.beyondbetatweaks.mixin.feature.animal_breeding;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(Pig.class)
public abstract class PigMixin extends Animal {

    // Pigs have litters code adapted from https://github.com/Pepperoni-Jabroni/PigsHaveLitters by [@Pepperoni-Jabroni](https://github.com/Pepperoni-Jabroni)

    @Shadow public abstract @Nullable Pig getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent);

    protected PigMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public void spawnChildFromBreeding(@NotNull ServerLevel level, @NotNull Animal mate) {
        super.spawnChildFromBreeding(level, mate);

        for(int i = 0; i < this.getPigletExtraSpawnCount(); ++i) {
            AgeableMob ageableMob = this.getBreedOffspring(level, mate);
            if (ageableMob != null) {
                ageableMob.setBaby(true);
                ageableMob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
                level.addFreshEntityWithPassengers(ageableMob);
            }
        }

    }

    @Unique
    private int getPigletExtraSpawnCount() {
        String litterSizing = ModConfig.pigLitterSizing;
        String[] confs = litterSizing.split(",");
        int currentSum = 0;
        Random random = new Random();
        float selection = random.nextFloat();

        for (String c : confs) {
            String[] entry = c.split(":");
            if (entry.length == 2) {
                int chances = Integer.parseInt(entry[0]);
                int pigletCount = Integer.parseInt(entry[1]);
                if (selection <= (float) chances / 100.0F + (float) currentSum / 100.0F) {
                    return pigletCount;
                }

                currentSum += chances;
            }
        }

        return 0;
    }
}
