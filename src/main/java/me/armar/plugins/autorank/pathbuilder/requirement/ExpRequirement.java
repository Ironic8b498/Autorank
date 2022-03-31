package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class ExpRequirement extends AbstractRequirement {
    int minExp = -1;

    public ExpRequirement() {
    }

    public String getDescription() {
        String lang = Lang.EXP_REQUIREMENT.getConfigValue(this.minExp + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        int expLevel = player.getLevel();
        return expLevel + "/" + this.minExp;
    }

    public boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else {
            int expLevel = player.getLevel();
            return expLevel >= this.minExp;
        }
    }

    public boolean initRequirement(String[] options) {
        this.minExp = (int) AutorankTools.stringToDouble(options[0]);
        if (this.minExp < 0) {
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
