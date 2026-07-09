package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerChecker {
    private final Autorank plugin;

    public PlayerChecker(Autorank plugin) {
        this.plugin = plugin;
    }

    public void checkPlayer(UUID uuid) {
        if (this.plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
            this.plugin.debugMessage("Player '" + uuid.toString() + "' is exempted from automated checking, so we don't check their path progress!");
        } else {
            this.plugin.getPathManager().autoAssignPaths(uuid);

            for(Path activePath : this.plugin.getPathManager().getActivePaths(uuid)) {
                activePath.checkPathProgress(uuid);
            }
        }

    }

    public void doOfflineExemptionChecks(Player player) {
        this.doLeaderboardExemptCheck(player);
        this.doAutomaticCheckingExemptionCheck(player);
        this.doTimeAdditionExemptionCheck(player);
    }

    public void doLeaderboardExemptCheck(Player player) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> s.setLeaderboardExemption(player.getUniqueId(), player.hasPermission("autorank.leaderboard.exclude")));
    }

    public void doAutomaticCheckingExemptionCheck(Player player) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> s.setAutoCheckingExemption(player.getUniqueId(), AutorankTools.isExcludedFromRanking(player)));
    }

    public void doTimeAdditionExemptionCheck(Player player) {
        this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> s.setTimeAdditionExemption(player.getUniqueId(), player.hasPermission("autorank.timeexclude")));
    }

    public boolean isExemptedFromLeaderboard(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        return player != null ? player.hasPermission("autorank.leaderboard.exclude") : this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> s.hasLeaderboardExemption(uuid)).orElse(false);
    }

    public boolean isExemptedFromAutomaticChecking(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        return player != null ? AutorankTools.isExcludedFromRanking(player) : this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> s.hasAutoCheckingExemption(uuid)).orElse(false);
    }

    public boolean isExemptedFromTimeAddition(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        return player != null ? player.hasPermission("autorank.timeexclude") : this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> s.hasTimeAdditionExemption(uuid)).orElse(false);
    }

    public List<String> formatRequirementsToList(List<CompositeRequirement> holders, List<CompositeRequirement> metRequirements) {
        List<String> messages = new ArrayList<>();
        messages.add(Lang.REQUIREMENT_PATH.getConfigValue());

        for(int i = 0; i < holders.size(); ++i) {
            CompositeRequirement holder = holders.get(i);
            if (holder != null) {
                String var10002 = Lang.REQUIREMENT_NUMBER.getConfigValue();
                StringBuilder message = new StringBuilder(var10002 + (i + 1) + ". ");
                if (metRequirements.contains(holder)) {
                    message.append(Lang.REQUIREMENT_MEET.getConfigValue()).append(holder.getDescription()).append(Lang.REQUIREMENT_DISCRIPTION.getConfigValue()).append(" (").append(Lang.DONE_MARKER.getConfigValue()).append(")");
                } else {
                    message.append(Lang.REQUIREMENT_NOT_MET.getConfigValue()).append(holder.getDescription());
                }

                if (holder.isOptional()) {
                    message.append(Lang.OPTIONAL_LEFT_BRACKET.getConfigValue()).append(Lang.OPTIONAL_MARKER.getConfigValue()).append(Lang.OPTIONAL_RIGHT_BRACKET.getConfigValue());
                }

                messages.add(message.toString());
            }
        }

        return messages;
    }

    public List<String> formatResultsToList(List<AbstractResult> abstractResults) {
        List<String> messages = new ArrayList<>();
        messages.add(Lang.REQUIREMENT_PATH.getConfigValue());

        for(int i = 0; i < abstractResults.size(); ++i) {
            AbstractResult abstractResult = abstractResults.get(i);
            if (abstractResult != null) {
                String var10001 = Lang.REQUIREMENT_NUMBER.getConfigValue();
                messages.add(var10001 + (i + 1) + ". " + Lang.RESULTS_DESCRIPTION.getConfigValue() + abstractResult.getDescription());
            }
        }

        return messages;
    }
}
