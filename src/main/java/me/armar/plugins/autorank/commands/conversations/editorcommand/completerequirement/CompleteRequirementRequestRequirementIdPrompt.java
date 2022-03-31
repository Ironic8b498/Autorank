package me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CompleteRequirementRequestRequirementIdPrompt extends StringPrompt {
    public static String KEY_REQUIREMENT_TO_BE_COMPLETED = "requirementToBeCompleted";

    public CompleteRequirementRequestRequirementIdPrompt() {
    }

    @NotNull
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.GOLD + "What requirement id do you want to complete?";
    }

    @Nullable
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
        Path path = Autorank.getInstance().getPathManager().findPathByInternalName(conversationContext.getSessionData(CompleteRequirementPrompt.KEY_PATH_OF_REQUIREMENT).toString(), false);
        Conversable conversable = conversationContext.getForWhom();
        int requirementId = Integer.parseInt(s.trim());
        UUID uuid = (UUID)conversationContext.getSessionData(SelectPlayerPrompt.KEY_UUID);
        String playerName = (String)conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME);
        if (path.getRequirement(requirementId) == null) {
            conversable.sendRawMessage(ChatColor.RED + "That requirement id does not exist for " + ChatColor.GRAY + path.getDisplayName());
            return this;
        } else if (path.hasCompletedRequirement(uuid, requirementId)) {
            conversable.sendRawMessage(ChatColor.GRAY + playerName + ChatColor.RED + " has already completed this requirement");
            return this;
        } else {
            conversationContext.setSessionData(KEY_REQUIREMENT_TO_BE_COMPLETED, requirementId);
            return END_OF_CONVERSATION;
        }
    }
}
