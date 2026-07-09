package me.armar.plugins.autorank.leaderboard;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider.StorageType;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LeaderboardHandler {
    private static final double LEADERBOARD_TIME_VALID = 30.0F;
    private final Autorank plugin;
    private String layout = "<GOLD>&r | <AQUA>&p - <GRAY>&d %day%, &h %hour% and &m %minute%.";
    private int leaderboardLength = 10;

    public LeaderboardHandler(Autorank plugin) {
        this.plugin = plugin;
        this.leaderboardLength = plugin.getSettingsConfig().getLeaderboardLength();
        this.layout = plugin.getSettingsConfig().getLeaderboardLayout();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<>();

        for(Map.Entry<K, V> entry : list) {
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

                    for(String msg : LeaderboardHandler.this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type)) {
                        LeaderboardHandler.this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }

                }
            });
        } else {
            for(String msg : this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type)) {
                this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }

    }

    private Map<UUID, Integer> getSortedTimesByUUID(TimeType type) {
        PlayTimeStorageProvider primaryStorageProvider = this.plugin.getPlayTimeStorageManager().getPrimaryStorageProvider();
        List<UUID> uuids = primaryStorageProvider.getStoredPlayers(type);
        HashMap<UUID, Integer> times = new HashMap<>();
        int size = uuids.size();
        int lastSentPercentage = 0;

        for(int i = 0; i < uuids.size(); ++i) {
            UUID uuid = uuids.get(i);
            if (uuid != null && !this.plugin.getPlayerChecker().isExemptedFromLeaderboard(uuid)) {
                DecimalFormat df = new DecimalFormat("#.#");
                double percentage = (double)i / (double)size * (double)100.0F;
                int floored = (int)Math.floor(percentage);
                if (lastSentPercentage != floored && floored % 10 == 0) {
                    lastSentPercentage = floored;
                    Autorank var10000 = this.plugin;
                    String var10001 = df.format(percentage);
                    var10000.debugMessage("Autorank leaderboard update is at " + var10001 + "%.");
                }

                if (type == TimeType.TOTAL_TIME) {
                    if (this.plugin.getSettingsConfig().useGlobalTimeInLeaderboard() && this.plugin.getPlayTimeStorageManager().isStorageTypeActive(StorageType.DATABASE)) {
                        try {
                            times.put(uuid, this.plugin.getPlayTimeManager().getGlobalPlayTime(type, uuid).get());
                        } catch (InterruptedException | ExecutionException var14) {
                            var14.printStackTrace();
                        }
                    } else {
                        try {
                            times.put(uuid, primaryStorageProvider.getPlayerTime(type, uuid).get());
                        } catch (InterruptedException | ExecutionException var16) {
                            var16.printStackTrace();
                        }
                    }
                } else {
                    try {
                        times.put(uuid, primaryStorageProvider.getPlayerTime(type, uuid).get());
                    } catch (InterruptedException | ExecutionException var15) {
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
        Map<String, Integer> times = new HashMap<>();
        int size = playerNames.size();
        int lastSentPercentage = 0;

        for(int i = 0; i < playerNames.size(); ++i) {
            String playerName = playerNames.get(i);
            if (playerName != null) {
                UUID uuid = null;

                try {
                    uuid = UUIDManager.getUUID(playerName).get();
                } catch (InterruptedException | ExecutionException var16) {
                    var16.printStackTrace();
                }

                if (uuid != null && !this.plugin.getPlayerChecker().isExemptedFromLeaderboard(uuid)) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    double percentage = (double)i / (double)size * (double)100.0F;
                    int floored = (int)Math.floor(percentage);
                    if (lastSentPercentage != floored) {
                        lastSentPercentage = floored;
                        Autorank var10000 = this.plugin;
                        String var10001 = df.format(percentage);
                        var10000.debugMessage("Autorank leaderboard update is at " + var10001 + "%.");
                    }

                    if (type == TimeType.TOTAL_TIME) {
                        if (this.plugin.getSettingsConfig().useGlobalTimeInLeaderboard() && this.plugin.getPlayTimeStorageManager().isStorageTypeActive(StorageType.DATABASE)) {
                            try {
                                times.put(playerName, this.plugin.getPlayTimeManager().getGlobalPlayTime(type, uuid).get());
                            } catch (InterruptedException | ExecutionException var15) {
                                var15.printStackTrace();
                            }
                        } else {
                            try {
                                times.put(playerName, primaryStorageProvider.getPlayerTime(type, uuid).get());
                            } catch (InterruptedException | ExecutionException var18) {
                                var18.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            times.put(playerName, primaryStorageProvider.getPlayerTime(type, uuid).get());
                        } catch (InterruptedException | ExecutionException var17) {
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
        for(String msg : this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type)) {
            AutorankTools.sendColoredMessage(sender, msg);
        }

    }

    private boolean shouldUpdateLeaderboard(TimeType type) {
        if ((double)(System.currentTimeMillis() - this.plugin.getInternalPropertiesConfig().getLeaderboardLastUpdateTime(type)) > (double)1800000.0F) {
            return true;
        } else {
            return this.plugin.getInternalPropertiesConfig().getCachedLeaderboard(type).size() <= 2;
        }
    }

    public void updateAllLeaderboards() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            public void run() {
                LeaderboardHandler.this.plugin.debugMessage("Updating all leaderboards forcefully");

                for(TimeType type : TimeType.values()) {
                    if (LeaderboardHandler.this.shouldUpdateLeaderboard(type)) {
                        LeaderboardHandler.this.updateLeaderboard(type);
                    }
                }

            }
        });
    }

    public void updateLeaderboard(TimeType type) {
        ChatColor var10001 = ChatColor.BLUE;
        this.plugin.debugMessage(var10001 + "Updating leaderboard '" + type.toString() + "'!");
        List<String> stringList = new ArrayList<>();
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
        } catch (InterruptedException | ExecutionException var12) {
            var12.printStackTrace();
        }

        if (finalLeaderboard != null) {
            Iterator<Map.Entry<String, Integer>> iterator = finalLeaderboard.getLeaderboard().entrySet().iterator();

            for(int i = 0; i < this.leaderboardLength && iterator.hasNext(); ++i) {
                Map.Entry<String, Integer> entry = iterator.next();
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
            Iterator<Map.Entry<UUID, Integer>> itr = sortedPlaytimes.entrySet().iterator();
            this.plugin.debugMessage("Size leaderboard: " + sortedPlaytimes.size());

            for(int i = 0; i < this.leaderboardLength && itr.hasNext(); ++i) {
                Map.Entry<UUID, Integer> entry = itr.next();
                UUID uuid = entry.getKey();
                String name = null;

                try {
                    name = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException var10) {
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
