package me.armar.plugins.autorank.hooks.quests;

import java.util.UUID;

public interface QuestsPlugin {
    int getNumberOfActiveQuests(UUID var1);

    int getNumberOfCompletedQuests(UUID var1);

    boolean hasCompletedQuest(UUID var1, String var2);
}
