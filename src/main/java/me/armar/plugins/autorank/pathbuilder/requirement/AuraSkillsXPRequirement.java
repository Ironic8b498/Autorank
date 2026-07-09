package me.armar.plugins.autorank.pathbuilder.requirement;

import dev.aurelium.auraskills.api.skill.Skills;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.AuraSkillsHook;

import java.util.Locale;
import java.util.UUID;

public class AuraSkillsXPRequirement extends AbstractRequirement {
    private AuraSkillsHook handler = null;
    private double requiredXP = -1.0F;
    private String skill = "ALL";

    public AuraSkillsXPRequirement() {
    }

    public String getDescription() {
        return Lang.AURA_SKILLS_XP_REQUIREMENT.getConfigValue(this.requiredXP, this.skill);
    }

    public String getProgressString(UUID uuid) {
        double var10000 = this.handler.getXP(uuid, this.skill);
        return var10000 + "/" + this.requiredXP;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getXP(uuid, this.skill) >= this.requiredXP;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.AURA_SKILLS);
        this.handler = (AuraSkillsHook)this.getDependencyManager().getLibraryHook(Library.AURA_SKILLS).orElse(null);
        if (options.length > 0) {
            try {
                this.requiredXP = Double.parseDouble(options[1]);
            } catch (NumberFormatException var4) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

            try {
                this.skill = Skills.valueOf(options[0].trim().toUpperCase(Locale.ROOT)).getDisplayName(Locale.ENGLISH);
            } catch (Exception var3) {
                this.registerWarningMessage("The XP skill '" + options[0].trim() + "' does not exist!");
                return false;
            }
        }

        if (this.requiredXP < (double)0.0F) {
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
