package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.TownyAdvancedHook;
import org.bukkit.entity.Player;

public class TownyNumberOfTownBlocksRequirement extends AbstractRequirement {
    int numberOfTownBlocks = 0;
    private TownyAdvancedHook hook;

    public TownyNumberOfTownBlocksRequirement() {
    }

    public String getDescription() {
        String lang = Lang.TOWNY_NEED_NUMBER_OF_TOWN_BLOCKS.getConfigValue(this.numberOfTownBlocks);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        return this.hook.getNumberOfTownBlocks(player.getName()) + "/" + this.numberOfTownBlocks;
    }

    public boolean meetsRequirement(Player player) {
        if (!this.hook.isHooked()) {
            return false;
        } else {
            return this.hook.getNumberOfTownBlocks(player.getName()) >= this.numberOfTownBlocks;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.TOWNY_ADVANCED);
        this.hook = (TownyAdvancedHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.TOWNY_ADVANCED).orElse(null);

        try {
            this.numberOfTownBlocks = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid integer was provided.");
            return false;
        }

        if (this.numberOfTownBlocks <= 0) {
            this.registerWarningMessage("Number of town blocks should be bigger than zero!");
            return false;
        } else if (this.hook != null && this.hook.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("Towny is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
