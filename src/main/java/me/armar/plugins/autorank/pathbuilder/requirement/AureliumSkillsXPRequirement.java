package me.armar.plugins.autorank.pathbuilder.requirement;

import com.archyx.aureliumskills.skills.Skill;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.AureliumSkillsHook;

import java.util.Locale;
import java.util.UUID;

public class AureliumSkillsXPRequirement extends AbstractRequirement {
    private AureliumSkillsHook handler = null;
    private double requiredXP = -1.0D;
    private String skill = "AGILITY";

    public AureliumSkillsXPRequirement() {
    }

    public String getDescription() {
        return Lang.AURELIUM_SKILLS_XP_REQUIREMENT.getConfigValue(this.requiredXP, this.skill);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getXP(uuid, this.skill) + "/" + this.requiredXP;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getXP(uuid, this.skill) >= this.requiredXP;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.AURELIUM_SKILLS);
        this.handler = (AureliumSkillsHook)this.getDependencyManager().getLibraryHook(Library.AURELIUM_SKILLS).orElse(null);
        if (options.length > 0) {
            try {
                this.requiredXP = Double.parseDouble(options[1]);
            } catch (NumberFormatException var4) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

            try {
                this.skill = Skill.valueOf(options[0].trim().toUpperCase(Locale.ROOT)).getDisplayName(Locale.ENGLISH);
            } catch (Exception var3) {
                this.registerWarningMessage("The skill '" + options[0].trim() + "' does not exist!");
                return false;
            }
        }

        if (this.requiredXP < 0.0D) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return this.handler.getXP(uuid, this.skill) / this.requiredXP;
    }
}
