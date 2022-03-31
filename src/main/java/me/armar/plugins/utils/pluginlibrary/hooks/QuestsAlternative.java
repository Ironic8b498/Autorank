package me.armar.plugins.utils.pluginlibrary.hooks;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class QuestsAlternative extends LibraryHook {
    private Quests quests;
    BukkitQuestsPlugin questsPlugin = (BukkitQuestsPlugin) Bukkit.getPluginManager().getPlugin("Quests");

    public QuestsAlternative() {
    }

    public boolean isHooked() {
        return this.quests != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.QUESTS_ALTERNATIVE)) {
            return false;
        } else {
            Plugin plugin = this.getServer().getPluginManager().getPlugin(Library.QUESTS_ALTERNATIVE.getInternalPluginName());
            if (!(plugin instanceof Quests)) {
                return false;
            } else {
                this.quests = (Quests)plugin;
                return this.quests != null;
            }
        }
    }


    public int getNumberOfCompletedQuests(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            QPlayer playerData = questsPlugin.getPlayerManager().getPlayer(uuid);
            final List<Quest> listCompleted = playerData.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED);
            return listCompleted.size();
        }
    }

    public int getNumberOfActiveQuests(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            QPlayer playerData = questsPlugin.getPlayerManager().getPlayer(uuid);
            return playerData.getQuestProgressFile().getStartedQuests().size();
        }
    }

    public boolean isQuestCompleted(UUID uuid, String questName) {
        if (!this.isHooked()) {
            return false;
        } else {
            Quest questData = questsPlugin.getQuestManager().getQuestById(questName);
            if (questData == null){
                return false;
            }
            QPlayer playerData = questsPlugin.getPlayerManager().getPlayer(uuid);
            QuestProgress progress = playerData.getQuestProgressFile().getQuestProgress(questData);
            return progress.isCompleted();
        }
        }

}
