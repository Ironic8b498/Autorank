package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class ArchiveCommand extends AutorankCommand {
    private final Autorank plugin;

    public ArchiveCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.archive", sender)) {
            return true;
        } else {
            if (args.length != 2) {
                sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                return true;
            } else {
                int rate = AutorankTools.stringToTime(args[1], TimeUnit.MINUTES);
                if (rate <= 0) {
                    sender.sendMessage(ChatColor.RED + Lang.INVALID_FORMAT.getConfigValue(new Object[]{"/ar archive 10d/10h/10m"}));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "This command has been deprecated and can therefore not be used anymore.");
                    return true;
                }
            }
        }
    }

    public String getDescription() {
        return "Archive storage with a minimum";
    }

    public String getPermission() {
        return "autorank.archive";
    }

    public String getUsage() {
        return "/ar archive <minimum>";
    }
}
