package me.armar.plugins.autorank.util.uuid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;

public class UUIDManager {
    private static final Autorank plugin = (Autorank)Bukkit.getPluginManager().getPlugin("Autorank");

    public UUIDManager() {
    }

    public static CompletableFuture<String> getPlayerName(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (uuid != null) {
                try {
                    Map<UUID, String> names = getPlayerNames(Collections.singletonList(uuid)).get();
                    Iterator<Map.Entry<UUID, String>> var2 = names.entrySet().iterator();
                    if (var2.hasNext()) {
                        Map.Entry<UUID, String> entry = var2.next();
                        return entry.getValue();
                    }
                } catch (InterruptedException | ExecutionException var4) {
                    var4.printStackTrace();
                }
            }

            return null;
        });
    }

    public static CompletableFuture<Map<UUID, String>> getPlayerNames(List<UUID> uuids) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> uuidsToSearch = new ArrayList<>(uuids);
            Map<UUID, String> cachedData = new HashMap<>();

            for(UUID uuid : uuids) {
                String playerName;
                try {
                    assert plugin != null;

                    playerName = plugin.getUUIDStorage().getUsername(uuid).get();
                } catch (InterruptedException | ExecutionException var8) {
                    continue;
                }

                if (playerName != null) {
                    cachedData.put(uuid, playerName);
                    uuidsToSearch.remove(uuid);
                }
            }

            if (!uuids.isEmpty()) {
                NameFetcher fetcher = new NameFetcher(uuidsToSearch, plugin);

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
            if (playerName != null) {
                try {
                    Map<String, UUID> uuids = getUUIDs(Collections.singletonList(playerName)).get();
                    Iterator<Map.Entry<String, UUID>> var2 = uuids.entrySet().iterator();
                    if (var2.hasNext()) {
                        Map.Entry<String, UUID> entry = var2.next();
                        return entry.getValue();
                    }
                } catch (InterruptedException | ExecutionException var4) {
                    var4.printStackTrace();
                }
            }

            return null;
        });
    }

    public static CompletableFuture<Map<String, UUID>> getUUIDs(List<String> playerNames) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> playerNamesToSearch = new ArrayList<>(playerNames);
            Map<String, UUID> cachedData = new HashMap<>();

            for(String playerName : playerNames) {
                UUID storedUUID;
                try {
                    assert plugin != null;

                    storedUUID = plugin.getUUIDStorage().getUUID(playerName).get();
                } catch (InterruptedException | ExecutionException var8) {
                    continue;
                }

                if (storedUUID != null) {
                    cachedData.put(playerName, storedUUID);
                    playerNamesToSearch.remove(playerName);
                }
            }

            if (!playerNamesToSearch.isEmpty()) {
                UUIDFetcher fetcher = new UUIDFetcher(playerNamesToSearch);

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
