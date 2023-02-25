package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewCommand extends AutorankCommand {
    private final Autorank plugin;

    public ViewCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (!(sender instanceof Player)){
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT.getConfigValue());
            return true;
        }
        if (!this.hasPermission("autorank.view", sender)) {
            return true;
        } else if (args.length < 2) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue("/ar view <path name> or /ar view list"));
            return true;
        } else {
            boolean isPlayer = sender instanceof Player;

            String pathName = AutorankCommand.getStringFromArgs(args, 1);
            if (pathName.equals("list")) {
                List<Path> paths = new ArrayList();
                Iterator var15 = this.plugin.getPathManager().getAllPaths().iterator();

                while(true) {
                    Path path;
                    do {
                        if (!var15.hasNext()) {
                            if (paths.isEmpty()) {
                                AutorankTools.sendDeserialize(sender, Lang.NO_PATHS_TO_CHOOSE.getConfigValue());
                                return true;
                            }
                            Component the_following_paths = mm.deserialize(Lang.THE_FOLLOWING_PATHS.getConfigValue())
                                    .append(mm.deserialize(AutorankTools.createStringFromList(paths)));
                            plugin.adventure().player((Player) sender).sendMessage(the_following_paths);
                            return true;
                        }

                        path = (Path)var15.next();
                    } while(isPlayer && path.onlyShowIfPrerequisitesMet() && path.meetsPrerequisites(((Player)sender).getUniqueId()));

                    paths.add(path);
                }
            } else {
                Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
                if (targetPath == null) {
                    AutorankTools.sendDeserialize(sender, Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                    return true;
                } else {
                    List<CompositeRequirement> prerequisites = targetPath.getPrerequisites();
                    List<String> messages = this.plugin.getPlayerChecker().formatRequirementsToList(prerequisites, new ArrayList());
                    AutorankTools.sendDeserialize(sender, Lang.PREREQUISITES_OF_PATH.getConfigValue(targetPath.getDisplayName()));
                    if (messages.isEmpty()) {
                        AutorankTools.sendDeserialize(sender, Lang.NONE.getConfigValue());
                    } else {
                        Iterator var10 = messages.iterator();

                        while(var10.hasNext()) {
                            String message = (String)var10.next();
                            AutorankTools.sendDeserialize(sender, message);
                        }
                    }

                    List<CompositeRequirement> requirements = targetPath.getRequirements();
                    messages = this.plugin.getPlayerChecker().formatRequirementsToList(requirements, new ArrayList());
                    AutorankTools.sendDeserialize(sender, Lang.REQUIREMENTS_OF_PATH.getConfigValue(targetPath.getDisplayName()));
                    if (messages.isEmpty()) {
                        AutorankTools.sendDeserialize(sender, Lang.NONE.getConfigValue());
                    } else {
                        Iterator var19 = messages.iterator();

                        while(var19.hasNext()) {
                            String message = (String)var19.next();
                            AutorankTools.sendDeserialize(sender, message);
                        }
                    }

                    List<AbstractResult> results = targetPath.getResults();
                    messages = this.plugin.getPlayerChecker().formatResultsToList(results);
                    AutorankTools.sendDeserialize(sender, Lang.RESULTS_OF_PATH.getConfigValue(targetPath.getDisplayName()));
                    if (messages.isEmpty()) {
                        AutorankTools.sendDeserialize(sender, Lang.NONE.getConfigValue());
                    } else {
                        Iterator var21 = messages.iterator();

                        while(var21.hasNext()) {
                            String message = (String)var21.next();
                            AutorankTools.sendDeserialize(sender, message);
                        }
                    }

                    return true;
                }
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> possibilities = new ArrayList();
        Iterator var6;
        Path path;
        if (args.length <= 1) {
            var6 = this.plugin.getPathManager().getAllPaths().iterator();

            while(var6.hasNext()) {
                path = (Path)var6.next();
                possibilities.add(path.getDisplayName());
            }

            possibilities.add("list");
        } else {
            var6 = this.plugin.getPathManager().getAllPaths().iterator();

            while(var6.hasNext()) {
                path = (Path)var6.next();
                if (path.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    possibilities.add(path.getDisplayName());
                }

                if (args[1].trim().equals("")) {
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
