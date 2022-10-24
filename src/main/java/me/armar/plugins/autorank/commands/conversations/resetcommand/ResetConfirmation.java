package me.armar.plugins.autorank.commands.conversations.resetcommand;

import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPromptCallback;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ResetConfirmation extends MessagePrompt {
    ResetConfirmation() {
    }

    @Nullable
    protected Prompt getNextPrompt(@NotNull final ConversationContext conversationContext) {
        String message = ChatColor.DARK_AQUA + "Are you sure you want to reset the %type% of " + ChatColor.GOLD + conversationContext.getSessionData("playerName") + ChatColor.DARK_AQUA + "? Please confirm or deny.";
        String resetType = conversationContext.getSessionData(ResetConversationType.RESET_TYPE).toString();
        if (resetType.equals(ResetConversationType.RESET_COMPLETED_PATHS)) {
            message = message.replace("%type%", "completed paths");
        } else if (resetType.equals(ResetConversationType.RESET_ACTIVE_PATHS)) {
            message = message.replace("%type%", "active paths");
        } else if (resetType.equals(ResetConversationType.RESET_ALL_PROGRESS)) {
            message = message.replace("%type%", "all progress");
        } else {
            message = message.replace("%type%", "active progress");
        }

        return new ConfirmPrompt(message, new ConfirmPromptCallback() {
            public void promptConfirmed() {
                conversationContext.setSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
            }

            public void promptDenied() {
                conversationContext.setSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, false);
            }
        });
    }

    @NotNull
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return "";
    }
}
