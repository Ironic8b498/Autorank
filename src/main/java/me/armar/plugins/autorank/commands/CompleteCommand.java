package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CompleteCommand extends AutorankCommand {
    private final Autorank plugin;

    public CompleteCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (!(sender instanceof Player)) {
            Component you_are_a_robot = mm.deserialize(Lang.YOU_ARE_A_ROBOT_COMPLETE.getConfigValue());
            plugin.adventure().player((Player) sender).sendMessage(you_are_a_robot);
            return true;
        } else if (!this.hasPermission("autorank.complete", sender)) {
            return true;
        } else if (args.length < 2) {
            Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            plugin.adventure().player((Player) sender).sendMessage(invalid_format);
            return true;
        } else {
            Player player = (Player)sender;
            String pathName;
            if (args.length < 3) {
                if (this.plugin.getPathManager().getActivePaths(player.getUniqueId()).size() != 1) {
                    Component invalid_format = mm.deserialize(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                    plugin.adventure().player((Player) sender).sendMessage(invalid_format);
                    return true;
                }

                pathName = this.plugin.getPathManager().getActivePaths(player.getUniqueId()).get(0).getDisplayName();
            } else {
                pathName = AutorankCommand.getStringFromArgs(args, 2);
            }

            String reqIdString = args[1];
            boolean var8 = false;

            int completionID;
            try {
                completionID = Integer.parseInt(reqIdString);
                if (completionID < 1) {
                    completionID = 1;
                }
            } catch (Exception var12) {
                Component invalid_number = mm.deserialize(Lang.INVALID_NUMBER.getConfigValue(new Object[]{reqIdString}));
                plugin.adventure().player((Player) sender).sendMessage(invalid_number);
                return true;
            }

            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                Component no_path_found_with_that_name = mm.deserialize(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(no_path_found_with_that_name);
                return true;
            } else if (!targetPath.isActive(player.getUniqueId())) {
                Component path_is_not_active = mm.deserialize(Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
                plugin.adventure().player((Player) sender).sendMessage(path_is_not_active);
                return true;
            } else if (!targetPath.allowPartialCompletion()) {
                Component this_path_does_not = mm.deserialize(Lang.THIS_PATH_DOES_NOT.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(this_path_does_not);
                return true;
            } else if (targetPath.getFailedRequirements(player.getUniqueId(), true).size() == 0) {
                Component you_dont_have = mm.deserialize(Lang.YOU_DONT_HAVE.getConfigValue());
                plugin.adventure().player((Player) sender).sendMessage(you_dont_have);
                return true;
            } else {
                List<CompositeRequirement> requirements = targetPath.getRequirements();
                if (completionID > requirements.size()) {
                    completionID = requirements.size();
                }

                CompositeRequirement holder = requirements.get(completionID - 1);
                if (targetPath.hasCompletedRequirement(player.getUniqueId(), completionID - 1)) {
                    Component already_completed_requirement = mm.deserialize(Lang.ALREADY_COMPLETED_REQUIREMENT.getConfigValue(new Object[0]));
                    plugin.adventure().player((Player) sender).sendMessage(already_completed_requirement);
                    return true;
                } else {
                    if (holder.meetsRequirement(player.getUniqueId())) {
                        targetPath.completeRequirement(player.getUniqueId(), holder.getRequirementId());
                    } else {
                        Component do_not_meet_requirements_for = mm.deserialize(Lang.DO_NOT_MEET_REQUIREMENTS_FOR.getConfigValue(new Object[]{completionID + ""}));
                        plugin.adventure().player((Player) sender).sendMessage(do_not_meet_requirements_for);
                        player.sendMessage(ChatColor.AQUA + holder.getDescription());
                        Component current = mm.deserialize(Lang.CURRENT.getConfigValue() + holder.getProgress(player.getUniqueId()));
                        plugin.adventure().player((Player) sender).sendMessage(current);
                    }

                    return true;
                }
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        } else {
            Set<String> suggestedIds = new HashSet();
            if (args.length == 2 && args[args.length - 1].trim().equals("")) {
                Iterator var10 = this.plugin.getPathManager().getActivePaths(((Player)sender).getUniqueId()).iterator();

                while(var10.hasNext()) {
                    Path activePath = (Path)var10.next();
                    Iterator var12 = activePath.getFailedRequirements(((Player)sender).getUniqueId(), true).iterator();

                    while(var12.hasNext()) {
                        CompositeRequirement requirement = (CompositeRequirement)var12.next();
                        suggestedIds.add("" + (requirement.getRequirementId() + 1));
                    }
                }

                return new ArrayList(suggestedIds);
            } else if (args.length >= 3) {
                UUID uuid = ((Player)sender).getUniqueId();
                Collection<String> suggestedPaths = this.plugin.getPathManager().getActivePaths(uuid).stream().map(Path::getDisplayName).collect(Collectors.toList());
                String typedPath = AutorankCommand.getStringFromArgs(args, 2);
                return AutorankCommand.getOptionsStartingWith(suggestedPaths, typedPath);
            } else {
                return null;
            }
        }
    }

    public String getDescription() {
        return "Complete a requirement at this moment";
    }

    public String getPermission() {
        return "autorank.complete";
    }

    public String getUsage() {
        return "/ar complete <req id> <path>";
    }
}
