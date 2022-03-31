package me.armar.plugins.autorank.playerchecker;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerChecker {
    private final Autorank plugin;

    public PlayerChecker(Autorank plugin) {
        this.plugin = plugin;
    }

    public boolean checkPlayer(UUID uuid) {
        if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
            this.plugin.debugMessage("Player '" + uuid.toString() + "' is exempted from automated checking, so we don't check their path progress!");
            return false;
        } else {
            this.plugin.getPathManager().autoAssignPaths(uuid);
            List<Path> activePaths = this.plugin.getPathManager().getActivePaths(uuid);
            boolean result = false;
            Iterator var4 = activePaths.iterator();

            while(var4.hasNext()) {
                Path activePath = (Path)var4.next();
                if (activePath.checkPathProgress(uuid)) {
                    result = true;
                }
            }

            return result;
        }
    }

    public void doOfflineExemptionChecks(Player player) {
        this.doLeaderboardExemptCheck(player);
        this.doAutomaticCheckingExemptionCheck(player);
        this.doTimeAdditionExemptionCheck(player);
    }

    public void doLeaderboardExemptCheck(Player player) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.setLeaderboardExemption(player.getUniqueId(), player.hasPermission("autorank.leaderboard.exclude"));
        });
    }

    public void doAutomaticCheckingExemptionCheck(Player player) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.setAutoCheckingExemption(player.getUniqueId(), AutorankTools.isExcludedFromRanking(player));
        });
    }

    public void doTimeAdditionExemptionCheck(Player player) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
            s.setTimeAdditionExemption(player.getUniqueId(), player.hasPermission("autorank.timeexclude"));
        });
    }

    public boolean isExemptedFromLeaderboard(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        return player != null ? player.hasPermission("autorank.leaderboard.exclude") : this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> {
            return s.hasLeaderboardExemption(uuid);
        }).orElse(false);
    }

    public boolean isExemptedFromAutomaticChecking(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        return player != null ? AutorankTools.isExcludedFromRanking(player) : this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> {
            return s.hasAutoCheckingExemption(uuid);
        }).orElse(false);
    }

    public boolean isExemptedFromTimeAddition(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        return player != null ? player.hasPermission("autorank.timeexclude") : this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> {
            return s.hasTimeAdditionExemption(uuid);
        }).orElse(false);
    }

    public List<String> formatRequirementsToList(List<CompositeRequirement> holders, List<CompositeRequirement> metRequirements) {
        List<String> messages = new ArrayList();
        messages.add(ChatColor.GRAY + " ------------ ");

        for(int i = 0; i < holders.size(); ++i) {
            CompositeRequirement holder = holders.get(i);
            if (holder != null) {
                StringBuilder message = new StringBuilder("     " + ChatColor.GOLD + (i + 1) + ". ");
                if (metRequirements.contains(holder)) {
                    message.append(ChatColor.GREEN).append(holder.getDescription()).append(ChatColor.DARK_AQUA).append(" (").append(Lang.DONE_MARKER.getConfigValue()).append(")");
                } else {
                    message.append(ChatColor.RED).append(holder.getDescription());
                }

                if (holder.isOptional()) {
                    message.append(ChatColor.AQUA + " (").append(Lang.OPTIONAL_MARKER.getConfigValue()).append(")");
                }

                messages.add(message.toString());
            }
        }

        return messages;
    }

    public List<String> formatResultsToList(List<AbstractResult> abstractResults) {
        List<String> messages = new ArrayList();
        messages.add(ChatColor.GRAY + " ------------ ");

        for(int i = 0; i < abstractResults.size(); ++i) {
            AbstractResult abstractResult = abstractResults.get(i);
            if (abstractResult != null) {
                StringBuilder message = new StringBuilder("     " + ChatColor.GOLD + (i + 1) + ". ");
                message.append(ChatColor.RED + abstractResult.getDescription());
                messages.add(message.toString());
            }
        }

        return messages;
    }
}
