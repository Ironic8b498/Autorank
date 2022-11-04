package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        var mm = MiniMessage.miniMessage();
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length < 2) {
            Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            plugin.adventure().player((Player) sender).sendMessage(invalid_format);
            return true;
        } else if (!(sender instanceof Player)) {
            Component you_are_a_robot = mm.deserialize(Lang.YOU_ARE_A_ROBOT_DEACTIVATE.getConfigValue());
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
            } else if (!targetPath.isActive(player.getUniqueId())) {
                Component path_is_not_active = mm.deserialize(Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
                plugin.adventure().player((Player) sender).sendMessage(path_is_not_active);
                return true;
            } else {
                this.plugin.getPathManager().deassignPath(targetPath, player.getUniqueId());
                if (!targetPath.shouldStoreProgressOnDeactivation()) {
                    Component and_your_progress = mm.deserialize(Lang.PATH_IS_DEACTIVATED.getConfigValue(targetPath.getDisplayName()) + Lang.AND_YOUR_PROGRESS.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(and_your_progress);
                } else {
                    Component but_your_progress = mm.deserialize(Lang.PATH_IS_DEACTIVATED.getConfigValue(targetPath.getDisplayName()) + Lang.BUT_YOUR_PROGRESS.getConfigValue());
                    plugin.adventure().player((Player) sender).sendMessage(but_your_progress);
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
