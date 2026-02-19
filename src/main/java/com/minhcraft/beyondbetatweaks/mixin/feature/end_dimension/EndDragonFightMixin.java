package com.minhcraft.beyondbetatweaks.mixin.feature.end_dimension;

import com.minhcraft.beyondbetatweaks.config.ModConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EndDragonFight.class)
public abstract class EndDragonFightMixin {

    @Shadow private boolean dragonKilled;
    @Shadow private boolean previouslyKilled;

    @Shadow protected abstract void spawnExitPortal(boolean previouslyKilled);

    @Unique
    final List<BlockPos> GATEWAY_POSITIONS = new ArrayList<>(Arrays.asList(
            new BlockPos(0, ModConfig.endGatewayHeight, -ModConfig.endGatewayRadius),
            new BlockPos(0, ModConfig.endGatewayHeight, ModConfig.endGatewayRadius),
            new BlockPos(ModConfig.endGatewayRadius, ModConfig.endGatewayHeight, 0),
            new BlockPos(-ModConfig.endGatewayRadius, ModConfig.endGatewayHeight, 0)
    ));

    @Final
    @Shadow
    private ObjectArrayList<Integer> gateways;

    @Unique
    private void spawnFourGateways(){
        EndDragonFight self = (EndDragonFight) (Object) this; //self-cast to access members and methods. IDE may not like this, but it works
        if (!gateways.isEmpty()) {
            for(BlockPos pos : GATEWAY_POSITIONS){
//                BeyondBetaTweaks.LOGGER.info("Spawning gateway at: {}", pos);
                self.spawnNewGateway(pos);
            }
        }
    }

    // Ender Dragon removal code adapted from https://github.com/quat1024/apathy by [quat1024](https://github.com/quat1024)
    @Inject(method = "scanState", at = @At("RETURN"))
    void beyond_beta_tweaks$finishScanningState(CallbackInfo ci) {
        //scanState is called ONCE, EVER, the very first time any player loads the End.
        //It is never called again (the `needsStateScanning` variable makes sure of that).
        //So this is a good time to do "first-run" tasks.

        dragonKilled = true;
        previouslyKilled = true;

        spawnExitPortal(true);
        spawnFourGateways();
    }
}
