package me.armar.plugins.autorank.commands.conversations.resetcommand;

import me.armar.plugins.autorank.commands.conversations.prompts.RequestPlayerNamePrompt;
import me.armar.plugins.autorank.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResetConversationType extends FixedSetPrompt {
    public static String RESET_TYPE = "resetType";
    public static String RESET_ACTIVE_PROGRESS = "active progress";
    public static String RESET_ALL_PROGRESS = "all progress";
    public static String RESET_COMPLETED_PATHS = "completed paths";
    public static String RESET_ACTIVE_PATHS = "active paths";

    public ResetConversationType() {
        super(RESET_ACTIVE_PROGRESS, RESET_COMPLETED_PATHS, RESET_ACTIVE_PATHS, RESET_ALL_PROGRESS);
    }

    @Nullable
    protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
        conversationContext.setSessionData(RESET_TYPE, s);
        String requestPlayerMessage;
        if (s.equals(RESET_COMPLETED_PATHS)) {
            requestPlayerMessage = ChatColor.DARK_AQUA + Lang.NCC_OF_WHICH_PLAYER_COMPLETED.getConfigValue();
        } else if (s.equals(RESET_ACTIVE_PATHS)) {
            requestPlayerMessage = ChatColor.DARK_AQUA + Lang.NCC_OF_WHICH_PLAYER_ACTIVE.getConfigValue();
        } else if (s.equals(RESET_ALL_PROGRESS)) {
            requestPlayerMessage = ChatColor.DARK_AQUA + Lang.NCC_OF_WHICH_PLAYER_ALL_PROGRESS.getConfigValue();
        } else {
            requestPlayerMessage = ChatColor.DARK_AQUA + Lang.NCC_OF_WHICH_PLAYER_ACTIVE_PROGRESS.getConfigValue();
        }

        return new RequestPlayerNamePrompt(requestPlayerMessage, new ResetConfirmation());
    }

    @NotNull
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.DARK_AQUA + Lang.NCC_WHAT_DO_YOU.getConfigValue() + ChatColor.RED + this.formatFixedSet();
    }
}
