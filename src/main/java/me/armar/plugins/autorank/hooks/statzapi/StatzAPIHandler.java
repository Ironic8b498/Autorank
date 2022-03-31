package me.armar.plugins.autorank.hooks.statzapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin.StatType;
import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.hooks.StatzDependency;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class StatzAPIHandler extends DependencyHandler {
    private final Autorank plugin;
    private Statz statz;

    public StatzAPIHandler(Autorank instance) {
        this.plugin = instance;
    }

    public Plugin get() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("Statz");

        try {
            return plugin != null && plugin instanceof Statz ? plugin : null;
        } catch (NoClassDefFoundError var3) {
            this.plugin.getLogger().info("Could not find Statz because it's probably disabled! Does Statz properly enable?");
            return null;
        }
    }

    public me.staartvin.statz.hooks.DependencyHandler getDependencyHandler(StatzDependency dep) {
        return !this.isAvailable() ? null : this.statz.getStatzAPI().getDependencyHandler(dep);
    }

    public double getSpecificData(UUID uuid, StatType statType, RowRequirement... conditions) {
        if (!this.isAvailable()) {
            return -1.0D;
        } else {
            Object value;
            switch(statType) {
                case VOTES:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.VOTES, uuid, conditions);
                    break;
                case DAMAGE_TAKEN:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.DAMAGE_TAKEN, uuid, conditions);
                    break;
                case MOBS_KILLED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.KILLS_MOBS, uuid, conditions);
                    break;
                case PLAYERS_KILLED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.KILLS_PLAYERS, uuid, conditions);
                    break;
                case BLOCKS_MOVED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.DISTANCE_TRAVELLED, uuid, conditions);
                    break;
                case BLOCKS_PLACED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_PLACED, uuid, conditions);
                    break;
                case BLOCKS_BROKEN:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_BROKEN, uuid, conditions);
                    break;
                case TIME_PLAYED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.TIME_PLAYED, uuid, conditions);
                    break;
                case ITEMS_CRAFTED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.ITEMS_CRAFTED, uuid, conditions);
                    break;
                case FISH_CAUGHT:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.ITEMS_CAUGHT, uuid, conditions);
                    break;
                case TIMES_SHEARED:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.TIMES_SHORN, uuid, conditions);
                    break;
                case FOOD_EATEN:
                    value = this.statz.getStatzAPI().getSpecificData(PlayerStat.FOOD_EATEN, uuid, conditions);
                    break;
                default:
                    value = 0;
            }

            return value == null ? 0.0D : (Double)value;
        }
    }

    public double getTotalOf(UUID uuid, StatType statType, String worldName) {
        if (!this.isAvailable()) {
            return -1.0D;
        } else {
            double value;
            if (worldName == null) {
                value = this.getSpecificData(uuid, statType);
            } else {
                value = this.getSpecificData(uuid, statType, new RowRequirement("world", worldName));
            }

            return value;
        }
    }

    public boolean isAvailable() {
        return this.statz != null;
    }

    public boolean isInstalled() {
        Plugin plugin = this.get();
        return plugin != null && plugin.isEnabled();
    }

    public boolean setup(boolean verbose) {
        if (!this.isInstalled()) {
            if (verbose) {
                this.plugin.getLogger().info("Statz has not been found!");
            }

            return false;
        } else {
            this.statz = (Statz)this.get();
            if (this.statz != null) {
                if (verbose) {
                    this.plugin.getLogger().info("Statz has been found and can be used!");
                }

                return true;
            } else {
                if (verbose) {
                    this.plugin.getLogger().info("Statz has been found but cannot be used!");
                }

                return false;
            }
        }
    }
}
