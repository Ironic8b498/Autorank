package me.armar.plugins.autorank.placeholders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutorankPlaceholder extends PlaceholderExpansion {
    private final Autorank plugin;

    public AutorankPlaceholder(Autorank instance) {
        this.plugin = instance;
    }

    public boolean canRegister() {
        return true;
    }

    @NotNull
    public String getIdentifier() {
        return "autorank";
    }

    @NotNull
    public String getAuthor() {
        return "Ironic_8b49";
    }

    @NotNull
    public String getVersion() {
        return "1.1.0";
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("total_time_of_player")) {
            try {
                return this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "";
            } catch (ExecutionException | InterruptedException var4) {
                return "Couldn't obtain total time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("total_hours_of_player")) {
            try {
                Integer totaltemp = Integer.valueOf(this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "");
                double totalhour = (totaltemp / 60);
                return Integer.toString((int)totalhour);
            } catch (ExecutionException | InterruptedException var5) {
                return "Couldn't obtain total time in hours of " + player.getName();
            }

        } else if (params.equalsIgnoreCase("total_just_mins_of_player")) {
            try {
                Integer totaltemp = Integer.valueOf(this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "");
                Integer totalday = (totaltemp / 1440);
                double dechour = (totaltemp -  (totalday * 1440));
                double totalhour = (dechour / 60);
                double totalmin = (dechour - ((int)totalhour * 60));
                return Integer.toString((int)totalmin);
            } catch (ExecutionException | InterruptedException var5) {
                return "Couldn't obtain total time in minutes of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("total_just_hours_of_player")) {
            try {
                Integer totaltemp = Integer.valueOf(this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "");
                Integer totalday = (totaltemp / 1440);
                double totalhour = (totaltemp -  (totalday * 1440));
                totalhour = (totalhour / 60);
                return Integer.toString((int)totalhour);
            } catch (ExecutionException | InterruptedException var5) {
                return "Couldn't obtain total time in hours of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("total_just_days_of_player")) {
            try {
                Integer totaltemp = Integer.valueOf(this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "");
                Integer totalday = (totaltemp / 1440);
                return String.valueOf(totalday);
            } catch (ExecutionException | InterruptedException var5) {
                return "Couldn't obtain total time in days of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("total_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(this.plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException var5) {
                return "Couldn't obtain total time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("daily_time_of_player")) {
            try {
                return this.plugin.getPlayTimeManager().getPlayTime(TimeType.DAILY_TIME, player.getUniqueId()).get() + "";
            } catch (ExecutionException | InterruptedException var6) {
                return "Couldn't obtain daily time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("daily_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(this.plugin.getPlayTimeManager().getPlayTime(TimeType.DAILY_TIME, player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException var7) {
                return "Couldn't obtain daily time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("weekly_time_of_player")) {
            try {
                return this.plugin.getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME, player.getUniqueId()).get() + "";
            } catch (ExecutionException | InterruptedException var8) {
                return "Couldn't obtain weekly time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("weekly_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(this.plugin.getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME, player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException var9) {
                return "Couldn't obtain weekly time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("monthly_time_of_player")) {
            try {
                return this.plugin.getPlayTimeManager().getPlayTime(TimeType.MONTHLY_TIME, player.getUniqueId()).get() + "";
            } catch (ExecutionException | InterruptedException var10) {
                return "Couldn't obtain monthly time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("monthly_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(this.plugin.getPlayTimeManager().getPlayTime(TimeType.MONTHLY_TIME, player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException var11) {
                return "Couldn't obtain monthly time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("local_time")) {
            try {
                return this.plugin.getPlayTimeManager().getLocalPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "";
            } catch (ExecutionException | InterruptedException var12) {
                return "Couldn't obtain local time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("local_time_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact((long) this.plugin.getPlayTimeManager().getLocalPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get()), TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException var13) {
                return "Couldn't obtain local time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("global_time")) {
            try {
                return this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "";
            } catch (ExecutionException | InterruptedException var14) {
                return "Couldn't obtain global time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("global_time_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact((long) this.plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get()), TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException var15) {
                return "Couldn't obtain global time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("completed_paths")) {
            return this.plugin.getAPI().getCompletedPaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.joining(","));
        } else if (params.equalsIgnoreCase("active_paths")) {
            return this.plugin.getAPI().getActivePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.joining(","));
        } else {
            return params.equalsIgnoreCase("eligible_paths") ? this.plugin.getAPI().getEligiblePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.joining(",")) : null;
        }
    }

    public boolean persist() {
        return true;
    }
}
