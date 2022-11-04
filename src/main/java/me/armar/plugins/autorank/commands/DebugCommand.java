package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DebugCommand extends AutorankCommand {
    private final Autorank plugin;

    public DebugCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission("autorank.debug", sender)) {
            return true;
        } else {
            Debugger.debuggerEnabled = !Debugger.debuggerEnabled;
            if (Debugger.debuggerEnabled) {
                AutorankTools.sendDeserialize(sender, Lang.DEBUG_MODE.getConfigValue() + Lang.ENABLED.getConfigValue());
            } else {
                AutorankTools.sendDeserialize(sender, Lang.DEBUG_MODE.getConfigValue() + Lang.DISABLED.getConfigValue());
            }

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                String fileName = this.plugin.getDebugger().createDebugFile();
                AutorankTools.sendDeserialize(sender, Lang.DEBUG_FILE.getConfigValue(fileName));
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
