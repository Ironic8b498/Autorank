package me.armar.plugins.autorank.pathbuilder.requirement;

import dev.aurelium.auraskills.api.stat.Stats;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.AuraSkillsHook;

import java.util.Locale;
import java.util.UUID;

public class AuraSkillsStatRequirement extends AbstractRequirement {
    private AuraSkillsHook handler = null;
    private double requiredLevel = -1.0F;
    private String stat = "HEALTH";

    public AuraSkillsStatRequirement() {
    }

    public String getDescription() {
        return Lang.AURA_SKILLS_STAT_REQUIREMENT.getConfigValue(this.requiredLevel, this.stat);
    }

    public String getProgressString(UUID uuid) {
        double var10000 = this.handler.getStatLevel(uuid, this.stat);
        return var10000 + "/" + this.requiredLevel;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getStatLevel(uuid, this.stat) >= this.requiredLevel;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.AURA_SKILLS);
        this.handler = (AuraSkillsHook)this.getDependencyManager().getLibraryHook(Library.AURA_SKILLS).orElse(null);
        if (options.length > 0) {
            try {
                this.requiredLevel = Double.parseDouble(options[1]);
            } catch (NumberFormatException var4) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

            try {
                this.stat = Stats.valueOf(options[0].trim().toUpperCase(Locale.ROOT)).getDisplayName(Locale.ENGLISH);
            } catch (Exception var3) {
                this.registerWarningMessage("The stat level '" + options[0].trim() + "' does not exist!");
                return false;
            }
        }

        if (this.requiredLevel < (double)0.0F) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return this.handler.getStatLevel(uuid, this.stat) / this.requiredLevel;
    }
}
