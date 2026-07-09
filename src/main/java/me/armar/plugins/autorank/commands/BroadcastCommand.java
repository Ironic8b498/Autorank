package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand extends AutorankCommand {
    public BroadcastCommand() {
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length < 1) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            AutorankTools.playersDeserialize(AutorankTools.getFinalArg(args, 1));
            return true;
        }
    }

    public String getDescription() {
        return "Broadcast ['messsage']";
    }

    public String getPermission() {
        return "autorank.admin";
    }

    public String getUsage() {
        return "/ar broadcast ['message']";
    }
}
