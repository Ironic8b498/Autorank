package me.armar.plugins.autorank.statsmanager.handlers.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VanillaDataLoader {
    public VanillaDataLoader() {
    }

    public int getTotalBlocksBroken(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        int count = 0;
        if (player == null) {
            return count;
        } else {
            Material[] var4 = Material.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Material mat = var4[var6];
                count += player.getStatistic(Statistic.MINE_BLOCK, mat);
            }

            return count;
        }
    }

    public int getBlocksBroken(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.MINE_BLOCK, material);
    }

    public int getTotalBlocksPlaced(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        int count = 0;
        if (player == null) {
            return count;
        } else {
            Material[] var4 = Material.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Material mat = var4[var6];
                if (mat.isBlock()) {
                    count += player.getStatistic(Statistic.USE_ITEM, mat);
                }
            }

            return count;
        }
    }

    public int getBlocksPlaced(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.USE_ITEM, material);
    }

    public double getDistanceWalked(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0.0D : (double)player.getStatistic(Statistic.WALK_ONE_CM) / 100.0D;
    }

    public int getDamageTaken(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.DAMAGE_TAKEN);
    }

    public int getFishCaught(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.FISH_CAUGHT);
    }

    public int getTotalFoodEaten(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        int count = 0;
        if (player == null) {
            return count;
        } else {
            Material[] var4 = Material.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Material mat = var4[var6];
                if (mat.isEdible()) {
                    count += player.getStatistic(Statistic.USE_ITEM, mat);
                }
            }

            return count;
        }
    }

    public int getFoodEaten(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.USE_ITEM, material);
    }

    public int getTotalItemsCrafted(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        int count = 0;
        if (player == null) {
            return count;
        } else {
            Material[] var4 = Material.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Material mat = var4[var6];
                count += player.getStatistic(Statistic.CRAFT_ITEM, mat);
            }

            return count;
        }
    }

    public int getItemsCrafted(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.CRAFT_ITEM, material);
    }

    public int getTotalMobsKilled(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.MOB_KILLS);
    }

    public int getMobsKilled(UUID uuid, EntityType entityType) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.KILL_ENTITY, entityType);
    }

    public int getTotalPlayersKilled(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.PLAYER_KILLS);
    }

    public int getTimePlayed(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : (int)((double)player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20.0D / 60.0D);
    }

    public int getTimesShearsUsed(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.USE_ITEM, Material.SHEARS);
    }

    public int getAnimalsBred(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.ANIMALS_BRED);
    }

    public int getCakeSlicesEaten(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.ANIMALS_BRED);
    }

    public int getItemsEnchanted(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.ITEM_ENCHANTED);
    }

    public int getTimesDied(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.DEATHS);
    }

    public int getPlantsPotted(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.FLOWER_POTTED);
    }

    public int getTimesTradedWithVillages(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.TRADED_WITH_VILLAGER);
    }

    public int getItemThrown(UUID uuid, Material item) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return player == null ? 0 : player.getStatistic(Statistic.USE_ITEM, item);
    }
}