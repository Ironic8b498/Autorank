package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class CompleteCommand extends AutorankCommand {
    private final Autorank plugin;

    public CompleteCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if (!(sender instanceof Player player)) {
            AutorankTools.consoleDeserialize(Lang.YOU_ARE_A_ROBOT_COMPLETE.getConfigValue());
            return true;
        } else if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else if (args.length < 2) {
            AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        } else {
            String pathName;
            if (args.length < 3) {
                if (this.plugin.getPathManager().getActivePaths(player.getUniqueId()).size() != 1) {
                    AutorankTools.sendDeserialize(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
                    return true;
                }

                pathName = this.plugin.getPathManager().getActivePaths(player.getUniqueId()).get(0).getDisplayName();
            } else {
                pathName = AutorankCommand.getStringFromArgs(args, 2);
            }

            String reqIdString = args[1];

            int completionID;
            try {
                completionID = Integer.parseInt(reqIdString);
                if (completionID < 1) {
                    completionID = 1;
                }
            } catch (Exception var14) {
                AutorankTools.sendDeserialize(sender, Lang.INVALID_NUMBER.getConfigValue(reqIdString));
                return true;
            }

            Path targetPath = this.plugin.getPathManager().findPathByDisplayName(pathName, false);
            if (targetPath == null) {
                AutorankTools.sendDeserialize(sender, Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                return true;
            } else if (!targetPath.isActive(player.getUniqueId())) {
                AutorankTools.sendDeserialize(sender, Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
                return true;
            } else if (!targetPath.allowPartialCompletion()) {
                AutorankTools.sendDeserialize(sender, Lang.THIS_PATH_DOES_NOT.getConfigValue());
                return true;
            } else if (targetPath.getFailedRequirements(player.getUniqueId(), true).isEmpty()) {
                AutorankTools.sendDeserialize(sender, Lang.YOU_DONT_HAVE.getConfigValue());
                return true;
            } else {
                List<CompositeRequirement> requirements = targetPath.getRequirements();
                if (completionID > requirements.size()) {
                    completionID = requirements.size();
                }

                CompositeRequirement holder = requirements.get(completionID - 1);
                if (targetPath.hasCompletedRequirement(player.getUniqueId(), completionID - 1)) {
                    AutorankTools.sendDeserialize(sender, Lang.ALREADY_COMPLETED_REQUIREMENT.getConfigValue());
                } else if (holder.meetsRequirement(player.getUniqueId())) {
                    targetPath.completeRequirement(player.getUniqueId(), holder.getRequirementId());
                } else {
                    Component var10000 = mm.deserialize(Lang.DO_NOT_MEET_REQUIREMENTS_FOR.getConfigValue(completionID));
                    Lang var10002 = Lang.DESCRIPTION;
                    Object[] var10003 = new Object[]{holder.getDescription()};
                    var10000 = var10000.append(mm.deserialize("<NEWLINE>" + var10002.getConfigValue(var10003)));
                    var10002 = Lang.CURRENT;
                    var10003 = new Object[]{holder.getProgress(player.getUniqueId())};
                    Component do_not_meet_requirements_for = var10000.append(mm.deserialize("<NEWLINE>" + var10002.getConfigValue(var10003)));
                    this.plugin.adventure().player(player).sendMessage(do_not_meet_requirements_for);
                }

                return true;
            }
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        } else {
            Set<String> suggestedIds = new HashSet();
            if (args.length == 2 && args[args.length - 1].trim().isEmpty()) {
                for(Path activePath : this.plugin.getPathManager().getActivePaths(((Player)sender).getUniqueId())) {
                    for(CompositeRequirement requirement : activePath.getFailedRequirements(((Player)sender).getUniqueId(), true)) {
                        int var10001 = requirement.getRequirementId();
                        suggestedIds.add("" + (var10001 + 1));
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
