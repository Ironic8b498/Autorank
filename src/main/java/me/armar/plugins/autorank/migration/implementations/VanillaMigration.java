package me.armar.plugins.autorank.migration.implementations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VanillaMigration extends MigrationablePlugin {
    public VanillaMigration(Autorank instance) {
        super(instance);
    }

    public boolean isReady() {
        return true;
    }

    public CompletableFuture<Integer> migratePlayTime(List<UUID> uuids) {
        return !this.isReady() ? CompletableFuture.completedFuture(0) : CompletableFuture.supplyAsync(() -> {
            this.getPlugin().debugMessage("Migrating player data from Minecraft's statistics!");
            int playersImported = 0;
            Iterator var2 = this.getPlugin().getServer().getWorlds().iterator();

            while(true) {
                World world;
                File[] playerFiles;
                do {
                    if (!var2.hasNext()) {
                        return playersImported;
                    }

                    world = (World)var2.next();
                    File worldFolder = new File(world.getWorldFolder(), "stats");
                    playerFiles = worldFolder.listFiles();
                } while(playerFiles == null);

                File[] var6 = playerFiles;
                int var7 = playerFiles.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    File playerStatistics = var6[var8];
                    UUID uuid = null;
                    if (playerStatistics.exists()) {
                        try {
                            uuid = UUID.fromString(playerStatistics.getName().replace("[Conflict]", "").replace(".json", ""));
                        } catch (IllegalArgumentException var18) {
                            this.getPlugin().debugMessage("Couldn't read statistics file '" + playerStatistics.getName() + "' on world " + world.getName());
                            continue;
                        }

                        if (uuid != null) {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = (JSONObject)parser.parse(new FileReader(playerStatistics));
                            } catch (ParseException | IOException var17) {
                                this.getPlugin().debugMessage("Couldn't read statistics file of player '" + uuid + "' on world " + world.getName());
                                continue;
                            }

                            if (jsonObject != null) {
                                long ticksPlayed = 0L;
                                if (jsonObject.containsKey("stats")) {
                                    JSONObject statsSection = (JSONObject)jsonObject.get("stats");
                                    if (statsSection.containsKey("minecraft:custom")) {
                                        JSONObject customSection = (JSONObject)statsSection.get("minecraft:custom");
                                        if (customSection.containsKey("minecraft:play_one_minute")) {
                                            ticksPlayed = (Long)customSection.get("minecraft:play_one_minute");
                                        }
                                    }
                                }

                                if (ticksPlayed > 0L) {
                                    this.getPlugin().debugMessage("Migrating vanilla data of '" + uuid + "' on world " + world.getName());
                                    this.getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.TOTAL_TIME, uuid, (int)(ticksPlayed / 1200L));
                                    ++playersImported;
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
