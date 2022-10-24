package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class PlayerKillsRequirement extends AbstractRequirement {
    int totalPlayersKilled = -1;

    public PlayerKillsRequirement() {
    }

    public String getDescription() {
        String lang = Lang.PLAYER_KILLS_REQUIREMENT.getConfigValue(this.totalPlayersKilled + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        int killed = this.getStatisticsManager().getPlayersKilled(uuid, this.getWorld());
        return killed + "/" + this.totalPlayersKilled + " player(s)";
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getPlayersKilled(uuid, this.getWorld()) >= this.totalPlayersKilled;
    }

    public boolean initRequirement(String[] options) {
        try {
            this.totalPlayersKilled = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.totalPlayersKilled < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int killed = this.getStatisticsManager().getPlayersKilled(uuid, this.getWorld());
        return (double)killed * 1.0D / (double)this.totalPlayersKilled;
    }
}
