package me.armar.plugins.autorank.statsmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatzHandler;
import me.armar.plugins.autorank.statsmanager.handlers.vanilla.VanillaHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StatisticsManager {
    private final Autorank plugin;
    private final List<StatsPlugin> availableStatsPlugins = new ArrayList();

    public StatisticsManager(Autorank instance) {
        this.plugin = instance;
    }

    private boolean isPluginAvailable(String pluginName) {
        Plugin x = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        return x != null;
    }

    public void loadAvailableStatsPlugins() {
        this.availableStatsPlugins.clear();
        if (this.isPluginAvailable("Statz")) {
            this.plugin.getLogger().info("Found Statz plugin: Statz (by Staartvin)");
            StatsPlugin statsPlugin = new StatzHandler(this.plugin, (StatzAPIHandler)this.plugin.getDependencyManager().getDependency(DependencyManager.AutorankDependency.STATZ));
            if (!statsPlugin.isEnabled()) {
                this.plugin.getLogger().info("Couldn't hook into Statz! Make sure the version is correct!");
                return;
            }

            this.plugin.getLogger().info("Hooked into Statz (by Staartvin)");
            this.availableStatsPlugins.add(statsPlugin);
        }

        StatsPlugin statsPlugin = new VanillaHandler(this.plugin);
        this.plugin.getLogger().info("Registering statistics of vanilla Minecraft!");
        this.availableStatsPlugins.add(statsPlugin);
    }

    public int getBlocksBroken(UUID uuid, String worldName, Material block) {
        Iterator var4 = this.availableStatsPlugins.iterator();

        while(var4.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var4.next();

            try {
                return availableStatsPlugin.getBlocksBroken(uuid, worldName, block);
            } catch (UnsupportedOperationException var7) {
            }
        }

        return 0;
    }

    public int getBlocksMoved(UUID uuid, String worldName) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getBlocksMoved(uuid, worldName);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }

    public int getBlocksPlaced(UUID uuid, String worldName, Material block) {
        Iterator var4 = this.availableStatsPlugins.iterator();

        while(var4.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var4.next();

            try {
                return availableStatsPlugin.getBlocksPlaced(uuid, worldName, block);
            } catch (UnsupportedOperationException var7) {
            }
        }

        return 0;
    }

    public int getDamageTaken(UUID uuid, String worldName) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getDamageTaken(uuid, worldName);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }

    public int getFishCaught(UUID uuid, String worldName) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getFishCaught(uuid, worldName);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }

    public int getFoodEaten(UUID uuid, String worldName, Material food) {
        Iterator var4 = this.availableStatsPlugins.iterator();

        while(var4.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var4.next();

            try {
                return availableStatsPlugin.getFoodEaten(uuid, worldName, food);
            } catch (UnsupportedOperationException var7) {
            }
        }

        return 0;
    }

    public int getItemsCrafted(UUID uuid, String worldName, Material item) {
        Iterator var4 = this.availableStatsPlugins.iterator();

        while(var4.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var4.next();

            try {
                return availableStatsPlugin.getItemsCrafted(uuid, worldName, item);
            } catch (UnsupportedOperationException var7) {
            }
        }

        return 0;
    }

    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) {
        Iterator var4 = this.availableStatsPlugins.iterator();

        while(var4.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var4.next();

            try {
                return availableStatsPlugin.getMobsKilled(uuid, worldName, mob);
            } catch (UnsupportedOperationException var7) {
            }
        }

        return 0;
    }

    public int getPlayersKilled(UUID uuid, String worldName) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getPlayersKilled(uuid, worldName);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }

    public int getTimePlayed(UUID uuid, String worldName) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getTimePlayed(uuid, worldName);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }

    public int getSheepShorn(UUID uuid, String worldName) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getSheepShorn(uuid, worldName);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }

    public int getTimesVoted(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getTimesVoted(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getAnimalsBred(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getAnimalsBred(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getCakeSlicesEaten(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getCakeSlicesEaten(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getItemsEnchanted(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getItemsEnchanted(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getTimesDied(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getTimesDied(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getPlantsPotted(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getPlantsPotted(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getTimesTradedWithVillagers(UUID uuid) {
        Iterator var2 = this.availableStatsPlugins.iterator();

        while(var2.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var2.next();

            try {
                return availableStatsPlugin.getTimesTradedWithVillagers(uuid);
            } catch (UnsupportedOperationException var5) {
            }
        }

        return 0;
    }

    public int getItemThrown(UUID uuid, Material item) {
        Iterator var3 = this.availableStatsPlugins.iterator();

        while(var3.hasNext()) {
            StatsPlugin availableStatsPlugin = (StatsPlugin)var3.next();

            try {
                return availableStatsPlugin.getItemThrown(uuid, item);
            } catch (UnsupportedOperationException var6) {
            }
        }

        return 0;
    }
}
