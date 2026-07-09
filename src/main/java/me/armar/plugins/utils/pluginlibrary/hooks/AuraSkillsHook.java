package me.armar.plugins.utils.pluginlibrary.hooks;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.user.SkillsUser;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.Locale;
import java.util.UUID;

public class AuraSkillsHook extends LibraryHook {

    public boolean isHooked() {
        return isPluginAvailable(Library.AURA_SKILLS);
    }

    public boolean hook() { return !isPluginAvailable(Library.AURA_SKILLS);
    }

    public double getStatLevel(UUID uuid, String statType) {
        if (!this.isHooked()) {
            return 0.0;
        } else {
            try {
                AuraSkillsApi auraSkills = AuraSkillsApi.get();
                SkillsUser user = auraSkills.getUser(uuid);
                return user.getStatLevel(Stats.valueOf(statType.toUpperCase(Locale.ROOT)));
            } catch (Exception var3) {
                return 0.0;
            }
        }
    }

    public int getSkillLevel(UUID uuid, String skillName) {
        if (!this.isHooked()) {
            return 0;
        } else {
            try {
                AuraSkillsApi auraSkills = AuraSkillsApi.get();
                SkillsUser user = auraSkills.getUser(uuid);
                return user.getSkillLevel(Skills.valueOf(skillName.toUpperCase(Locale.ROOT)));
            } catch (Exception var3) {
                return 0;
            }
        }
    }


    public double getXP(UUID uuid, String skillName) {
        if (!this.isHooked()) {
            return 0.0;
        } else {
            try {
                AuraSkillsApi auraSkills = AuraSkillsApi.get();
                SkillsUser user = auraSkills.getUser(uuid);
                return user.getSkillXp(Skills.valueOf(skillName.toUpperCase(Locale.ROOT)));
            } catch (Exception var3) {
                return 0.0;
            }
        }
    }

    public double getMana(UUID uuid) {
        if (!this.isHooked()) {
            return 0.0;
        } else {
            try {
                AuraSkillsApi auraSkills = AuraSkillsApi.get();
                SkillsUser user = auraSkills.getUser(uuid);
                return user.getMana();
            } catch (Exception var3) {
                return 0.0;
            }
        }
    }
}
