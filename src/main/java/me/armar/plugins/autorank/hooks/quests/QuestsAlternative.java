package me.armar.plugins.autorank.hooks.quests;

import java.util.UUID;

public class QuestsAlternative implements QuestsPlugin {
    private final me.armar.plugins.utils.pluginlibrary.hooks.QuestsAlternative questsHook;

    public QuestsAlternative(me.armar.plugins.utils.pluginlibrary.hooks.QuestsAlternative hook) {
        this.questsHook = hook;
    }

    public int getNumberOfActiveQuests(UUID uuid) {
        return this.questsHook.getNumberOfActiveQuests(uuid);
    }

    public int getNumberOfCompletedQuests(UUID uuid) {
        return this.questsHook.getNumberOfCompletedQuests(uuid);
    }

    public boolean hasCompletedQuest(UUID uuid, String questName) {
        return this.questsHook.isQuestCompleted(uuid, questName);
    }
}
