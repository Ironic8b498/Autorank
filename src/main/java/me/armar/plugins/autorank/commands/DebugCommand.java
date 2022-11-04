package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.language.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                Component enabled = mm.deserialize(Lang.DEBUG_MODE.getConfigValue() + Lang.ENABLED.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(enabled);
            } else {
                Component disabled = mm.deserialize(Lang.DEBUG_MODE.getConfigValue() + Lang.DISABLED.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(disabled);
            }

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                String fileName = this.plugin.getDebugger().createDebugFile();
                Component debug_file = mm.deserialize(Lang.DEBUG_FILE.getConfigValue(fileName));
                plugin.adventure().player((Player) sender).sendMessage(debug_file);
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
