package me.armar.plugins.autorank.commands;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPromptCallback;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider.StorageType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ImportCommand extends AutorankCommand {
    private final Autorank plugin;

    public ImportCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            List<String> parameters = getArgumentOptions(args);
            boolean writeToGlobalDatabase = false;
            boolean writeToLocalDatabase = true;
            boolean overwriteGlobalDatabase = false;
            boolean overwriteLocalDatabase = false;
            if (parameters.contains("db-only")) {
                writeToGlobalDatabase = true;
                writeToLocalDatabase = false;
            } else if (parameters.contains("db")) {
                writeToGlobalDatabase = true;
            }

            if (parameters.contains("overwrite-all")) {
                overwriteGlobalDatabase = true;
                overwriteLocalDatabase = true;
                writeToGlobalDatabase = true;
                writeToLocalDatabase = true;
            } else if (parameters.contains("overwrite-flatfile")) {
                overwriteLocalDatabase = true;
                writeToLocalDatabase = true;
            } else if (parameters.contains("overwrite-db")) {
                overwriteGlobalDatabase = true;
                writeToGlobalDatabase = true;
            }

            if (args.length > 1 && args[1] != null && args[1].equalsIgnoreCase("vanilladata")) {
                int importedPlayers = 0;
                OfflinePlayer[] var11 = this.plugin.getServer().getOfflinePlayers();

                for(OfflinePlayer offlinePlayer : var11) {
                    if (offlinePlayer.hasPlayedBefore() && offlinePlayer.getPlayer() != null) {
                        int vanillaTime = offlinePlayer.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);
                        this.plugin.getPlayTimeManager().addLocalPlayTime(TimeType.TOTAL_TIME, offlinePlayer.getUniqueId(), vanillaTime);
                        ++importedPlayers;
                    }
                }

                AutorankTools.sendDeserialize(sender, Lang.IMPORTED_DATA.getConfigValue(importedPlayers));
                return true;
            } else if (this.plugin.getPlayTimeStorageManager().getActiveStorageProviders().isEmpty()) {
                AutorankTools.sendDeserialize(sender, Lang.THERE_ARE_NO_ACTIVE.getConfigValue());
                return true;
            } else if (writeToGlobalDatabase && !this.plugin.getPlayTimeStorageManager().isStorageTypeActive(StorageType.DATABASE)) {
                AutorankTools.sendDeserialize(sender, Lang.YOU_WANT.getConfigValue());
                return true;
            } else {
                boolean finalWriteToGlobalDatabase = writeToGlobalDatabase;
                boolean finalWriteToLocalDatabase = writeToLocalDatabase;
                boolean finalOverwriteGlobalDatabase = overwriteGlobalDatabase;
                boolean finalOverwriteLocalDatabase = overwriteLocalDatabase;
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    public void run() {
                        String var10000 = ImportCommand.this.plugin.getDataFolder().getAbsolutePath();
                        final String importFolder = var10000 + File.separator + "imports" + File.separator;
                        final Map<String, TimeType> filesToImport = new HashMap<String, TimeType>() {
                            {
                                this.put("Total_time.yml", TimeType.TOTAL_TIME);
                                this.put("Daily_time.yml", TimeType.DAILY_TIME);
                                this.put("Weekly_time.yml", TimeType.WEEKLY_TIME);
                                this.put("Monthly_time.yml", TimeType.MONTHLY_TIME);
                            }
                        };
                        if (finalWriteToGlobalDatabase && finalWriteToLocalDatabase) {
                            if (finalOverwriteGlobalDatabase && finalOverwriteLocalDatabase) {
                                AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_BOTH.getConfigValue());
                            } else {
                                if (finalOverwriteGlobalDatabase) {
                                    AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_OVERRIDING_GLOBAL.getConfigValue());
                                } else {
                                    AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_ADDING_GLOBAL.getConfigValue());
                                }

                                if (finalOverwriteLocalDatabase) {
                                    AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_OVERRIDING_LOCAL.getConfigValue());
                                } else {
                                    AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_ADDING_LOCAL.getConfigValue());
                                }
                            }
                        } else if (finalWriteToGlobalDatabase) {
                            if (finalOverwriteGlobalDatabase) {
                                AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_OVERRIDING_GLOBAL.getConfigValue());
                            } else {
                                AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_ADDING_GLOBAL.getConfigValue());
                            }
                        } else if (finalOverwriteLocalDatabase) {
                            AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_OVERRIDING_LOCAL.getConfigValue());
                        } else {
                            AutorankTools.sendDeserialize(sender, Lang.IMPORTING_DATA_ADDING_LOCAL.getConfigValue());
                        }

                        AutorankConversation.fromFirstPrompt(new ConfirmPrompt(null, new ConfirmPromptCallback() {
                            public void promptConfirmed() {
                                for(Map.Entry<String, TimeType> fileToImport : filesToImport.entrySet()) {
                                    String var10002 = importFolder;
                                    YamlConfiguration timeConfig = YamlConfiguration.loadConfiguration(new File(var10002 + fileToImport.getKey()));
                                    TimeType importedTimeType = fileToImport.getValue();
                                    int importedPlayers = 0;

                                    for(String uuidString : timeConfig.getKeys(false)) {
                                        if (uuidString == null) {
                                            return;
                                        }

                                        int importedValue = timeConfig.getInt(uuidString);

                                        UUID importedPlayer;
                                        try {
                                            importedPlayer = UUID.fromString(uuidString);
                                        } catch (IllegalArgumentException var11) {
                                            return;
                                        }

                                        ++importedPlayers;
                                        if (finalWriteToLocalDatabase && finalWriteToGlobalDatabase) {
                                            if (finalOverwriteGlobalDatabase && finalOverwriteLocalDatabase) {
                                                ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(importedTimeType, importedPlayer, importedValue);
                                            } else {
                                                if (finalOverwriteGlobalDatabase) {
                                                    ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                                } else {
                                                    ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                                }

                                                if (finalOverwriteLocalDatabase) {
                                                    ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                                } else {
                                                    ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                                }
                                            }
                                        } else if (finalWriteToGlobalDatabase) {
                                            if (finalOverwriteGlobalDatabase) {
                                                ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                            } else {
                                                ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                            }
                                        } else if (finalOverwriteLocalDatabase) {
                                            ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                        } else {
                                            ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                        }
                                    }

                                    if (importedPlayers == 0) {
                                        AutorankTools.sendDeserialize(sender, Lang.COULD_NOT_IMPORT.getConfigValue(importedTimeType));
                                    }
                                }

                                ImportCommand.this.plugin.getInternalPropertiesConfig().saveConfig();
                                File file = new File(ImportCommand.this.plugin.getDataFolder(), "internalprops.yml");
                                file.delete();
                                ImportCommand.this.plugin.getInternalPropertiesConfig().loadConfig();
                                AutorankTools.sendDeserialize(sender, Lang.STORAGE_IMPORTED.getConfigValue());
                            }

                            public void promptDenied() {
                                AutorankTools.sendDeserialize(sender, Lang.IMPORTED_OPERATION.getConfigValue());
                            }
                        })).startConversationAsSender(sender);
                    }
                });
                return true;
            }
        }
    }

    public String getDescription() {
        return "Import time data from your flatfiles into the system.";
    }

    public String getPermission() {
        return "autorank.import";
    }

    public String getUsage() {
        return "/ar import <parameters>";
    }
}
