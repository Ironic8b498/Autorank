package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DebugCommand extends AutorankCommand {
    private final Autorank plugin;

    public DebugCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.debug", sender)) {
            return true;
        } else {
            Debugger.debuggerEnabled = !Debugger.debuggerEnabled;
            if (Debugger.debuggerEnabled) {
                sender.sendMessage(ChatColor.GOLD + Lang.DEBUG_MODE.getConfigValue() + ChatColor.GREEN + Lang.ENABLED.getConfigValue());
            } else {
                sender.sendMessage(ChatColor.GOLD + Lang.DEBUG_MODE.getConfigValue() + ChatColor.RED + Lang.DISABLED.getConfigValue());
            }

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                String fileName = this.plugin.getDebugger().createDebugFile();
                sender.sendMessage(ChatColor.GREEN + Lang.DEBUG_FILE.getConfigValue(fileName));
            });
            return true;
        }
    }

    public String getDescription() {
        return "Shows debug information.";
    }

    public String getPermission() {
        return "autorank.debug";
    }

    public String getUsage() {
        return "/ar debug";
    }
}
