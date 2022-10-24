package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.QuestsHook;

import java.util.UUID;

public class QuestsQuestPointsRequirement extends AbstractRequirement {
    private QuestsHook handler = null;
    private int questPoints = -1;

    public QuestsQuestPointsRequirement() {
    }

    public String getDescription() {
        return Lang.QUESTS_QUEST_POINTS_REQUIREMENT.getConfigValue(this.questPoints);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getQuestsPoints(uuid) + "/" + this.questPoints;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getQuestsPoints(uuid) >= this.questPoints;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.QUESTS);
        this.handler = (QuestsHook)this.getDependencyManager().getLibraryHook(Library.QUESTS).orElse(null);
        if (options.length > 0) {
            try {
                this.questPoints = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.questPoints < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double)this.handler.getQuestsPoints(uuid) * 1.0D / (double)this.questPoints;
    }
}
