package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ArchiveCommand extends AutorankCommand {
    private final Autorank plugin;

    public ArchiveCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission("autorank.archive", sender)) {
            return true;
        } else {
            if (args.length != 2) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                return true;
            } else {
                int rate = AutorankTools.stringToTime(args[1], TimeUnit.MINUTES);
                if (rate <= 0) {
                    AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue("/ar archive 10d/10h/10m"));
                    return true;
                } else {
                    AutorankTools.sendDeserialize(sender, Lang.THIS_COMMAND.getConfigValue());
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
