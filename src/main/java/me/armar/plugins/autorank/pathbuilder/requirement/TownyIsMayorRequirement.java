package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.TownyAdvancedHook;
import org.bukkit.entity.Player;

public class TownyIsMayorRequirement extends AbstractRequirement {
    boolean shouldBeMayor = true;
    private TownyAdvancedHook hook;

    public TownyIsMayorRequirement() {
    }

    public String getDescription() {
        String lang = Lang.TOWNY_IS_MAYOR_REQUIREMENT.getConfigValue();
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        return this.hook.isMayor(player.getName()) + "/" + this.shouldBeMayor;
    }

    public boolean meetsRequirement(Player player) {
        if (!this.hook.isHooked()) {
            return false;
        } else {
            return this.hook.isMayor(player.getName()) == this.shouldBeMayor;
        }
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.TOWNY_ADVANCED);
        this.hook = (TownyAdvancedHook)this.getAutorank().getDependencyManager().getLibraryHook(Library.TOWNY_ADVANCED).orElse(null);

        try {
            this.shouldBeMayor = Boolean.parseBoolean(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid boolean was provided.");
            return false;
        }

        if (this.hook != null && this.hook.isHooked()) {
            return this.hook != null;
        } else {
            this.registerWarningMessage("Towny is not available");
            return false;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
