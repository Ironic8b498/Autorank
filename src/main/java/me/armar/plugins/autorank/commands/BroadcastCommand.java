package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static me.armar.plugins.autorank.util.AutorankTools.getFinalArg;

public class BroadcastCommand extends AutorankCommand {
    public BroadcastCommand(Autorank plugin) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
        if (!this.hasPermission("autorank.admin", sender)) {
            return true;
        } else if (args.length < 1) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            AutorankTools.playersDeserialize(getFinalArg(args, 1));
            return true;
        }
    }

    @Override
    public String getDescription() { return "Broadcast ['messsage']";
    }

    @Override
    public String getPermission() {
        return "autorank.admin";
    }

    @Override
    public String getUsage() {
        return "/ar broadcast ['message']";
    }
}
