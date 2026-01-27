package com.minhcraft.beyondbetatweaks.mixin.compat_archery_exp;

import com.bawnorton.mixinsquared.TargetHandler;
import com.minhcraft.beyondbetatweaks.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 1500)
public abstract class GuiMixinSquared {

    @Unique
    private static final ResourceLocation BOW_METER = new ResourceLocation(ArcheryExpansion.MOD_ID, "textures/gui/bow_charge_meter.png");

    // Add Archery Expansion's arrow GUI to crossbow loading too
    // Also add option to disable it entirely
    @TargetHandler(
            mixin = "org.infernalstudios.archeryexp.mixin.client.GuiMixin",
            name = "archeryexp$renderCrosshair"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beyond_beta_tweaks$removeArcheryExpArrowCrosshair(GuiGraphics drawContext, CallbackInfo ci, CallbackInfo ciSquared) {
        ciSquared.cancel();

        if (ModConfig.disableArcheryExpansionArrowGui) {
            return;
        }

        // Below this is modified version of archeryexp$renderCrosshair which also applies the arrow GUI to crossbow loading item use
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            ItemStack stack = player.getUseItem();
            Item item = stack.getItem();
            if (item instanceof BowItem || item instanceof CrossbowItem) {
                int width = drawContext.guiWidth();
                int height = drawContext.guiHeight();

                int y = height / 2 - 16;
                int x = width / 2 - 8;

                drawContext.blit(BOW_METER, x, y, 0, 0, 16, 7, 48, 7);

                float drawTime;

                if (item instanceof BowItem) {
                    IBowProperties properties = (IBowProperties) item;

                    if (properties.archeryexp$isSpecial())  {
                        drawTime = BowUtil.getPowerForDrawTime(stack.getUseDuration() - player.getUseItemRemainingTicks(), properties);
                    }
                    else {
                        drawTime = BowItem.getPowerForTime(player.getUseItem().getUseDuration() - player.getUseItemRemainingTicks());
                    }
                } else {
                    drawTime = (stack.getUseDuration() - player.getUseItemRemainingTicks()) / (float)(stack.getUseDuration() - 3);
                }


                if (drawTime >= 1) {
                    drawContext.blit(BOW_METER, x, y, 32, 0, 16, 7, 48, 7);
                } else {
                    drawContext.blit(BOW_METER, x, y, 16, 0, (int) (16 * drawTime), 7, 48, 7);
                }
            }
        }
    }

}
