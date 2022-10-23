package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.UUID;

public class TotalVotesRequirement extends AbstractRequirement {
    int totalVotes = -1;

    public TotalVotesRequirement() {
    }

    public String getDescription() {
        String lang = Lang.VOTE_REQUIREMENT.getConfigValue(this.totalVotes + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        int votes = this.getStatisticsManager().getTimesVoted(uuid);
        return votes + "/" + this.totalVotes;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getTimesVoted(uuid) >= this.totalVotes;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.STATZ);

        try {
            this.totalVotes = Integer.parseInt(options[0]);
        } catch (Exception var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.totalVotes < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int votes = this.getStatisticsManager().getTimesVoted(uuid);
        return (double)votes * 1.0D / (double)this.totalVotes;
    }
}
