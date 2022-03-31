package me.armar.plugins.autorank.util.uuid;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class UUIDStorage {
    private final HashMap<String, File> configFiles = new HashMap();
    private final HashMap<String, FileConfiguration> configs = new HashMap();
    private final String desFolder;
    private final List<String> fileSuffixes = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "other");
    private final Autorank plugin;

    public UUIDStorage(Autorank instance) {
        this.plugin = instance;
        this.desFolder = this.plugin.getDataFolder() + "/uuids";
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            public void run() {
                UUIDStorage.this.plugin.debugMessage("Periodically save all UUID files");
                UUIDStorage.this.saveAllFiles();
            }
        }, 1200L, 2400L);
    }

    public void loadStorageFiles() {
        this.plugin.getLogger().info("Loading UUID storage files...");
        long startTime = System.currentTimeMillis();
        this.fileSuffixes.parallelStream().forEach((suffix) -> {
            this.plugin.debugMessage("Loading uuids_" + suffix + " ...");
            this.reloadConfig(suffix);
            this.loadConfig(suffix);
        });
        this.plugin.getLogger().info("Loaded UUID storage in " + (System.currentTimeMillis() - startTime) / 1000L + " seconds.");
    }

    private FileConfiguration findCorrectConfig(String playerName) {
        String key = this.findMatchingKey(playerName.toLowerCase());
        return this.configs.get(key);
    }

    private String findMatchingKey(String text) {
        text = text.toLowerCase();
        Iterator var2 = this.fileSuffixes.iterator();

        String key;
        do {
            if (!var2.hasNext()) {
                return "other";
            }

            key = (String)var2.next();
        } while(key.equals("other") || !text.startsWith(key));

        return key;
    }

    private String getStoredUsername(UUID uuid) {
        Iterator var2 = this.fileSuffixes.iterator();

        while(var2.hasNext()) {
            String suffix = (String)var2.next();
            FileConfiguration config = this.getConfig(suffix);
            if (config == null) {
                return null;
            }

            Iterator var5 = config.getKeys(false).iterator();

            while(var5.hasNext()) {
                String fPlayerName = (String)var5.next();
                String fuuid = config.getString(fPlayerName + ".uuid");
                if (fuuid != null && fuuid.equals(uuid.toString())) {
                    String realName = config.getString(fPlayerName + ".realName", null);
                    if (realName != null) {
                        return realName;
                    }

                    return fPlayerName;
                }
            }
        }

        return null;
    }

    private FileConfiguration getConfig(String key) {
        FileConfiguration config = this.configs.get(key);
        if (config == null) {
            this.reloadConfig(key);
            config = this.configs.get(key);
        }

        return config;
    }

    private int getLastUpdateTime(String playerName) {
        playerName = playerName.toLowerCase();
        FileConfiguration fileConfiguration = this.findCorrectConfig(playerName);
        if (fileConfiguration == null) {
            return -1;
        } else {
            long lastUpdateTime = fileConfiguration.getLong(playerName + ".updateTime", -1L);
            if (lastUpdateTime < 0L) {
                return -1;
            } else {
                long difference = System.currentTimeMillis() - lastUpdateTime;
                return Math.round((float)difference / 3600000.0F);
            }
        }
    }

    protected CompletableFuture<String> getUsername(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String storedUsername = this.getStoredUsername(uuid);
            if (storedUsername != null) {
                return storedUsername;
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                return offlinePlayer.getName() == null ? null : offlinePlayer.getName();
            }
        });
    }

    protected CompletableFuture<UUID> getUUID(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = this.getStoredUUID(playerName);
            if (uuid != null) {
                return uuid;
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                return offlinePlayer.getName() == null ? offlinePlayer.getUniqueId() : null;
            }
        });
    }

    public UUID getStoredUUID(String playerName) {
        playerName = playerName.toLowerCase();
        FileConfiguration fileConfiguration = this.findCorrectConfig(playerName);
        if (fileConfiguration == null) {
            return null;
        } else {
            String uuidString = fileConfiguration.getString(playerName + ".uuid", null);
            return uuidString == null ? null : UUID.fromString(uuidString);
        }
    }

    protected boolean isStored(UUID uuid) {
        return this.getStoredUsername(uuid) != null;
    }

    public boolean isOutdated(String playerName) {
        return true;
    }

    public void loadConfig(String key) {
        FileConfiguration config = this.configs.get(key);
        if (config == null) {
            this.plugin.getLogger().severe("Can't find UUID storage file for " + key);
        } else {
            config.options().header("This file stores all uuids of players that Autorank has looked up before.\nEach file stores accounts with the starting letter of the player's name.");
            config.options().copyDefaults(true);
            this.saveConfig(key);
        }
    }

    public void reloadConfig(String key) {
        File configFile = null;
        FileConfiguration config = null;
        configFile = new File(this.desFolder, "uuids_" + key + ".yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        this.configs.put(key, config);
        this.configFiles.put(key, configFile);
    }

    public void saveAllFiles() {
        Iterator var1 = this.fileSuffixes.iterator();

        while(var1.hasNext()) {
            String suffix = (String)var1.next();
            this.saveConfig(suffix);
        }

    }

    public void saveConfig(String key) {
        File configFile = this.configFiles.get(key);
        FileConfiguration config = this.configs.get(key);
        if (config != null && configFile != null) {
            try {
                this.getConfig(key).save(configFile);
            } catch (IOException var5) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, var5);
            }

        }
    }

    public CompletableFuture<Boolean> storeUUID(String playerName, UUID uuid) {
        String lowerCasePlayerName = playerName.toLowerCase();
        return CompletableFuture.supplyAsync(() -> {
            if (!this.isOutdated(lowerCasePlayerName)) {
                this.plugin.debugMessage("Do not store " + playerName + " because it's not outdated.");
                return true;
            } else {
                FileConfiguration config;
                if (this.isStored(uuid)) {
                    String oldUser = this.getStoredUsername(uuid);
                    if (oldUser != null) {
                        config = this.findCorrectConfig(oldUser);
                        if (oldUser.equalsIgnoreCase(lowerCasePlayerName)) {
                            config.set(lowerCasePlayerName + ".updateTime", System.currentTimeMillis());
                            this.plugin.debugMessage("Already stored " + oldUser + ", so only updating time.");
                            return true;
                        }

                        this.plugin.debugMessage("Deleting old user '" + oldUser + "'!");
                        config.set(oldUser, null);
                    }
                }

                config = this.findCorrectConfig(lowerCasePlayerName);
                if (config == null) {
                    this.plugin.debugMessage("Could not store uuid " + uuid.toString() + " of player " + lowerCasePlayerName);
                    return false;
                } else {
                    config.set(lowerCasePlayerName + ".uuid", uuid.toString());
                    config.set(lowerCasePlayerName + ".updateTime", System.currentTimeMillis());
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    String realName = offlinePlayer.getName();
                    if (realName == null) {
                        try {
                            realName = UUIDManager.getPlayerName(uuid).get();
                        } catch (ExecutionException | InterruptedException var8) {
                            var8.printStackTrace();
                        }
                    }

                    config.set(lowerCasePlayerName + ".realName", realName);
                    this.plugin.debugMessage("Stored user '" + playerName + "' with uuid " + uuid + "!");
                    return true;
                }
            }
        });
    }

    public void transferUUIDs() {
        if (!this.plugin.getInternalPropertiesConfig().hasTransferredUUIDs()) {
            this.plugin.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Since the uuid storage have not been converted yet, I need to convert your UUID files to a new format.");
            this.plugin.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Converting UUID files to new format (3.7.1), this may take a while.");
            Iterator var1 = this.fileSuffixes.iterator();

            while(true) {
                FileConfiguration config;
                do {
                    if (!var1.hasNext()) {
                        this.plugin.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.GREEN + "All UUID files were properly converted. Please restart your server!");
                        this.plugin.getInternalPropertiesConfig().hasTransferredUUIDs(true);
                        return;
                    }

                    String suffix = (String)var1.next();
                    config = this.getConfig(suffix);
                } while(config == null);

                Set<String> names = config.getKeys(false);
                Iterator var5 = names.iterator();

                while(var5.hasNext()) {
                    String name = (String)var5.next();
                    String uuidString = config.getString(name + ".uuid");
                    long updateTime = config.getLong(name + ".updateTime", 0L);
                    config.set(name, null);
                    config.set(name.toLowerCase() + ".uuid", uuidString);
                    config.set(name.toLowerCase() + ".updateTime", updateTime);
                }
            }
        }
    }

    public List<String> getStoredPlayerNames() {
        List<String> playerNames = new ArrayList();
        Iterator var2 = this.configs.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, FileConfiguration> entry = (Entry)var2.next();
            FileConfiguration config = entry.getValue();
            playerNames.addAll(config.getKeys(false));
        }

        return playerNames;
    }
}
