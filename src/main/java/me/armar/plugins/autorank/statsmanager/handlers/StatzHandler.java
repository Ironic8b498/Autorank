package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.statz.database.datatype.RowRequirement;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class StatzHandler extends StatsPlugin {
    private final Autorank plugin;
    private final StatzAPIHandler statzApi;

    public StatzHandler(Autorank instance, StatzAPIHandler statzAPI) {
        this.plugin = instance;
        this.statzApi = statzAPI;
    }

    public int getBlocksBroken(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        return block == null ? (int)this.statzApi.getSpecificData(uuid, StatType.BLOCKS_BROKEN, new RowRequirement[0]) : (int)this.statzApi.getSpecificData(uuid, StatType.BLOCKS_BROKEN, new RowRequirement[]{new RowRequirement("block", block.name())});
    }

    public int getBlocksMoved(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.BLOCKS_MOVED, worldName);
    }

    public int getBlocksPlaced(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        return block == null ? (int)this.statzApi.getSpecificData(uuid, StatType.BLOCKS_PLACED, new RowRequirement[0]) : (int)this.statzApi.getSpecificData(uuid, StatType.BLOCKS_PLACED, new RowRequirement[]{new RowRequirement("block", block.name())});
    }

    public int getDamageTaken(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.DAMAGE_TAKEN, worldName);
    }

    public int getFishCaught(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.FISH_CAUGHT, worldName);
    }

    public int getFoodEaten(UUID uuid, String worldName, Material food) throws UnsupportedOperationException {
        return food == null ? (int)this.statzApi.getSpecificData(uuid, StatType.FOOD_EATEN, new RowRequirement[0]) : (int)this.statzApi.getSpecificData(uuid, StatType.FOOD_EATEN, new RowRequirement[]{new RowRequirement("foodEaten", food.name())});
    }

    public int getItemsCrafted(UUID uuid, String worldName, Material item) throws UnsupportedOperationException {
        return item == null ? (int)this.statzApi.getSpecificData(uuid, StatType.ITEMS_CRAFTED, new RowRequirement[0]) : (int)this.statzApi.getSpecificData(uuid, StatType.ITEMS_CRAFTED, new RowRequirement[]{new RowRequirement("item", item.name())});
    }

    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) throws UnsupportedOperationException {
        return mob == null ? (int)this.statzApi.getSpecificData(uuid, StatType.MOBS_KILLED, new RowRequirement[0]) : (int)this.statzApi.getSpecificData(uuid, StatType.MOBS_KILLED, new RowRequirement[]{new RowRequirement("mob", mob.name())});
    }

    public int getPlayersKilled(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.PLAYERS_KILLED, worldName);
    }

    public int getTimePlayed(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.TIME_PLAYED, worldName);
    }

    public int getSheepShorn(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.TIMES_SHEARED, worldName);
    }

    public int getTimesVoted(UUID uuid) throws UnsupportedOperationException {
        return (int)this.statzApi.getTotalOf(uuid, StatType.VOTES, null);
    }

    public int getAnimalsBred(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getCakeSlicesEaten(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getItemsEnchanted(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getTimesDied(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getPlantsPotted(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getTimesTradedWithVillagers(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getItemThrown(UUID uuid, Material material) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean isEnabled() {
        if (this.statzApi == null) {
            this.plugin.getLogger().info("Statz (by Staartvin) api library was not found!");
            return false;
        } else if (!this.statzApi.isAvailable()) {
            this.plugin.getLogger().info("Statz (by Staartvin) is not enabled!");
            return false;
        } else {
            return true;
        }
    }
}
