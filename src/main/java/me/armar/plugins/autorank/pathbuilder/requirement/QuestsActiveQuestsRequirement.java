package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.hooks.quests.QuestsPlugin;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.UUID;

public class QuestsActiveQuestsRequirement extends AbstractRequirement {
    private QuestsPlugin handler = null;
    private int activeQuests = -1;

    public QuestsActiveQuestsRequirement() {
    }

    public String getDescription() {
        return Lang.QUESTS_ACTIVE_QUESTS_REQUIREMENT.getConfigValue(this.activeQuests);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getNumberOfActiveQuests(uuid) + "/" + this.activeQuests;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.handler.getNumberOfActiveQuests(uuid) >= this.activeQuests;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.QUESTS);
        this.addDependency(Library.QUESTS_ALTERNATIVE);
        this.handler = this.getDependencyManager().getQuestsPlugin().orElse(null);
        if (options.length > 0) {
            try {
                this.activeQuests = Integer.parseInt(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.activeQuests < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double)this.handler.getNumberOfActiveQuests(uuid) * 1.0D / (double)this.activeQuests;
    }
}
