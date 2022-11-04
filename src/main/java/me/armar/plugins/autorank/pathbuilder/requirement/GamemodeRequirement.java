package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class GamemodeRequirement extends AbstractRequirement {
    int gameMode = -1;

    public GamemodeRequirement() {
    }

    public String getDescription() {
        String lang = Lang.GAMEMODE_REQUIREMENT.getConfigValue(this.gameMode + "");
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        return player.getGameMode().getValue() + "/" + this.gameMode;
    }

    protected boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else {
            return player.getGameMode().getValue() == this.gameMode;
        }
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.gameMode = (int) AutorankTools.stringToDouble(options[0]);
        }

        if (this.gameMode < 0) {
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
