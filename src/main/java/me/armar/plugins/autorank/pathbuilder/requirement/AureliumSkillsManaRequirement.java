package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.AureliumSkillsHook;

import java.util.UUID;

public class AureliumSkillsManaRequirement extends AbstractRequirement {
    private AureliumSkillsHook handler = null;
    private double requiredMana = -1.0D;

    public AureliumSkillsManaRequirement() {
    }

    public String getDescription() {
        return Lang.AURELIUM_SKILLS_MANA_REQUIREMENT.getConfigValue(this.requiredMana);
    }

    public String getProgressString(UUID uuid) {
        return this.handler.getMana(uuid) + "/" + this.requiredMana;
    }

    protected boolean meetsRequirement(UUID uuid) {
        if (!this.handler.isHooked()) {
            return false;
        } else {
            return this.handler.getMana(uuid) >= this.requiredMana;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.AURELIUM_SKILLS);
        this.handler = (AureliumSkillsHook)this.getDependencyManager().getLibraryHook(Library.AURELIUM_SKILLS).orElse(null);
        if (options.length > 0) {
            try {
                this.requiredMana = Double.parseDouble(options[0]);
            } catch (NumberFormatException var3) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (this.requiredMana < 0.0D) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return this.handler != null;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return this.handler.getMana(uuid) / this.requiredMana;
    }
}
