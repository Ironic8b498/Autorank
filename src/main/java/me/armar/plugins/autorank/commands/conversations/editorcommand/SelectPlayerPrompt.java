package me.armar.plugins.autorank.commands.conversations.editorcommand;

import me.armar.plugins.autorank.commands.conversations.prompts.RequestPlayerNamePrompt;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SelectPlayerPrompt extends RequestPlayerNamePrompt {
    public static String KEY_PLAYERNAME = "playerName";
    public static String KEY_UUID = "uuid";

    public SelectPlayerPrompt() {
        super(ChatColor.GOLD + Lang.NCC_WHAT_PLAYER.getConfigValue(), KEY_PLAYERNAME, new EditorMenuPrompt());
    }

    @Nullable
    protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
        Prompt nextPrompt = super.acceptValidatedInput(conversationContext, s);

        try {
            UUID uuid = UUIDManager.getUUID(s).get();
            conversationContext.setSessionData(KEY_UUID, uuid);
        } catch (ExecutionException | InterruptedException var5) {
            var5.printStackTrace();
        }

        return nextPrompt;
    }
}
