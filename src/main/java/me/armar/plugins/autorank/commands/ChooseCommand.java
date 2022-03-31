package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChooseCommand extends AutorankCommand {
    private final Autorank plugin;

    public ChooseCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission("autorank.choose", sender)) {
            return true;
        } else if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't choose ranking paths, silly.."));
            return true;
        } else {
            Player player = (Player)sender;
            String pathName = AutorankCommand.getStringFromArgs(args, 1);
            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                return true;
            } else if (targetPath.isActive(player.getUniqueId())) {
                sender.sendMessage(Lang.ALREADY_ON_THIS_PATH.getConfigValue());
                return true;
            } else if (targetPath.hasCompletedPath(player.getUniqueId()) && !targetPath.isRepeatable()) {
                sender.sendMessage(Lang.PATH_NOT_ALLOWED_TO_RETAKE.getConfigValue());
                return true;
            } else if (!targetPath.meetsPrerequisites(player.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + Lang.YOU_DO_NOT_MEET.getConfigValue());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.TYPE.getConfigValue(targetPath.getDisplayName())));
                return true;
            } else if (targetPath.isOnCooldown(player.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + Lang.YOU_ARE_ON_COOLDOWN.getConfigValue());
                sender.sendMessage(ChatColor.RED + Lang.YOU_NEED_TO.getConfigValue() + ChatColor.GOLD + AutorankTools.timeToString((int)targetPath.getTimeLeftForCooldown(player.getUniqueId()), TimeUnit.MINUTES));
                return true;
            } else {
                this.plugin.getPathManager().assignPath(targetPath, player.getUniqueId(), false);
                sender.sendMessage(Lang.CHOSEN_PATH.getConfigValue(targetPath.getDisplayName()));
                if (!targetPath.shouldStoreProgressOnDeactivation()) {
                    sender.sendMessage(Lang.PROGRESS_RESET.getConfigValue());
                } else {
                    sender.sendMessage(Lang.PROGRESS_RESTORED.getConfigValue());
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
            return AutorankCommand.getOptionsStartingWith(this.plugin.getPathManager().getEligiblePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.toList()), typedPath);
        }
    }

    public String getDescription() {
        return "Activate a path";
    }

    public String getPermission() {
        return "autorank.choose";
    }

    public String getUsage() {
        return "/ar choose <path>";
    }
}
