package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TrackCommand extends AutorankCommand {
    private final Autorank plugin;

    public TrackCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you don't make progress, silly.."));
            return true;
        } else if (!this.hasPermission("autorank.track", sender)) {
            return true;
        } else if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            Player player = (Player)sender;
            String pathName;
            if (args.length < 3) {
                if (this.plugin.getPathManager().getActivePaths(player.getUniqueId()).size() != 1) {
                    sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
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
            } catch (Exception var13) {
                player.sendMessage(ChatColor.RED + Lang.INVALID_NUMBER.getConfigValue(new Object[]{reqIdString}));
                return true;
            }

            UUID uuid = player.getUniqueId();
            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                return true;
            } else if (!targetPath.isActive(player.getUniqueId())) {
                sender.sendMessage(Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
                return true;
            } else {
                List<CompositeRequirement> requirements = targetPath.getRequirements();
                if (requirements.size() == 0) {
                    player.sendMessage(ChatColor.RED + Lang.YOU_DONT_HAVE_ANY.getConfigValue());
                    return true;
                } else {
                    player.sendMessage(ChatColor.GRAY + " ------------ ");
                    if (completionID > requirements.size()) {
                        completionID = requirements.size();
                    }

                    if (completionID < 1) {
                        completionID = 1;
                    }

                    CompositeRequirement holder = requirements.get(completionID - 1);
                    if (this.plugin.getPathManager().hasCompletedRequirement(uuid, targetPath, completionID - 1)) {
                        player.sendMessage(ChatColor.RED + Lang.ALREADY_COMPLETED_REQUIREMENT.getConfigValue(new Object[0]));
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + Lang.REQUIREMENT_PROGRESS.getConfigValue(new Object[]{completionID + ""}));
                        player.sendMessage(ChatColor.AQUA + holder.getDescription());
                        player.sendMessage(ChatColor.GREEN + "Current: " + ChatColor.GOLD + holder.getProgress(player.getUniqueId()));
                        return true;
                    }
                }
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        } else {
            Set<String> suggestedIds = new HashSet();
            UUID uuid;
            if (args.length == 2 && args[args.length - 1].trim().equals("")) {
                uuid = ((Player)sender).getUniqueId();
                Iterator var11 = this.plugin.getPathManager().getActivePaths(uuid).iterator();

                while(var11.hasNext()) {
                    Path activePath = (Path)var11.next();
                    Iterator var9 = activePath.getFailedRequirements(uuid, true).iterator();

                    while(var9.hasNext()) {
                        CompositeRequirement requirement = (CompositeRequirement)var9.next();
                        suggestedIds.add("" + (requirement.getRequirementId() + 1));
                    }
                }

                return new ArrayList(suggestedIds);
            } else if (args.length >= 3) {
                uuid = ((Player)sender).getUniqueId();
                Collection<String> suggestedPaths = this.plugin.getPathManager().getActivePaths(uuid).stream().map(Path::getDisplayName).collect(Collectors.toList());
                String typedPath = AutorankCommand.getStringFromArgs(args, 2);
                return AutorankCommand.getOptionsStartingWith(suggestedPaths, typedPath);
            } else {
                return null;
            }
        }
    }

    public String getDescription() {
        return "Track the progress of a requirement.";
    }

    public String getPermission() {
        return "autorank.track";
    }

    public String getUsage() {
        return "/ar track <req id> <path>";
    }
}
