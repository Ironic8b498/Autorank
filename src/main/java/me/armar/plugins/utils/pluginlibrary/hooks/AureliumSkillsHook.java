package me.armar.plugins.utils.pluginlibrary.hooks;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.stats.Stats;
import me.armar.plugins.utils.pluginlibrary.Library;

import java.util.Locale;
import java.util.UUID;

public class AureliumSkillsHook extends LibraryHook{

    private AureliumSkillsHook aureliumSkillsHook;

    public AureliumSkillsHook(){

    }

    public boolean isHooked() {
        return isPluginAvailable(Library.AURELIUM_SKILLS);
    }

    public boolean hook() {
        return isPluginAvailable(Library.AURELIUM_SKILLS);
    }

    public double getStatLevel(UUID uuid, String statType) {
        Stats stat = null;

        try {
            stat = Stats.valueOf(statType.toUpperCase(Locale.ROOT));
        } catch (Exception var5) {
            return 0.0D;
        }

        return AureliumAPI.getStatLevel(uuid, stat);
    }

    public int getSkillLevel(UUID uuid, String skillName) {
        Skills skills = null;

        try {
            skills = Skills.valueOf(skillName.toUpperCase(Locale.ROOT));
        } catch (Exception var5) {
            return 0;
        }

        return AureliumAPI.getSkillLevel(uuid, skills);
    }

    public double getXP(UUID uuid, String skillName) {
        Skills skills = null;

        try {
            skills = Skills.valueOf(skillName.toUpperCase(Locale.ROOT));
        } catch (Exception var5) {
            return 0.0D;
        }

        return AureliumAPI.getXp(uuid, skills);
    }

    public double getMana(UUID uuid) {
        return AureliumAPI.getMana(uuid);
    }
}