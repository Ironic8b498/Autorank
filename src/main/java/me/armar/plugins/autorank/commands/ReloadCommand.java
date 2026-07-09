package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends AutorankCommand {
    private final Autorank plugin;

    public ReloadCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else {
            if (this.hasPermission(this.getPermission(), sender)) {
                this.plugin.getWarningManager().clearWarnings();
                this.plugin.getPathsConfig().reloadConfig();
                this.plugin.getSettingsConfig().reloadConfig();
                this.plugin.getPathManager().initialiseFromConfigs();
                this.plugin.getLanguageHandler().reloadLangConfig();
                AutorankTools.sendDeserialize(sender, Lang.AUTORANK_RELOADED.getConfigValue());
            }

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
