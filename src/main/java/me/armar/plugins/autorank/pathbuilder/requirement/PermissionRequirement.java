package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

public class PermissionRequirement extends AbstractRequirement {
    String permission = null;

    public PermissionRequirement() {
    }

    public String getDescription() {
        String lang = Lang.PERMISSION_REQUIREMENT.getConfigValue(this.permission);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        String progress = "unknown";
        return "unknown";
    }

    public boolean meetsRequirement(Player player) {
        return (!this.isWorldSpecific() || this.getWorld().equals(player.getWorld().getName())) && player.hasPermission(this.permission);
    }

    public boolean initRequirement(String[] options) {
        this.permission = options[0];
        if (this.permission == null) {
            this.registerWarningMessage("No permission is provided");
            return false;
        } else {
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
