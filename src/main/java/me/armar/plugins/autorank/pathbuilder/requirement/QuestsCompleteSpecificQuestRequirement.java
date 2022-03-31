package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.hooks.quests.QuestsPlugin;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.UUID;

public class QuestsCompleteSpecificQuestRequirement extends AbstractRequirement {
    private QuestsPlugin handler = null;
    private String questName = null;

    public QuestsCompleteSpecificQuestRequirement() {
    }

    public String getDescription() {
        return Lang.QUESTS_COMPLETE_SPECIFIC_QUEST_REQUIREMENT.getConfigValue(this.questName);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.hasCompletedQuest(uuid, this.questName) + "";
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.handler.hasCompletedQuest(uuid, this.questName);
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.QUESTS);
        this.addDependency(Library.QUESTS_ALTERNATIVE);
        this.handler = this.getDependencyManager().getQuestsPlugin().orElse(null);
        if (options.length > 0) {
            this.questName = options[0];
            if (this.handler == null) {
                this.registerWarningMessage("There is no Quests plugin available!");
                return false;
            } else {
                return true;
            }
        } else {
            this.registerWarningMessage("No quest name was provided.");
            return false;
        }
    }
}
