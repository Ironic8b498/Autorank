package me.armar.plugins.utils.pluginlibrary.hooks;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.UUID;

public class AdvancedAchievementsHook extends LibraryHook {
    private AdvancedAchievementsAPI advancedAchievements;

    public AdvancedAchievementsHook() {
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.ADVANCEDACHIEVEMENTS);
    }

    public boolean hook() {
        return isPluginAvailable(Library.ADVANCEDACHIEVEMENTS);
    }

    public boolean hasAchievement(UUID uuid, String achievementName) {
        return this.isHooked() && this.advancedAchievements.hasPlayerReceivedAchievement(uuid, achievementName);
    }

    public int getNumberOfAchievements(UUID uuid) {
        return !this.isHooked() ? -1 : this.advancedAchievements.getPlayerTotalAchievements(uuid);
    }
}
