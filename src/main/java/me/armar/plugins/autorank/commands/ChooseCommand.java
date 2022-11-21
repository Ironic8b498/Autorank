package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.util.AutorankTools;
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
        if (!(sender instanceof Player player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT_CHOOSE.getConfigValue());
            return true;
        } else if (!this.hasPermission("autorank.choose", sender)) {
            return true;
        } else if (args.length < 2) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            String pathName = AutorankCommand.getStringFromArgs(args, 1);
            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                AutorankTools.sendDeserialize(sender, Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                return true;
            } else if (targetPath.isActive(player.getUniqueId())) {
                AutorankTools.sendDeserialize(sender, Lang.ALREADY_ON_THIS_PATH.getConfigValue());
                return true;
            } else if (targetPath.hasCompletedPath(player.getUniqueId()) && !targetPath.isRepeatable()) {
                AutorankTools.sendDeserialize(sender, Lang.PATH_NOT_ALLOWED_TO_RETAKE.getConfigValue());
                return true;
            } else if (!targetPath.meetsPrerequisites(player.getUniqueId())) {
                AutorankTools.sendDeserialize(sender, Lang.YOU_DO_NOT_MEET.getConfigValue() + "<NEWLINE>" + Lang.TYPE.getConfigValue(targetPath.getDisplayName()));
                return true;
            } else if (targetPath.isOnCooldown(player.getUniqueId())) {
                AutorankTools.sendDeserialize(sender, Lang.YOU_ARE_ON_COOLDOWN.getConfigValue() + "NEWLINE" + Lang.YOU_NEED_TO.getConfigValue() + AutorankTools.timeToString((int)targetPath.getTimeLeftForCooldown(player.getUniqueId()), TimeUnit.MINUTES));
                return true;
            } else {
                this.plugin.getPathManager().assignPath(targetPath, player.getUniqueId(), false);
                AutorankTools.sendDeserialize(sender, Lang.CHOSEN_PATH.getConfigValue(targetPath.getDisplayName()));
                if (!targetPath.shouldStoreProgressOnDeactivation()) {
                    AutorankTools.sendDeserialize(sender, Lang.PROGRESS_RESET.getConfigValue());
                } else {
                    AutorankTools.sendDeserialize(sender, Lang.PROGRESS_RESTORED.getConfigValue());
                }

                return true;
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            return this.plugin.getPathManager().getAllPaths().stream().map(Path::getDisplayName).collect(Collectors.toList());
        } else {
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
