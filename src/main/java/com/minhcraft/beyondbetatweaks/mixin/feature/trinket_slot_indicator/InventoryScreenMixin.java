package com.minhcraft.beyondbetatweaks.mixin.feature.trinket_slot_indicator;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Unique
    private static final int[] ARMOR_SLOT_INDICES = {5, 6, 7, 8};
    @Unique
    private static final String[] TRINKET_GROUPS = {"head", "chest", "legs", "feet"};

    // Indicator placement: top-right corner of the slot.
    @Unique
    private static final int OFFSET_X = 13; // from slot left edge (moved 1 right from original 12)
    @Unique
    private static final int OFFSET_Y = -2; // from slot top edge  (moved 1 up from original -1)

    // Colors (ARGB)
    @Unique
    private static final int COLOR_OUTLINE  = 0xFF000000;
    @Unique
    private static final int COLOR_UNFILLED_DARK = 0xFF595959;
    // 0xFF555555
    @Unique
    private static final int COLOR_UNFILLED_MEDIUM = 0xFF595959;
    // 0xFF8B8B8B
    @Unique
    private static final int COLOR_UNFILLED_LIGHT = 0xFF595959;
    // 0xFFC6C6C6

    @Unique
    private static final int COLOR_FILLED_DARK = 0xFF555555;
    // 0xFF303030
    @Unique
    private static final int COLOR_FILLED_MEDIUM = 0xFF8B8B8B;
    // 0xFF555555
    @Unique
    private static final int COLOR_FILLED_LIGHT = 0xFFC6C6C6;
    // 0xFF787878

    /**
     * We inject at the TAIL of {@code render} (not {@code renderBg}) because Trinkets
     * draws its slot-group panel during the render cycle of AbstractContainerScreen,
     * after renderBg. By hooking at the very end of render and pushing the pose stack
     * to a high Z, our indicators draw on top of everything — including the Trinkets
     * hover panel.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void beyond_beta_tweaks$renderTrinketSlotIndicators(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Player player = this.minecraft != null ? this.minecraft.player : null;
        if (player == null) return;

        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isEmpty()) return;

        TrinketComponent component = optional.get();
        Map<String, Map<String, TrinketInventory>> inventory = component.getInventory();

        // Push the pose stack and translate Z forward so we render on top of
        // the Trinkets slot-group panel, tooltips excluded.
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 400);

        for (int i = 0; i < ARMOR_SLOT_INDICES.length; i++) {
            String group = TRINKET_GROUPS[i];
            Map<String, TrinketInventory> groupSlots = inventory.get(group);
            if (groupSlots == null || groupSlots.isEmpty()) continue;

            // Find the matching container slot to get screen coordinates
            Slot armorSlot = null;
            for (Slot s : this.menu.slots) {
                if (s.index == ARMOR_SLOT_INDICES[i] && s.container == player.getInventory()) {
                    armorSlot = s;
                    break;
                }
            }
            if (armorSlot == null) continue;

            // Check if any trinket is equipped in this group
            boolean hasEquipped = false;
            int totalSlotCount = 0;
            for (TrinketInventory trinketInv : groupSlots.values()) {
                for (int slot = 0; slot < trinketInv.getContainerSize(); slot++) {
                    totalSlotCount++;
                    if (!trinketInv.getItem(slot).isEmpty()) {
                        hasEquipped = true;
                    }
                }
            }

            if (totalSlotCount == 0) continue;

            int x = this.leftPos + armorSlot.x + OFFSET_X;
            int y = this.topPos + armorSlot.y + OFFSET_Y;

            if (hasEquipped) {
                drawFilledCircle(guiGraphics, x, y, COLOR_FILLED_DARK, COLOR_FILLED_MEDIUM, COLOR_FILLED_LIGHT);
            } else {
                drawFilledCircle(guiGraphics, x, y, COLOR_UNFILLED_DARK, COLOR_UNFILLED_MEDIUM, COLOR_UNFILLED_LIGHT);
            }
        }

        pose.popPose();
    }

    /**
     * 5×5 filled circle with beveled 3×3 inner shading:
     *   #OOO#        O = outline
     *   OLLMO        L = light (highlight)
     *   OLMDO        M = medium (mid-tone)
     *   OMDDO        D = dark (shadow)
     *   #OOO#
     * Light in top-left 3 pixels, dark in bottom-right 3 pixels,
     * medium in bottom-left, center, and top-right.
     */
    @Unique
    private void drawFilledCircle(GuiGraphics g, int x, int y, int darkColor, int mediumColor, int lightColor) {
        // Outline (border pixels)
        g.fill(x + 1, y,     x + 4, y + 1, COLOR_OUTLINE); // top 3
        g.fill(x + 1, y + 4, x + 4, y + 5, COLOR_OUTLINE); // bottom 3
        g.fill(x,     y + 1, x + 1, y + 4, COLOR_OUTLINE); // left 3
        g.fill(x + 4, y + 1, x + 5, y + 4, COLOR_OUTLINE); // right 3

        // 3×3 inner fill — per-pixel beveled shading:
        //   L L M
        //   L M D
        //   M D D
        g.fill(x + 1, y + 1, x + 2, y + 2, lightColor);  // (0,0) top-left
        g.fill(x + 2, y + 1, x + 3, y + 2, lightColor);  // (1,0) top-center
        g.fill(x + 3, y + 1, x + 4, y + 2, mediumColor); // (2,0) top-right
        g.fill(x + 1, y + 2, x + 2, y + 3, lightColor);  // (0,1) mid-left
        g.fill(x + 2, y + 2, x + 3, y + 3, mediumColor); // (1,1) center
        g.fill(x + 3, y + 2, x + 4, y + 3, darkColor);   // (2,1) mid-right
        g.fill(x + 1, y + 3, x + 2, y + 4, mediumColor); // (0,2) bottom-left
        g.fill(x + 2, y + 3, x + 3, y + 4, darkColor);   // (1,2) bottom-center
        g.fill(x + 3, y + 3, x + 4, y + 4, darkColor);   // (2,2) bottom-right
    }
}