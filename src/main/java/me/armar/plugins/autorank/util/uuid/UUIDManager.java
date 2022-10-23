package me.armar.plugins.autorank.util.uuid;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UUIDManager {
    private static final boolean useCache = true;
    private static final Autorank plugin = (Autorank)Bukkit.getPluginManager().getPlugin("Autorank");

    public UUIDManager() {
    }

    public static CompletableFuture<String> getPlayerName(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (uuid == null) {
                return null;
            } else {
                try {
                    Map<UUID, String> names = getPlayerNames(Collections.singletonList(uuid)).get();
                    Iterator var2 = names.entrySet().iterator();
                    if (var2.hasNext()) {
                        Entry<UUID, String> entry = (Entry)var2.next();
                        return entry.getValue();
                    }
                } catch (ExecutionException | InterruptedException var4) {
                    var4.printStackTrace();
                }

                return null;
            }
        });
    }

    public static CompletableFuture<Map<UUID, String>> getPlayerNames(List<UUID> uuids) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> uuidsToSearch = new ArrayList(uuids);
            Map<UUID, String> cachedData = new HashMap();
            Iterator var3 = uuids.iterator();

            UUID uuid;
            while(var3.hasNext()) {
                uuid = (UUID)var3.next();
                String playerName = null;

                try {
                    playerName = plugin.getUUIDStorage().getUsername(uuid).get();
                } catch (ExecutionException | InterruptedException var8) {
                    continue;
                }

                if (playerName != null) {
                    cachedData.put(uuid, playerName);
                    uuidsToSearch.remove(uuid);
                }
            }

            if (!uuids.isEmpty()) {
                NameFetcher fetcher = new NameFetcher(uuidsToSearch, plugin);
                uuid = null;

                try {
                    Map<UUID, String> response = fetcher.call();
                    cachedData.putAll(response);
                } catch (Exception var7) {
                    if (var7 instanceof IOException) {
                        Bukkit.getLogger().warning("Tried to contact Mojang page for UUID lookup but failed.");
                    }

                    var7.printStackTrace();
                }
            }

            return cachedData;
        });
    }

    public static CompletableFuture<UUID> getUUID(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            if (playerName == null) {
                return null;
            } else {
                try {
                    Map<String, UUID> uuids = getUUIDs(Collections.singletonList(playerName)).get();
                    Iterator var2 = uuids.entrySet().iterator();
                    if (var2.hasNext()) {
                        Entry<String, UUID> entry = (Entry)var2.next();
                        return entry.getValue();
                    }
                } catch (ExecutionException | InterruptedException var4) {
                    var4.printStackTrace();
                }

                return null;
            }
        });
    }

    public static CompletableFuture<Map<String, UUID>> getUUIDs(List<String> playerNames) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> playerNamesToSearch = new ArrayList(playerNames);
            Map<String, UUID> cachedData = new HashMap();
            Iterator var3 = playerNames.iterator();

            String playerName;
            while(var3.hasNext()) {
                playerName = (String)var3.next();
                UUID storedUUID = null;

                try {
                    storedUUID = plugin.getUUIDStorage().getUUID(playerName).get();
                } catch (ExecutionException | InterruptedException var8) {
                    continue;
                }

                if (storedUUID != null) {
                    cachedData.put(playerName, storedUUID);
                    playerNamesToSearch.remove(playerName);
                }
            }

            if (!playerNamesToSearch.isEmpty()) {
                UUIDFetcher fetcher = new UUIDFetcher(playerNamesToSearch);
                playerName = null;

                try {
                    Map<String, UUID> response = fetcher.call();
                    cachedData.putAll(response);
                } catch (Exception var7) {
                    if (var7 instanceof IOException) {
                        Bukkit.getLogger().warning("Tried to contact Mojang page for UUID lookup but failed.");
                    }

                    var7.printStackTrace();
                }
            }

            return cachedData;
        });
    }
}
