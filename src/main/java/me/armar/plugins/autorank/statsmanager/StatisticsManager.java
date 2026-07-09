package me.armar.plugins.autorank.statsmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatzHandler;
import me.armar.plugins.autorank.statsmanager.handlers.vanilla.VanillaHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Material.*;

public class StatisticsManager {
    private final Autorank plugin;
    private final List<StatsPlugin> availableStatsPlugins = new ArrayList<>();

    public StatisticsManager(Autorank instance) {
        this.plugin = instance;
    }

    private boolean isPluginAvailable() {
        Plugin x = Bukkit.getServer().getPluginManager().getPlugin("Statz");
        return x != null;
    }

    public void loadAvailableStatsPlugins() {
        this.availableStatsPlugins.clear();
        if (this.isPluginAvailable()) {
            this.plugin.getLogger().info("Found Statz plugin: Statz (by Staartvin)");
            StatsPlugin statsPlugin = new StatzHandler(this.plugin, (StatzAPIHandler)this.plugin.getDependencyManager().getDependency(AutorankDependency.STATZ));
            if (!statsPlugin.isEnabled()) {
                this.plugin.getLogger().info("Couldn't hook into Statz! Make sure the version is correct!");
                return;
            }

            this.plugin.getLogger().info("Hooked into Statz (by Staartvin)");
            this.availableStatsPlugins.add(statsPlugin);
        }

        StatsPlugin statsPlugin = new VanillaHandler();
        this.plugin.getLogger().info("Registering statistics of vanilla Minecraft!");
        this.availableStatsPlugins.add(statsPlugin);
    }

    public int getBlocksBroken(UUID uuid, String worldName, Material block) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                if (block == CARROT) block = CARROTS;
                if (block == POTATO) block = POTATOES;
                if (block == BEETROOT) block = BEETROOTS;
                return availableStatsPlugin.getBlocksBroken(uuid, worldName, block);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getBlocksMoved(UUID uuid, String worldName) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getBlocksMoved(uuid, worldName);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getBlocksPlaced(UUID uuid, String worldName, Material block) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getBlocksPlaced(uuid, worldName, block);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getDamageTaken(UUID uuid, String worldName) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getDamageTaken(uuid, worldName);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getFishCaught(UUID uuid, String worldName) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getFishCaught(uuid, worldName);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getFoodEaten(UUID uuid, String worldName, Material food) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getFoodEaten(uuid, worldName, food);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getItemsCrafted(UUID uuid, String worldName, Material item) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getItemsCrafted(uuid, worldName, item);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getMobsKilled(uuid, worldName, mob);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getPlayersKilled(UUID uuid, String worldName) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getPlayersKilled(uuid, worldName);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getTimePlayed(UUID uuid, String worldName) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimePlayed(uuid, worldName);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getSheepShorn(UUID uuid, String worldName) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getSheepShorn(uuid, worldName);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getTimesVoted(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimesVoted(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getAnimalsBred(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getAnimalsBred(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getCakeSlicesEaten(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getCakeSlicesEaten(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getItemsEnchanted(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getItemsEnchanted(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getTimesDied(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimesDied(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getPlantsPotted(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getPlantsPotted(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getTimesTradedWithVillagers(UUID uuid) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimesTradedWithVillagers(uuid);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }

    public int getItemThrown(UUID uuid, Material item) {
        for(StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getItemThrown(uuid, item);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return 0;
    }
}
