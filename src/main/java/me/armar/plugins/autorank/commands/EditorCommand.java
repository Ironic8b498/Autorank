package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.editorcommand.EditorMenuPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.AssignPathByForcePrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.AssignPathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.UnAssignPathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completepath.CompletePathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement.CompleteRequirementPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement.CompleteRequirementRequestRequirementIdPrompt;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class EditorCommand extends AutorankCommand {
    private final Autorank plugin;

    public EditorCommand(Autorank instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        } else {
            AutorankConversation conversation = AutorankConversation.fromFirstPrompt(new SelectPlayerPrompt());
            conversation.afterConversationEnded((callback) -> {
                String actionType = callback.getStorageString(EditorMenuPrompt.KEY_ACTION_TYPE);
                UUID uuid = (UUID)callback.getStorageObject(SelectPlayerPrompt.KEY_UUID);
                String playerName = callback.getStorageString(SelectPlayerPrompt.KEY_PLAYERNAME);
                if (actionType != null && !callback.isEndedByKeyword()) {
                    if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_ASSIGN_PATH)) {
                        boolean assignedByForce = callback.getStorageBoolean(AssignPathByForcePrompt.KEY_ASSIGN_PATH_BY_FORCE);
                        if (!this.hasPermission(assignedByForce ? "autorank.editor.path.assign.force" : "autorank.editor.path.assign", sender)) {
                            return;
                        }

                        String pathToAssign = callback.getStorageString(AssignPathPrompt.KEY_PATH_TO_BE_ASSIGNED);
                        if (pathToAssign == null) {
                            return;
                        }

                        Path pathx = this.plugin.getPathManager().findPathByInternalName(pathToAssign, false);
                        if (pathx == null) {
                            return;
                        }

                        try {
                            this.plugin.getPathManager().assignPath(pathx, uuid, assignedByForce);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.ASSIGNED.getConfigValue(playerName, pathx.getDisplayName())));
                        } catch (IllegalArgumentException var10) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.COULD_NOT_ASSIGN.getConfigValue(playerName, pathx.getDisplayName())));
                            return;
                        }
                    } else {
                        String pathOfRequirement;
                        Path path;
                        if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_UNASSIGN_PATH)) {
                            if (!this.hasPermission("autorank.editor.path.unassign", sender)) {
                                return;
                            }

                            pathOfRequirement = callback.getStorageString(UnAssignPathPrompt.KEY_PATH_TO_BE_UNASSIGNED);
                            if (pathOfRequirement == null) {
                                return;
                            }

                            path = this.plugin.getPathManager().findPathByInternalName(pathOfRequirement, false);
                            if (path == null) {
                                return;
                            }

                            this.plugin.getPathManager().deassignPath(path, uuid);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.UNASSIGNED.getConfigValue(playerName, path.getDisplayName())));
                        } else if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_COMPLETE_PATH)) {
                            if (!this.hasPermission("autorank.editor.complete.path", sender)) {
                                return;
                            }

                            pathOfRequirement = callback.getStorageString(CompletePathPrompt.KEY_PATH_TO_BE_COMPLETED);
                            if (pathOfRequirement == null) {
                                return;
                            }

                            path = this.plugin.getPathManager().findPathByInternalName(pathOfRequirement, false);
                            if (path == null) {
                                return;
                            }

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Lang.PATH.getConfigValue(playerName, path.getDisplayName())));
                            this.plugin.getPathManager().completePath(path, uuid);
                        } else if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_COMPLETE_REQUIREMENT)) {
                            if (!this.hasPermission("autorank.editor.complete.requirement", sender)) {
                                return;
                            }

                            pathOfRequirement = callback.getStorageString(CompleteRequirementPrompt.KEY_PATH_OF_REQUIREMENT);
                            if (pathOfRequirement == null) {
                                return;
                            }

                            path = this.plugin.getPathManager().findPathByInternalName(pathOfRequirement, false);
                            if (path == null) {
                                return;
                            }

                            Integer requirementId = callback.getStorageInteger(CompleteRequirementRequestRequirementIdPrompt.KEY_REQUIREMENT_TO_BE_COMPLETED);
                            if (requirementId == null) {
                                return;
                            }

                            CompositeRequirement requirement = path.getRequirement(requirementId);
                            if (requirement == null) {
                                return;
                            }

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Lang.REQUIREMENT_PROGRESS.getConfigValue(playerName, path.getDisplayName())));
                            path.completeRequirement(uuid, requirementId);
                        }
                    }

                }
            });
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                conversation.startConversationAsSender(sender);
            });
            return true;
        }
    }

    public String getDescription() {
        return "Edit player data of any player";
    }

    public String getPermission() {
        return "";
    }

    public String getUsage() {
        return "/ar editor";
    }
}
