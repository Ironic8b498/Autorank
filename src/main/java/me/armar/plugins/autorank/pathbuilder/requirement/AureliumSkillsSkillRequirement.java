package me.armar.plugins.autorank.pathbuilder.requirement;

import com.archyx.aureliumskills.skills.Skills;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.AureliumSkillsHook;

import java.util.Locale;
import java.util.UUID;

public class AureliumSkillsSkillRequirement extends AbstractRequirement {
    private AureliumSkillsHook handler = null;
    private double requiredLevel = -1.0D;
    private String skill = "AGILITY";

    public AureliumSkillsSkillRequirement() {
    }

    public String getDescription() {
        return Lang.AURELIUM_SKILLS_SKILL_REQUIREMENT.getConfigValue(this.requiredLevel, this.skill);
    }

    public String getProgressString(UUID uuid) {
        int var10000 = this.handler.getSkillLevel(uuid, this.skill);
        return var10000 + "/" + this.requiredLevel;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return (double)this.handler.getSkillLevel(uuid, this.skill) >= this.requiredLevel;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.AURELIUM_SKILLS);
        this.handler = (AureliumSkillsHook)this.getDependencyManager().getLibraryHook(Library.AURELIUM_SKILLS).orElse(null);
        if (options.length > 0) {
            try {
                this.requiredLevel = Double.parseDouble(options[1]);
            } catch (NumberFormatException var4) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

            try {
                this.skill = Skills.valueOf(options[0].trim().toUpperCase(Locale.ROOT)).getDisplayName(Locale.ENGLISH);
            } catch (Exception var3) {
                this.registerWarningMessage("The skill '" + options[0].trim() + "' does not exist!");
                return false;
            }
        }

        if (this.requiredLevel < 0.0D) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double)this.handler.getSkillLevel(uuid, this.skill) / this.requiredLevel;
    }
}
