package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class QuestsHook extends LibraryHook {
    private Quests quests;

    public QuestsHook() {
    }

    public boolean isHooked() {
        return this.quests != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.QUESTS)) {
            return false;
        } else {
            Plugin plugin = this.getServer().getPluginManager().getPlugin(Library.QUESTS.getInternalPluginName());
            if (!(plugin instanceof Quests)) {
                return false;
            } else {
                this.quests = (Quests)plugin;
                return this.quests != null;
            }
        }
    }

    private Quester getQuester(UUID uuid) {
        return this.quests.getQuester(uuid);
    }

    public int getNumberOfCompletedQuests(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            Quester quester = this.getQuester(uuid);
            return quester == null ? -1 : quester.getCompletedQuests().size();
        }
    }

    public int getNumberOfActiveQuests(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            Quester quester = this.getQuester(uuid);
            return quester == null ? -1 : quester.getCurrentQuests().size();
        }
    }

    public int getQuestsPoints(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            Quester quester = this.getQuester(uuid);
            return quester == null ? -1 : quester.getQuestPoints();
        }
    }

    public boolean isQuestCompleted(UUID uuid, String questName) {
        if (!this.isHooked()) {
            return false;
        } else {
            Quester quester = this.getQuester(uuid);
            return quester != null && quester.getCompletedQuests().contains(questName);
        }
    }
}
