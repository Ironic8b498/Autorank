package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
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
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission("autorank.archive", sender)) {
            return true;
        } else {
            if (args.length != 2) {
                Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                plugin.adventure().player((Player) sender).sendMessage(invalid_format);
                return true;
            } else {
                int rate = AutorankTools.stringToTime(args[1], TimeUnit.MINUTES);
                if (rate <= 0) {
                    Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(new Object[]{"/ar archive 10d/10h/10m"}));
                    plugin.adventure().player((Player) sender).sendMessage(invalid_format);
                    return true;
                } else {
                    Component invalid_format = mm.deserialize(Lang.THIS_COMMAND.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(invalid_format);
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
