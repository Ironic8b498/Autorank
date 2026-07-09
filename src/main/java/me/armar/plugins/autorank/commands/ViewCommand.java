package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class ViewCommand extends AutorankCommand {
    private final Autorank plugin;

    public ViewCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if (!(sender instanceof Player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length < 2) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue("/ar view <path name> or /ar view list"));
            return true;
        } else {
            String pathName = AutorankCommand.getStringFromArgs(args, 1);
            if (pathName.equals("list")) {
                List<Path> paths = new ArrayList();

                for(Path path : this.plugin.getPathManager().getAllPaths()) {
                    if (!path.onlyShowIfPrerequisitesMet() || !path.meetsPrerequisites(((Player)sender).getUniqueId())) {
                        paths.add(path);
                    }
                }

                if (paths.isEmpty()) {
                    AutorankTools.sendDeserialize(sender, Lang.NO_PATHS_TO_CHOOSE.getConfigValue());
                    return true;
                } else {
                    Component the_following_paths = mm.deserialize(Lang.THE_FOLLOWING_PATHS.getConfigValue()).append(mm.deserialize(AutorankTools.createStringFromList(paths)));
                    this.plugin.adventure().player((Player)sender).sendMessage(the_following_paths);
                    return true;
                }
            } else {
                Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
                if (targetPath == null) {
                    AutorankTools.sendDeserialize(sender, Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                } else {
                    List<CompositeRequirement> prerequisites = targetPath.getPrerequisites();
                    List<String> messages = this.plugin.getPlayerChecker().formatRequirementsToList(prerequisites, new ArrayList());
                    AutorankTools.sendDeserialize(sender, Lang.PREREQUISITES_OF_PATH.getConfigValue(targetPath.getDisplayName()));
                    if (messages.isEmpty()) {
                        AutorankTools.sendDeserialize(sender, Lang.NONE.getConfigValue());
                    } else {
                        for(Object message : messages) {
                            AutorankTools.sendDeserialize(sender, message.toString());
                        }
                    }

                    List<CompositeRequirement> requirements = targetPath.getRequirements();
                    messages = this.plugin.getPlayerChecker().formatRequirementsToList(requirements, new ArrayList());
                    AutorankTools.sendDeserialize(sender, Lang.REQUIREMENTS_OF_PATH.getConfigValue(targetPath.getDisplayName()));
                    if (messages.isEmpty()) {
                        AutorankTools.sendDeserialize(sender, Lang.NONE.getConfigValue());
                    } else {
                        for(String o : messages) {
                            AutorankTools.sendDeserialize(sender, o);
                        }
                    }

                    List<AbstractResult> results = targetPath.getResults();
                    messages = this.plugin.getPlayerChecker().formatResultsToList(results);
                    AutorankTools.sendDeserialize(sender, Lang.RESULTS_OF_PATH.getConfigValue(targetPath.getDisplayName()));
                    if (messages.isEmpty()) {
                        AutorankTools.sendDeserialize(sender, Lang.NONE.getConfigValue());
                    } else {
                        for(String o : messages) {
                            AutorankTools.sendDeserialize(sender, o);
                        }
                    }
                }

                return true;
            }
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        List<String> possibilities = new ArrayList();
        if (args.length <= 1) {
            for(Path path : this.plugin.getPathManager().getAllPaths()) {
                possibilities.add(path.getDisplayName());
            }

            possibilities.add("list");
        } else {
            for(Path path : this.plugin.getPathManager().getAllPaths()) {
                if (path.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    possibilities.add(path.getDisplayName());
                }

                if (args[1].trim().isEmpty()) {
                    possibilities.add("list");
                }
            }
        }

        return possibilities;
    }

    public String getDescription() {
        return "Gives a preview of a certain ranking path";
    }

    public String getPermission() {
        return "autorank.view";
    }

    public String getUsage() {
        return "/ar view <path name>";
    }
}
