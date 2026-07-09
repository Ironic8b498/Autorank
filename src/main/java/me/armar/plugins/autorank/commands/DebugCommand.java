package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DebugCommand extends AutorankCommand {
    private final Autorank plugin;

    public DebugCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else {
            if (this.hasPermission(this.getPermission(), sender)) {
                Debugger.debuggerEnabled = !Debugger.debuggerEnabled;
                if (Debugger.debuggerEnabled) {
                    String var10001 = Lang.DEBUG_MODE.getConfigValue();
                    AutorankTools.sendDeserialize(sender, var10001 + Lang.ENABLED.getConfigValue());
                } else {
                    String var5 = Lang.DEBUG_MODE.getConfigValue();
                    AutorankTools.sendDeserialize(sender, var5 + Lang.DISABLED.getConfigValue());
                }

                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    String fileName = this.plugin.getDebugger().createDebugFile();
                    AutorankTools.sendDeserialize(sender, Lang.DEBUG_FILE.getConfigValue(fileName));
                });
            }

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
