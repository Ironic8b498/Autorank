package me.armar.plugins.autorank.commands.conversations.resetcommand;

import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPromptCallback;
import me.armar.plugins.autorank.language.Lang;
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
        String message = ChatColor.DARK_AQUA + Lang.NCC_ARE_YOU_SURE_RESET.getConfigValue(ChatColor.GOLD + conversationContext.getSessionData("playerName").toString() + ChatColor.DARK_AQUA);
        String resetType = conversationContext.getSessionData(ResetConversationType.RESET_TYPE).toString();
        if (resetType.equals(ResetConversationType.RESET_COMPLETED_PATHS)) {
            message = message.replace("%type%", Lang.NCC_COMPLETED.getConfigValue());
        } else if (resetType.equals(ResetConversationType.RESET_ACTIVE_PATHS)) {
            message = message.replace("%type%", Lang.NCC_ACTIVE.getConfigValue());
        } else if (resetType.equals(ResetConversationType.RESET_ALL_PROGRESS)) {
            message = message.replace("%type%", Lang.NCC_ALL_PROGRESS.getConfigValue());
        } else {
            message = message.replace("%type%", Lang.NCC_ACTIVE_PROGRESS.getConfigValue());
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
