package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DeactivateCommand extends AutorankCommand {
    private final Autorank plugin;

    public DeactivateCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't deactivate paths, silly.."));
            return true;
        } else {
            Player player = (Player)sender;
            String pathName = AutorankCommand.getStringFromArgs(args, 1);
            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                return true;
            } else if (!targetPath.isActive(player.getUniqueId())) {
                sender.sendMessage(Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
                return true;
            } else {
                this.plugin.getPathManager().deassignPath(targetPath, player.getUniqueId());
                if (!targetPath.shouldStoreProgressOnDeactivation()) {
                    sender.sendMessage(ChatColor.GREEN + Lang.PATH_IS_DEACTIVATED.getConfigValue(targetPath.getDisplayName()) + ChatColor.RED + Lang.AND_YOUR_PROGRESS.getConfigValue());
                } else {
                    sender.sendMessage(ChatColor.GREEN + Lang.PATH_IS_DEACTIVATED.getConfigValue(targetPath.getDisplayName()) + ChatColor.GOLD + Lang.BUT_YOUR_PROGRESS.getConfigValue());
                }

                return true;
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return this.plugin.getPathManager().getAllPaths().stream().map(Path::getDisplayName).collect(Collectors.toList());
        } else {
            Player player = (Player)sender;
            String typedPath = AutorankCommand.getStringFromArgs(args, 1);
            return AutorankCommand.getOptionsStartingWith(this.plugin.getPathManager().getActivePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.toList()), typedPath);
        }
    }

    public String getDescription() {
        return "Deactivate a path";
    }

    public String getPermission() {
        return "autorank.deactivate";
    }

    public String getUsage() {
        return "/ar deactivate <path>";
    }
}
