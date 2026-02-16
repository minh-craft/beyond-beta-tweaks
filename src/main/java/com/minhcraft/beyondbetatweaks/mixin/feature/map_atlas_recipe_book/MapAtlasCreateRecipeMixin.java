package com.minhcraft.beyondbetatweaks.mixin.feature.map_atlas_recipe_book;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pepjebs.mapatlases.recipe.MapAtlasCreateRecipe;

@Mixin(MapAtlasCreateRecipe.class)
public abstract class MapAtlasCreateRecipeMixin extends CustomRecipe {

    @Final
    @Shadow
    private NonNullList<Ingredient> ingredients;

    @Unique
    private boolean inMatchesCall = false;

    public MapAtlasCreateRecipeMixin(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    // Allows the recipe to display in the recipe book
    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    // Allows the recipe to display properly in the recipe book
    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return new ItemStack(pepjebs.mapatlases.MapAtlasesMod.MAP_ATLAS.get());
    }

    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z",
            at = @At("HEAD"))
    private void onMatchesStart(CraftingContainer inv, Level level, CallbackInfoReturnable<Boolean> cir) {
        this.inMatchesCall = true;
    }

    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z",
            at = @At("RETURN"))
    private void onMatchesEnd(CraftingContainer inv, Level level, CallbackInfoReturnable<Boolean> cir) {
        this.inMatchesCall = false;
    }

    // Make the recipe display properly in the recipe book
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        if (this.inMatchesCall) {
            // During matches(), return the original ingredients only
            return this.ingredients;
        }

        ItemStack ghostMap = new ItemStack(Items.FILLED_MAP);

        // Add custom tooltip to ghost map
        CompoundTag displayTag = ghostMap.getOrCreateTagElement("display");
        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(
                Component.Serializer.toJson(
                        Component.translatable("tooltip.beyond-beta-tweaks.ghost_map.description")
                                .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false))
                )
        ));
        displayTag.put("Lore", lore);

        NonNullList<Ingredient> display = NonNullList.create();
        display.add(Ingredient.of(ghostMap));
        display.addAll(this.ingredients);
        return display;
    }
}
