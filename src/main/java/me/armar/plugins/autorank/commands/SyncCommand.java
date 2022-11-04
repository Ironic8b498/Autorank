package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SyncCommand extends AutorankCommand {
    private final Autorank plugin;

    public SyncCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.sync", sender)) {
            return true;
        } else if (args.length > 1 && args[1].equalsIgnoreCase("stats")) {
            AutorankTools.sendDeserialize(sender, Lang.YOU_PROBARLY.getConfigValue());
            return true;
        } else {
            boolean reverse = args.length > 1 && args[1].equalsIgnoreCase("reverse");

            if (!this.plugin.getSettingsConfig().useMySQL()) {
                AutorankTools.sendDeserialize(sender, Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
                return true;
            } else if (!this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE)) {
                AutorankTools.sendDeserialize(sender, Lang.THERE_IS_NO_ACTIVE_FLATFILE.getConfigValue());
                return true;
            } else {
                AutorankTools.sendDeserialize(sender, Lang.YOU_DONT_HAVE.getConfigValue());
                PlayTimeStorageProvider flatfileStorageProvider = this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE);
                PlayTimeStorageProvider databaseStorageProvider = this.plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE);
                if (reverse) {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        int count = 0;
                        TimeType[] var4 = TimeType.values();
                        int var5 = var4.length;

                        for(int var6 = 0; var6 < var5; ++var6) {
                            TimeType timeType = var4[var6];
                            List<UUID> storedUUIDsFlatfile = flatfileStorageProvider.getStoredPlayers(timeType);
                            Iterator var9 = storedUUIDsFlatfile.iterator();

                            while(var9.hasNext()) {
                                UUID uuid = (UUID)var9.next();
                                int databaseValue = 0;

                                try {
                                    databaseValue = databaseStorageProvider.getPlayerTime(timeType, uuid).get();
                                } catch (ExecutionException | InterruptedException var13) {
                                    var13.printStackTrace();
                                }

                                if (databaseValue > 0) {
                                    flatfileStorageProvider.setPlayerTime(timeType, uuid, databaseValue);
                                    ++count;
                                }
                            }
                        }

                        AutorankTools.sendDeserialize(sender, Lang.SUCCESSFULLY_UPDATED.getConfigValue(count));
                    });
                } else {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        TimeType[] var3 = TimeType.values();
                        int var4 = var3.length;

                        for(int var5 = 0; var5 < var4; ++var5) {
                            TimeType timeType = var3[var5];
                            List<UUID> storedUUIDsFlatfile = flatfileStorageProvider.getStoredPlayers(timeType);
                            Iterator var8 = storedUUIDsFlatfile.iterator();

                            while(var8.hasNext()) {
                                UUID uuid = (UUID)var8.next();
                                int flatfileValue = 0;

                                try {
                                    flatfileValue = flatfileStorageProvider.getPlayerTime(timeType, uuid).get();
                                } catch (ExecutionException | InterruptedException var12) {
                                    var12.printStackTrace();
                                }

                                if (flatfileValue > 0) {
                                    databaseStorageProvider.addPlayerTime(timeType, uuid, flatfileValue);
                                }
                            }
                        }

                        AutorankTools.sendDeserialize(sender, Lang.SUCCESSFULLY_UPDATED_MYSQL.getConfigValue());
                    });
                }

                return true;
            }
        }
    }

    public String getDescription() {
        return "Sync MySQL database with server (Use only once per server).";
    }

    public String getPermission() {
        return "autorank.sync";
    }

    public String getUsage() {
        return "/ar sync";
    }
}
