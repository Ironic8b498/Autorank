package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.AutorankHook;
import me.armar.plugins.utils.pluginlibrary.hooks.LibraryHook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HooksCommand extends AutorankCommand {
    private final Autorank plugin;

    public HooksCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (!this.plugin.getDependencyManager().isPluginLibraryLoaded()) {
            AutorankTools.sendDeserialize(sender, Lang.DEPRECATED_COMMAND.getConfigValue());
            return true;
        } else {
            AutorankTools.sendDeserialize(sender, Lang.HOOKS.getConfigValue());
            Library[] var5 = Library.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Library dep = var5[var7];
                LibraryHook handler = this.plugin.getDependencyManager().getLibraryHook(dep).orElse(null);
                if (handler != null && handler.isHooked() && !(handler instanceof AutorankHook)) {
                    AutorankTools.sendDeserialize(sender, Lang.HOOKED_PLUGINS.getConfigValue(dep.getHumanPluginName()));
                }
            }

            return true;
        }
    }

    public String getDescription() {
        return "Shows a list of plugins Autorank is hooked into.";
    }

    public String getPermission() {
        return "autorank.hooks";
    }

    public String getUsage() {
        return "/ar hooks";
    }
}
