package me.armar.plugins.autorank.statsmanager.handlers.vanilla;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class VanillaHandler extends StatsPlugin {
    private final Autorank plugin;
    private final VanillaDataLoader dataLoader;

    public VanillaHandler(Autorank instance) {
        this.plugin = instance;
        this.dataLoader = new VanillaDataLoader();
    }

    public int getBlocksBroken(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        return block == null ? this.dataLoader.getTotalBlocksBroken(uuid) : this.dataLoader.getBlocksBroken(uuid, block);
    }

    public int getBlocksMoved(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int)this.dataLoader.getDistanceWalked(uuid);
    }

    public int getBlocksPlaced(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        return block == null ? this.dataLoader.getTotalBlocksPlaced(uuid) : this.dataLoader.getBlocksPlaced(uuid, block);
    }

    public int getDamageTaken(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getDamageTaken(uuid);
    }

    public int getFishCaught(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getFishCaught(uuid);
    }

    public int getFoodEaten(UUID uuid, String worldName, Material food) throws UnsupportedOperationException {
        return food == null ? this.dataLoader.getTotalFoodEaten(uuid) : this.dataLoader.getFoodEaten(uuid, food);
    }

    public int getItemsCrafted(UUID uuid, String worldName, Material item) throws UnsupportedOperationException {
        return item == null ? this.dataLoader.getTotalItemsCrafted(uuid) : this.dataLoader.getItemsCrafted(uuid, item);
    }

    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) throws UnsupportedOperationException {
        return mob == null ? this.dataLoader.getTotalMobsKilled(uuid) : this.dataLoader.getMobsKilled(uuid, mob);
    }

    public int getPlayersKilled(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getTotalPlayersKilled(uuid);
    }

    public int getTimePlayed(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getTimePlayed(uuid);
    }

    public int getSheepShorn(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getTimesShearsUsed(uuid);
    }

    public int getTimesVoted(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public int getAnimalsBred(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getAnimalsBred(uuid);
    }

    public int getCakeSlicesEaten(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getCakeSlicesEaten(uuid);
    }

    public int getItemsEnchanted(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getItemsEnchanted(uuid);
    }

    public int getTimesDied(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getTimesDied(uuid);
    }

    public int getPlantsPotted(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getPlantsPotted(uuid);
    }

    public int getTimesTradedWithVillagers(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getTimesTradedWithVillages(uuid);
    }

    public int getItemThrown(UUID uuid, Material material) throws UnsupportedOperationException {
        return this.dataLoader.getItemThrown(uuid, material);
    }

    public boolean isEnabled() {
        return true;
    }
}
