package com.minhcraft.beyondbetatweaks.mixin.world;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
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

    @Inject(method = "spawnNewGateway()V", at=@At("HEAD"), cancellable = true)
    private void beyond_beta_tweaks$spawnOnlyFourGateways(CallbackInfo ci){

        //BeyondBetaTweaks.LOGGER.info("spawnOnlyFourGateways called");
        EndDragonFight self = (EndDragonFight) (Object) this; //self-cast to access members and methods. IDE may not like this, but it works
        if (!gateways.isEmpty()) {
            for(BlockPos pos : GATEWAY_POSITIONS){
                BeyondBetaTweaks.LOGGER.info("Spawning gateway at: " + pos);
                self.spawnNewGateway(pos);
            }
        }
        ci.cancel(); //cancel the rest of the method
    }
}
