package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends AutorankCommand {
    private final Autorank plugin;

    public HelpCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            if (args.length == 1) {
                this.showHelpPages(sender, 1);
            } else {
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
    }

    private void showHelpPages(CommandSender sender, int page) {
        List<AutorankCommand> commands = (List)(new ArrayList(this.plugin.getCommandsManager().getRegisteredCommands().values())).stream().sorted(Comparator.comparing(AutorankCommand::getUsage)).collect(Collectors.toList());
        if (this.plugin.getSettingsConfig().doBaseHelpPageOnPermissions() && !sender.isOp()) {
            commands = commands.stream().filter((cmd) -> sender.hasPermission(cmd.getPermission())).toList();
        }

        int listSize = commands.size();
        int maxPages = (int)Math.ceil((double)listSize / (double)6.0F);
        if (page > maxPages || page == 0) {
            page = maxPages;
        }

        int start = 0;
        int end = 6;
        if (page != 1) {
            int i = page - 1;
            ++start;
            start += 6 * i;
            end = start + 6;
        }

        sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");

        for(int i = start; i < end && i < listSize; ++i) {
            AutorankCommand command = commands.get(i);
            ChatColor var10001 = ChatColor.AQUA;
            sender.sendMessage(var10001 + command.getUsage() + ChatColor.GRAY + " - " + command.getDescription());
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
