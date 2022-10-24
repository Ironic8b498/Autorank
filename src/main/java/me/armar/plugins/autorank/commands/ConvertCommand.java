package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConvertCommand extends AutorankCommand {
    private final Autorank plugin;

    public ConvertCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("This command is not used anymore and will be deprecated.");
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
