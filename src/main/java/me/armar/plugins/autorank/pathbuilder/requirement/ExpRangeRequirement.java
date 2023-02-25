package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class ExpRangeRequirement extends AbstractRequirement {
    int minExp = -1;
    int maxExp = -1;

    public ExpRangeRequirement() {
    }

    public String getDescription() {
        String lang = Lang.EXP_RANGE_REQUIREMENT.getConfigValue(this.minExp + "", this.maxExp + " ");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        int expLevel = player.getLevel();
        return expLevel + "/" + this.minExp + " to " + this.maxExp;
    }

    public boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else {
            int expLevel = player.getLevel();
            return expLevel >= this.minExp && expLevel <= this.maxExp;
            }
        }

    public boolean initRequirement(String[] options) {
        this.minExp = (int) AutorankTools.stringToDouble(options[0]);
        this.maxExp = (int) AutorankTools.stringToDouble(options[1]);
        if (this.minExp < 0 || this.maxExp < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
