package com.minhcraft.mixin.entity;

import com.minhcraft.config.ModConfig;
import com.minhcraft.entity.GiantAttackGoal;
import com.minhcraft.register.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Giant.class)
public abstract class GiantMixin extends Monster {

    protected GiantMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GIANT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.GIANT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GIANT_DEATH;
    }

    @Unique
    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    // Louder and lower step sound
    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(this.getStepSound(), 1.2F, 0.65F);
    }

    // volume increased beyond 1.0 just increases sound range
    // 2.2 increases sound range to ~35 blocks
    @Override
    protected float getSoundVolume() {
        return 2.2F;
    }

    // Copied from zombie goals
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    // Copied from zombie behavior goals
    @Unique
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new GiantAttackGoal((Giant)(Object) this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // Despawn the giant if it sees the sun
    @Override
    protected void customServerAiStep() {
        if (ModConfig.giantDespawnInSunlight
                && !this.level().dimensionType().hasFixedTime()
                && this.level().getSkyDarken() < ModConfig.giantDespawnWhenSkyDarkenLessThan) // isDay checks for < 4
        {
            if (this.level().canSeeSky(this.blockPosition()) && this.random.nextFloat() * 40.0F < 1.0F) {
                this.playSound(ModSounds.GIANT_HURT, 1.6F, 0.8F);
                spawnDespawnSmokeParticles();
                this.discard();
            }
        }

        super.customServerAiStep();
    }

    @Unique
    private void spawnDespawnSmokeParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            float width = this.getBbWidth();
            float height = this.getBbHeight();

            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    x, y + height / 2, z,
                    40, width / 2, height / 2, width / 2, 0.05);

            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    x, y + height / 2, z,
                    15, width / 2, height / 3, width / 2, 0.02);

            serverLevel.sendParticles(ParticleTypes.POOF,
                    x, y + 1, z,
                    20, width / 2, 0.5, width / 2, 0.1);
        }
    }

    // Set giant attributes
    @Inject(
            method = "createAttributes",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void crt$overrideGiantAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.setReturnValue(
                Monster.createMonsterAttributes()
                        .add(Attributes.MAX_HEALTH, ModConfig.giantMaxHealth)
                        .add(Attributes.MOVEMENT_SPEED, 0.23 * ModConfig.giantSpeedModifier)
                        .add(Attributes.ATTACK_DAMAGE, ModConfig.giantAttackDamage)
                        .add(Attributes.FOLLOW_RANGE, 35.0)
        );
    }

    // This condition was what was blocking giants from spawning under normal conditions, even if added to biome mob configuration
    // Thanks to @flytre from the Fabric discord for discovering the fix and posting about it - https://discord.com/channels/507304429255393322/721100785936760876/798534491538063380
    @Inject(
            method = "getWalkTargetValue",
            at = @At("HEAD"),
            cancellable = true)
    private void crt$allowGiantSpawn(BlockPos pos, LevelReader level, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.0F);
    }
}
