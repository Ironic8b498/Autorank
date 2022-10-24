package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

public class WorldRequirement extends AbstractRequirement {
    String worldName = null;

    public WorldRequirement() {
    }

    public String getDescription() {
        return Lang.WORLD_REQUIREMENT.getConfigValue(this.worldName);
    }

    public String getProgressString(Player player) {
        return player.getWorld().getName() + "/" + this.worldName;
    }

    public boolean meetsRequirement(Player player) {
        String world = player.getWorld().getName();
        return world.equals(this.worldName);
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.worldName = options[0];
        }

        if (this.worldName == null) {
            this.registerWarningMessage("No world is specified");
            return false;
        } else {
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
