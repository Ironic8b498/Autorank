package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.McMMOHook;
import org.bukkit.entity.Player;

public class McMMOSkillLevelRequirement extends AbstractRequirement {
    private McMMOHook handler = null;
    private int skillLevel = -1;
    private String skillName = "all";

    public McMMOSkillLevelRequirement() {
    }

    public String getDescription() {
        return !this.skillName.equals("all") && !this.skillName.equals("none") ? Lang.MCMMO_SKILL_LEVEL_REQUIREMENT.getConfigValue(this.skillLevel + "", this.skillName) : Lang.MCMMO_SKILL_LEVEL_REQUIREMENT.getConfigValue(this.skillLevel + "", "all skills");
    }

    public String getProgressString(Player player) {
        int level;
        if (this.skillName.equalsIgnoreCase("all")) {
            level = this.handler.getPowerLevel(player);
        } else {
            level = this.handler.getLevel(player, this.skillName);
        }

        return level + "/" + this.skillLevel;
    }

    public boolean meetsRequirement(Player player) {
        if (this.skillName.equalsIgnoreCase("all")) {
            return this.handler.getPowerLevel(player) >= this.skillLevel;
        } else {
            return this.handler.getLevel(player, this.skillName) >= this.skillLevel;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.MCMMO);
        this.handler = (McMMOHook)this.getDependencyManager().getLibraryHook(Library.MCMMO).orElse(null);
        if (options.length > 0) {
            this.skillLevel = Integer.parseInt(options[0]);
        }

        if (options.length > 1) {
            this.skillName = options[1];
        }

        if (this.skillLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("mcMMO is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
