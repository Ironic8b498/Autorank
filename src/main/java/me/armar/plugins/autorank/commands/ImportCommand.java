package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPromptCallback;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class ImportCommand extends AutorankCommand {
    private final Autorank plugin;

    public ImportCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission("autorank.import", sender)) {
            return true;
        } else {
            List<String> parameters = getArgumentOptions(args);
            boolean writeToGlobalDatabase = false; //made these boolean's not final
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
                int var12 = var11.length;

                for(int var13 = 0; var13 < var12; ++var13) {
                    OfflinePlayer offlinePlayer = var11[var13];
                    if (offlinePlayer.hasPlayedBefore() && offlinePlayer.getPlayer() != null) {
                        int vanillaTime = offlinePlayer.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);
                        this.plugin.getPlayTimeManager().addLocalPlayTime(TimeType.TOTAL_TIME, offlinePlayer.getUniqueId(), vanillaTime);
                        ++importedPlayers;
                    }
                }

                AutorankTools.sendDeserialize(sender, Lang.IMPORTED_DATA.getConfigValue(importedPlayers));
                return true;
            } else if (this.plugin.getPlayTimeStorageManager().getActiveStorageProviders().size() == 0) {
                AutorankTools.sendDeserialize(sender, Lang.THERE_ARE_NO_ACTIVE.getConfigValue());
                return true;
            } else if (writeToGlobalDatabase && !this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
                AutorankTools.sendDeserialize(sender, Lang.YOU_WANT.getConfigValue());

                return true;
            } else {
                boolean finalWriteToGlobalDatabase = writeToGlobalDatabase;
                boolean finalWriteToLocalDatabase = writeToLocalDatabase;
                boolean finalOverwriteGlobalDatabase = overwriteGlobalDatabase;
                boolean finalOverwriteLocalDatabase = overwriteLocalDatabase;
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    public void run() {
                        final String importFolder = ImportCommand.this.plugin.getDataFolder().getAbsolutePath() + File.separator + "imports" + File.separator;
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
                                Iterator var1 = filesToImport.entrySet().iterator();

                                label70:
                                while(var1.hasNext()) {
                                    Entry<String, TimeType> fileToImport = (Entry)var1.next();
                                    YamlConfiguration timeConfig = YamlConfiguration.loadConfiguration(new File(importFolder + fileToImport.getKey()));
                                    TimeType importedTimeType = fileToImport.getValue();
                                    int importedPlayers = 0;
                                    Iterator var6 = timeConfig.getKeys(false).iterator();

                                    while(true) {
                                        while(true) {
                                            while(var6.hasNext()) {
                                                String uuidString = (String)var6.next();
                                                if (uuidString == null) {
                                                    return;
                                                }

                                                int importedValue = timeConfig.getInt(uuidString);
                                                UUID importedPlayer = null;

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
                                                            ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(PlayTimeStorageProvider.StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                                        } else {
                                                            ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(PlayTimeStorageProvider.StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                                        }

                                                        if (finalOverwriteLocalDatabase) {
                                                            ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(PlayTimeStorageProvider.StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                                        } else {
                                                            ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(PlayTimeStorageProvider.StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                                        }
                                                    }
                                                } else if (finalWriteToGlobalDatabase) {
                                                    if (finalOverwriteGlobalDatabase) {
                                                        ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(PlayTimeStorageProvider.StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                                    } else {
                                                        ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(PlayTimeStorageProvider.StorageType.DATABASE, importedTimeType, importedPlayer, importedValue);
                                                    }
                                                } else if (finalOverwriteLocalDatabase) {
                                                    ImportCommand.this.plugin.getPlayTimeStorageManager().setPlayerTime(PlayTimeStorageProvider.StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                                } else {
                                                    ImportCommand.this.plugin.getPlayTimeStorageManager().addPlayerTime(PlayTimeStorageProvider.StorageType.FLAT_FILE, importedTimeType, importedPlayer, importedValue);
                                                }
                                            }

                                            if (importedPlayers == 0) {
                                                AutorankTools.sendDeserialize(sender, Lang.COULD_NOT_IMPORT.getConfigValue(importedTimeType));
                                            }
                                            continue label70;
                                        }
                                    }
                                }

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
