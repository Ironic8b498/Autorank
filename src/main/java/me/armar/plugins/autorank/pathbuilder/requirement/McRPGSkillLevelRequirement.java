package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.McRPGHook;

import java.util.UUID;

public class McRPGSkillLevelRequirement extends AbstractRequirement {
    private McRPGHook hook = null;
    private int skillLevel = -1;
    private String skillName = "all";

    public McRPGSkillLevelRequirement() {
    }

    public String getDescription() {
        return !this.skillName.equals("all") && !this.skillName.equals("none") ? Lang.MCRPG_SKILL_LEVEL_REQUIREMENT.getConfigValue(this.skillLevel, this.skillName) : Lang.MCRPG_SKILL_LEVEL_REQUIREMENT.getConfigValue(this.skillLevel, "all skills");
    }

    public String getProgressString(UUID uuid) {
        int level;
        if (this.skillName.equalsIgnoreCase("all")) {
            level = this.hook.getPowerLevel(uuid);
        } else {
            level = this.hook.getSkillLevel(uuid, this.skillName);
        }

        return level + "/" + this.skillLevel;
    }

    public boolean meetsRequirement(UUID uuid) {
        if (this.skillName.equalsIgnoreCase("all")) {
            return this.hook.getPowerLevel(uuid) >= this.skillLevel;
        } else {
            return this.hook.getSkillLevel(uuid, this.skillName) >= this.skillLevel;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.MCRPG);
        this.hook = (McRPGHook)this.getDependencyManager().getLibraryHook(Library.MCRPG).orElse(null);
        if (options.length > 0) {
            this.skillLevel = Integer.parseInt(options[0]);
        }

        if (options.length > 1) {
            this.skillName = options[1];
        }

        if (this.skillLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.hook != null && this.hook.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("McRPG is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return this.skillName.equalsIgnoreCase("all") ? (double) this.hook.getPowerLevel(uuid) / (double)this.skillLevel : (double) this.hook.getSkillLevel(uuid, this.skillName) / (double)this.skillLevel;
    }
}
