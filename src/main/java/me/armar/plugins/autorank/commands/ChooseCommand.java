package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.util.AutorankTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission("autorank.choose", sender)) {
            return true;
        } else if (args.length < 2) {
            Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            plugin.adventure().player((Player) sender).sendMessage(invalid_format);
            return true;
        } else if (!(sender instanceof Player)) {
            Component you_are_a_robot = mm.deserialize(Lang.YOU_ARE_A_ROBOT_CHOOSE.getConfigValue());
            plugin.adventure().player((Player) sender).sendMessage(you_are_a_robot);
            return true;
        } else {
            Player player = (Player)sender;
            String pathName = AutorankCommand.getStringFromArgs(args, 1);
            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                Component no_path_found_with_that_name = mm.deserialize(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(no_path_found_with_that_name);
                return true;
            } else if (targetPath.isActive(player.getUniqueId())) {
                Component already_on_this_path = mm.deserialize(Lang.ALREADY_ON_THIS_PATH.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(already_on_this_path);
                return true;
            } else if (targetPath.hasCompletedPath(player.getUniqueId()) && !targetPath.isRepeatable()) {
                Component path_not_allowed_to_retake = mm.deserialize(Lang.PATH_NOT_ALLOWED_TO_RETAKE.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(path_not_allowed_to_retake);
                return true;
            } else if (!targetPath.meetsPrerequisites(player.getUniqueId())) {
                Component you_do_not_meet = mm.deserialize(Lang.YOU_DO_NOT_MEET.getConfigValue());
                Component type = mm.deserialize(Lang.TYPE.getConfigValue(targetPath.getDisplayName()));
                plugin.adventure().player((Player) sender).sendMessage(you_do_not_meet);
                plugin.adventure().player((Player) sender).sendMessage(type);
                return true;
            } else if (targetPath.isOnCooldown(player.getUniqueId())) {
                Component you_are_on_cooldown = mm.deserialize(Lang.YOU_ARE_ON_COOLDOWN.getConfigValue());
                Component you_need_to = mm.deserialize(Lang.YOU_NEED_TO.getConfigValue() + AutorankTools.timeToString((int)targetPath.getTimeLeftForCooldown(player.getUniqueId()), TimeUnit.MINUTES));
                plugin.adventure().player((Player) sender).sendMessage(you_are_on_cooldown);
                plugin.adventure().player((Player) sender).sendMessage(you_need_to);
                return true;
            } else {
                this.plugin.getPathManager().assignPath(targetPath, player.getUniqueId(), false);
                Component chosen_path = mm.deserialize(Lang.CHOSEN_PATH.getConfigValue(targetPath.getDisplayName()));
                plugin.adventure().player((Player) sender).sendMessage(chosen_path);
                if (!targetPath.shouldStoreProgressOnDeactivation()) {
                    Component progress_reset = mm.deserialize(Lang.PROGRESS_RESET.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(progress_reset);
                } else {
                    Component progress_restored = mm.deserialize(Lang.PROGRESS_RESTORED.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(progress_restored);
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
