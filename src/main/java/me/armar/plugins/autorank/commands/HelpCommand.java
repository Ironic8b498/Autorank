package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends AutorankCommand {
    private final Autorank plugin;

    public HelpCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            this.showHelpPages(sender, 1);
        } else {
            boolean var5 = true;

            int page;
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception var7) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_NUMBER.getConfigValue(args[1]));
                return true;
            }

            this.showHelpPages(sender, page);
        }

        return true;
    }

    private void showHelpPages(CommandSender sender, int page) {
        List<AutorankCommand> commands = (List)(new ArrayList(this.plugin.getCommandsManager().getRegisteredCommands().values())).stream().sorted(Comparator.comparing(AutorankCommand::getUsage)).collect(Collectors.toList());
        if (this.plugin.getSettingsConfig().doBaseHelpPageOnPermissions() && !sender.isOp()) {
            commands = commands.stream().filter((cmd) -> {
                return sender.hasPermission(cmd.getPermission());
            }).collect(Collectors.toList());
        }

        int listSize = commands.size();
        int maxPages = (int)Math.ceil((double)listSize / 6.0D);
        if (page > maxPages || page == 0) {
            page = maxPages;
        }

        int start = 0;
        int end = 6;
        int i;
        if (page != 1) {
            i = page - 1;
            ++start;
            start += 6 * i;
            end = start + 6;
        }

        sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");

        for(i = start; i < end && i < listSize; ++i) {
            AutorankCommand command = commands.get(i);
            sender.sendMessage(ChatColor.AQUA + command.getUsage() + ChatColor.GRAY + " - " + command.getDescription());
        }

        sender.sendMessage(ChatColor.BLUE + "Page " + page + " of " + maxPages);
    }

    public String getDescription() {
        return "Show a list of commands.";
    }

    public String getPermission() {
        return "autorank.help";
    }

    public String getUsage() {
        return "/ar help <page>";
    }
}
