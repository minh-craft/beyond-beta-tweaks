package com.minhcraft.beyondbetatweaks.world.bedrock_egg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class BedrockEggState extends SavedData {
    private static final String DATA_NAME = "bedrock_egg_state";

    private boolean eggPlaced = false;
    private boolean eggOpened = false;
    private int eggY = 0;

    // Transient flag — not saved to disk. Set when the egg opens,
    // consumed when the player arrives in the overworld.
    private boolean playBedrockEggOpeningSoundPending = false;

    public boolean isPlayBedrockEggOpeningSoundPending() { return playBedrockEggOpeningSoundPending; }
    public void setPlayBedrockEggOpeningSoundPending(boolean pending) { playBedrockEggOpeningSoundPending = pending; }


    public BedrockEggState() {
    }

    public static BedrockEggState load(CompoundTag tag) {
        BedrockEggState state = new BedrockEggState();
        state.eggPlaced = tag.getBoolean("EggPlaced");
        state.eggOpened = tag.getBoolean("EggOpened");
        state.eggY = tag.getInt("EggY");
        return state;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.putBoolean("EggPlaced", eggPlaced);
        tag.putBoolean("EggOpened", eggOpened);
        tag.putInt("EggY", eggY);
        return tag;
    }

    public static BedrockEggState get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                BedrockEggState::load,
                BedrockEggState::new,
                DATA_NAME
        );
    }

    public boolean isEggPlaced() { return eggPlaced; }
    public void setEggPlaced(boolean placed) { eggPlaced = placed; setDirty(); }

    public boolean isEggOpened() { return eggOpened; }
    public void setEggOpened(boolean opened) { eggOpened = opened; setDirty(); }

    public int getEggY() { return eggY; }
    public void setEggY(int y) { eggY = y; setDirty(); }
}
