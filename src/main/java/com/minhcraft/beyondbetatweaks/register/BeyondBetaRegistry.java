package com.minhcraft.beyondbetatweaks.register;

import com.minhcraft.beyondbetatweaks.BeyondBetaTweaks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class BeyondBetaRegistry {
    public static void init() {

        // Register new paintings
        // all credits to https://www.youtube.com/@Mongster83 https://www.reddit.com/r/GoldenAgeMinecraft/comments/1i5fnh0/i_made_a_bunch_of_new_paintings_based_off/
        // and the original artist Kristoffer Zetterstrand https://zetterstrand.com/works/
        registerPainting("artist_and_kebab", 1, 2);
        registerPainting("cactus_bird", 2, 1);
        registerPainting("chocolate_grinder", 2, 2);
        registerPainting("de_chateau", 1, 1);
        registerPainting("drop", 2, 2);
        registerPainting("easel", 2, 2);
        registerPainting("fog", 2, 2);
        registerPainting("kebab", 1, 1);
        registerPainting("pergola", 4, 4);
        registerPainting("self_portrait", 1, 1);
        registerPainting("spawn", 1, 2);
        registerPainting("sunset_average", 2, 1);
        registerPainting("tergo", 4, 4);
    }

    public static void registerPainting(String paintingName, int width, int height) {
        Registry.register(
                BuiltInRegistries.PAINTING_VARIANT,
                BeyondBetaTweaks.id(paintingName),
                new PaintingVariant(width*16, height*16));
    }
}
