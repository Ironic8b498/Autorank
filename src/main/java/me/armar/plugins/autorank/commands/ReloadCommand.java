package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends AutorankCommand {
    private final Autorank plugin;

    public ReloadCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.reload", sender)) {
            return true;
        } else {
            this.plugin.getWarningManager().clearWarnings();
            this.plugin.getPathsConfig().reloadConfig();
            this.plugin.getSettingsConfig().reloadConfig();
            this.plugin.getPathManager().initialiseFromConfigs();
            AutorankTools.sendColoredMessage(sender, Lang.AUTORANK_RELOADED.getConfigValue());
            return true;
        }
    }

    public String getDescription() {
        return "Reload Autorank.";
    }

    public String getPermission() {
        return "autorank.reload";
    }

    public String getUsage() {
        return "/ar reload";
    }
}
