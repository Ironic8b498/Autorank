package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConvertCommand extends AutorankCommand {
    private final Autorank plugin;

    public ConvertCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.DEPRECATED_COMMAND.getConfigValue());
            return true;
        }
        AutorankTools.sendDeserialize(sender, Lang.DEPRECATED_COMMAND.getConfigValue());
        return true;
    }

    public String getDescription() {
        return "Convert a file to UUID format.";
    }

    public String getPermission() {
        return "autorank.convert.storage";
    }

    public String getUsage() {
        return "/ar convert <file>";
    }
}
