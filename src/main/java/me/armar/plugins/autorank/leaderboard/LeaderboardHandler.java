package me.armar.plugins.autorank.leaderboard;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LeaderboardHandler {
    private static final double LEADERBOARD_TIME_VALID = 30.0D;
    private final Autorank plugin;
    private String layout = "&6&r | &b&p - &7&d %day%, &h %hour% and &m %minute%.";
    private int leaderboardLength = 10;

    public LeaderboardHandler(Autorank plugin) {
        this.plugin = plugin;
        this.leaderboardLength = plugin.getSettingsConfig().getLeaderboardLength();
        this.layout = plugin.getSettingsConfig().getLeaderboardLayout();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator<Entry<K, V>>() {
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            Entry<K, V> entry = (Entry)var3.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public void broadcastLeaderboard(final TimeType type) {
        if (this.shouldUpdateLeaderboard(type)) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                public void run() {
                    LeaderboardHandler.this.plugin.debugMessage("Updating leaderboard because it's outdated");
                    LeaderboardHandler.this.updateLeaderboard(type);
                    Iterator var1 = LeaderboardHandler.this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type).iterator();

                    while(var1.hasNext()) {
                        String msg = (String)var1.next();
                        LeaderboardHandler.this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }

                }
            });
        } else {
            Iterator var2 = this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type).iterator();

            while(var2.hasNext()) {
                String msg = (String)var2.next();
                this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }

    }

    private Map<UUID, Integer> getSortedTimesByUUID(TimeType type) {
        PlayTimeStorageProvider primaryStorageProvider = this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider();
        List<UUID> uuids = primaryStorageProvider.getStoredPlayers(type);
        HashMap<UUID, Integer> times = new HashMap();
        int size = uuids.size();
        int lastSentPercentage = 0;

        for(int i = 0; i < uuids.size(); ++i) {
            UUID uuid = uuids.get(i);
            if (uuid != null && !this.plugin.getPlayerChecker().isExemptedFromLeaderboard(uuid)) {
                DecimalFormat df = new DecimalFormat("#.#");
                double percentage = (double)i * 1.0D / (double)size * 100.0D;
                int floored = (int)Math.floor(percentage);
                if (lastSentPercentage != floored && floored % 10 == 0) {
                    lastSentPercentage = floored;
                    this.plugin.debugMessage("Autorank leaderboard update is at " + df.format(percentage) + "%.");
                }

                if (type == TimeType.TOTAL_TIME) {
                    if (this.plugin.getSettingsConfig().useGlobalTimeInLeaderboard() && this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
                        try {
                            times.put(uuid, this.plugin.getPlayTimeManager().getGlobalPlayTime(type, uuid).get());
                        } catch (ExecutionException | InterruptedException var14) {
                            var14.printStackTrace();
                        }
                    } else {
                        try {
                            times.put(uuid, primaryStorageProvider.getPlayerTime(type, uuid).get());
                        } catch (ExecutionException | InterruptedException var16) {
                            var16.printStackTrace();
                        }
                    }
                } else {
                    try {
                        times.put(uuid, primaryStorageProvider.getPlayerTime(type, uuid).get());
                    } catch (ExecutionException | InterruptedException var15) {
                        var15.printStackTrace();
                    }
                }
            }
        }

        return sortByValue(times);
    }

    private Map<String, Integer> getSortedTimesByNames(TimeType type) {
        PlayTimeStorageProvider primaryStorageProvider = this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider();
        List<String> playerNames = this.plugin.getUUIDStorage().getStoredPlayerNames();
        Map<String, Integer> times = new HashMap();
        int size = playerNames.size();
        int lastSentPercentage = 0;

        for(int i = 0; i < playerNames.size(); ++i) {
            String playerName = playerNames.get(i);
            if (playerName != null) {
                UUID uuid = null;

                try {
                    uuid = UUIDManager.getUUID(playerName).get();
                } catch (ExecutionException | InterruptedException var16) {
                    var16.printStackTrace();
                }

                if (uuid != null && !this.plugin.getPlayerChecker().isExemptedFromLeaderboard(uuid)) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    double percentage = (double)i * 1.0D / (double)size * 100.0D;
                    int floored = (int)Math.floor(percentage);
                    if (lastSentPercentage != floored) {
                        lastSentPercentage = floored;
                        this.plugin.debugMessage("Autorank leaderboard update is at " + df.format(percentage) + "%.");
                    }

                    if (type == TimeType.TOTAL_TIME) {
                        if (this.plugin.getSettingsConfig().useGlobalTimeInLeaderboard() && this.plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
                            try {
                                times.put(playerName, this.plugin.getPlayTimeManager().getGlobalPlayTime(type, uuid).get());
                            } catch (ExecutionException | InterruptedException var15) {
                                var15.printStackTrace();
                            }
                        } else {
                            try {
                                times.put(playerName, primaryStorageProvider.getPlayerTime(type, uuid).get());
                            } catch (ExecutionException | InterruptedException var18) {
                                var18.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            times.put(playerName, primaryStorageProvider.getPlayerTime(type, uuid).get());
                        } catch (ExecutionException | InterruptedException var17) {
                            var17.printStackTrace();
                        }
                    }
                }
            }
        }

        return sortByValue(times);
    }

    public void sendLeaderboard(final CommandSender sender, final TimeType type) {
        if (this.shouldUpdateLeaderboard(type)) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                public void run() {
                    LeaderboardHandler.this.plugin.debugMessage("Updating leaderboard because it's outdated");
                    LeaderboardHandler.this.updateLeaderboard(type);
                    LeaderboardHandler.this.sendMessages(sender, type);
                }
            });
        } else {
            this.sendMessages(sender, type);
        }

    }

    public void sendMessages(CommandSender sender, TimeType type) {
        Iterator var3 = this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type).iterator();

        while(var3.hasNext()) {
            String msg = (String)var3.next();
            AutorankTools.sendColoredMessage(sender, msg);
        }

    }

    private boolean shouldUpdateLeaderboard(TimeType type) {
        if ((double)(System.currentTimeMillis() - this.plugin.getInternalPropertiesConfig().getLeaderboardLastUpdateTime(type)) > 1800000.0D) {
            return true;
        } else {
            return this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type).size() <= 2;
        }
    }

    public void updateAllLeaderboards() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            public void run() {
                LeaderboardHandler.this.plugin.debugMessage("Updating all leaderboards forcefully");
                TimeType[] var1 = TimeType.values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    TimeType type = var1[var3];
                    if (LeaderboardHandler.this.shouldUpdateLeaderboard(type)) {
                        LeaderboardHandler.this.updateLeaderboard(type);
                    }
                }

            }
        });
    }

    public void updateLeaderboard(TimeType type) {
        this.plugin.debugMessage(ChatColor.BLUE + "Updating leaderboard '" + type.toString() + "'!");
        List<String> stringList = new ArrayList();
        if (type == TimeType.TOTAL_TIME) {
            stringList.add(Lang.LEADERBOARD_HEADER_ALL_TIME.getConfigValue());
        } else if (type == TimeType.DAILY_TIME) {
            stringList.add(Lang.LEADERBOARD_HEADER_DAILY.getConfigValue());
        } else if (type == TimeType.WEEKLY_TIME) {
            stringList.add(Lang.LEADERBOARD_HEADER_WEEKLY.getConfigValue());
        } else if (type == TimeType.MONTHLY_TIME) {
            stringList.add(Lang.LEADERBOARD_HEADER_MONTHLY.getConfigValue());
        }

        AutorankLeaderboard finalLeaderboard = null;

        try {
            finalLeaderboard = this.getSortedLeaderboard(type).get();
        } catch (ExecutionException | InterruptedException var12) {
            var12.printStackTrace();
        }

        if (finalLeaderboard != null) {
            Iterator<Entry<String, Integer>> iterator = finalLeaderboard.getLeaderboard().entrySet().iterator();

            for(int i = 0; i < this.leaderboardLength && iterator.hasNext(); ++i) {
                Entry<String, Integer> entry = iterator.next();
                int time = entry.getValue();
                String message = this.layout.replace("&p", entry.getKey());
                int days = time / 1440;
                int hours = (time - days * 1440) / 60;
                int minutes = time - days * 1440 - hours * 60;
                message = message.replace("&r", Integer.toString(i + 1));
                message = message.replace("&tm", Integer.toString(time));
                message = message.replace("&th", Integer.toString(time / 60));
                message = message.replace("&d", Integer.toString(days));
                time -= time / 1440 * 1440;
                message = message.replace("&h", Integer.toString(hours));
                int var10000 = time - time / 60 * 60;
                message = message.replace("&m", Integer.toString(minutes));
                message = ChatColor.translateAlternateColorCodes('&', message);
                if (days <= 1 && days != 0) {
                    message = message.replace("%day%", Lang.DAY_SINGULAR.getConfigValue());
                } else {
                    message = message.replace("%day%", Lang.DAY_PLURAL.getConfigValue());
                }

                if (hours <= 1 && hours != 0) {
                    message = message.replace("%hour%", Lang.HOUR_SINGULAR.getConfigValue());
                } else {
                    message = message.replace("%hour%", Lang.HOUR_PLURAL.getConfigValue());
                }

                if (minutes <= 1 && minutes != 0) {
                    message = message.replace("%minute%", Lang.MINUTE_SINGULAR.getConfigValue());
                } else {
                    message = message.replace("%minute%", Lang.MINUTE_PLURAL.getConfigValue());
                }

                stringList.add(message);
            }

            stringList.add(Lang.LEADERBOARD_FOOTER.getConfigValue());
            this.plugin.getInternalPropertiesConfig().setCachedLeaderboard(type, stringList);
            this.plugin.getInternalPropertiesConfig().setLeaderboardLastUpdateTime(type, System.currentTimeMillis());
        }
    }

    private CompletableFuture<AutorankLeaderboard> getSortedLeaderboard(TimeType type) {
        return CompletableFuture.supplyAsync(() -> {
            AutorankLeaderboard finalLeaderboard = new AutorankLeaderboard(type);
            Map<UUID, Integer> sortedPlaytimes = this.getSortedTimesByUUID(type);
            Iterator<Entry<UUID, Integer>> itr = sortedPlaytimes.entrySet().iterator();
            this.plugin.debugMessage("Size leaderboard: " + sortedPlaytimes.size());

            for(int i = 0; i < this.leaderboardLength && itr.hasNext(); ++i) {
                Entry<UUID, Integer> entry = itr.next();
                UUID uuid = entry.getKey();
                String name = null;

                try {
                    name = UUIDManager.getPlayerName(uuid).get();
                } catch (ExecutionException | InterruptedException var10) {
                    var10.printStackTrace();
                }

                if (name != null) {
                    finalLeaderboard.add(name, entry.getValue());
                }
            }

            finalLeaderboard.sortLeaderboard();
            return finalLeaderboard;
        });
    }
}
