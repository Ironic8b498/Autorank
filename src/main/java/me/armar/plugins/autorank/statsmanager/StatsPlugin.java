package me.armar.plugins.autorank.statsmanager;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public abstract class StatsPlugin {
    public StatsPlugin() {
    }

    public abstract int getBlocksBroken(UUID var1, String var2, Material var3) throws UnsupportedOperationException;

    public abstract int getBlocksMoved(UUID var1, String var2) throws UnsupportedOperationException;

    public abstract int getBlocksPlaced(UUID var1, String var2, Material var3) throws UnsupportedOperationException;

    public abstract int getDamageTaken(UUID var1, String var2) throws UnsupportedOperationException;

    public abstract int getFishCaught(UUID var1, String var2) throws UnsupportedOperationException;

    public abstract int getFoodEaten(UUID var1, String var2, Material var3) throws UnsupportedOperationException;

    public abstract int getItemsCrafted(UUID var1, String var2, Material var3) throws UnsupportedOperationException;

    public abstract int getMobsKilled(UUID var1, String var2, EntityType var3) throws UnsupportedOperationException;

    public abstract int getPlayersKilled(UUID var1, String var2) throws UnsupportedOperationException;

    public abstract int getTimePlayed(UUID var1, String var2) throws UnsupportedOperationException;

    public abstract int getSheepShorn(UUID var1, String var2) throws UnsupportedOperationException;

    public abstract int getTimesVoted(UUID var1) throws UnsupportedOperationException;

    public abstract int getAnimalsBred(UUID var1) throws UnsupportedOperationException;

    public abstract int getCakeSlicesEaten(UUID var1) throws UnsupportedOperationException;

    public abstract int getItemsEnchanted(UUID var1) throws UnsupportedOperationException;

    public abstract int getTimesDied(UUID var1) throws UnsupportedOperationException;

    public abstract int getPlantsPotted(UUID var1) throws UnsupportedOperationException;

    public abstract int getTimesTradedWithVillagers(UUID var1) throws UnsupportedOperationException;

    public abstract int getItemThrown(UUID var1, Material var2) throws UnsupportedOperationException;

    public abstract boolean isEnabled();

    public enum StatType {
        BLOCKS_BROKEN,
        BLOCKS_MOVED,
        BLOCKS_PLACED,
        DAMAGE_TAKEN,
        FISH_CAUGHT,
        FOOD_EATEN,
        ITEMS_CRAFTED,
        MOBS_KILLED,
        PLAYERS_KILLED,
        TIME_PLAYED,
        TIMES_SHEARED,
        VOTES;

        StatType() {
        }
    }
}
