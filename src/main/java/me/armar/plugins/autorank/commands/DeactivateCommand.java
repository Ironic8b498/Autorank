//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.armar.plugins.autorank.commands;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeactivateCommand extends AutorankCommand {
    private final Autorank plugin;

    public DeactivateCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (!this.hasPermission(this.getPermission(), sender)) {
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
                } else if (!targetPath.isActive(player.getUniqueId())) {
                    AutorankTools.sendDeserialize(sender, Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
                    return true;
                } else {
                    this.plugin.getPathManager().deassignPath(targetPath, player.getUniqueId());
                    if (!targetPath.shouldStoreProgressOnDeactivation()) {
                        String var10001 = Lang.PATH_IS_DEACTIVATED.getConfigValue(targetPath.getDisplayName());
                        AutorankTools.sendDeserialize(sender, var10001 + Lang.AND_YOUR_PROGRESS.getConfigValue());
                    } else {
                        String var8 = Lang.PATH_IS_DEACTIVATED.getConfigValue(targetPath.getDisplayName());
                        AutorankTools.sendDeserialize(sender, var8 + Lang.BUT_YOUR_PROGRESS.getConfigValue());
                    }

                    return true;
                }
            }
        } else {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT_DEACTIVATE.getConfigValue());
            return true;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (sender instanceof Player player) {
            String typedPath = AutorankCommand.getStringFromArgs(args, 1);
            return AutorankCommand.getOptionsStartingWith(this.plugin.getPathManager().getActivePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors.toList()), typedPath);
        } else {
            return this.plugin.getPathManager().getAllPaths().stream().map(Path::getDisplayName).collect(Collectors.toList());
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
