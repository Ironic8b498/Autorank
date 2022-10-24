package me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.editorcommand.EditorMenuPrompt;
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

public class AssignPathPrompt extends StringPrompt {
    public static String KEY_PATH_TO_BE_ASSIGNED = "pathToBeAssigned";

    public AssignPathPrompt() {
    }

    @NotNull
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        String playerName = conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME).toString();
        return ChatColor.GOLD + "What path do you want to assign to " + ChatColor.GRAY + playerName + ChatColor.GOLD + "?";
    }

    @Nullable
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
        Path path = Autorank.getInstance().getPathManager().findPathByDisplayName(s, false);
        Conversable conversable = conversationContext.getForWhom();
        if (path == null) {
            conversable.sendRawMessage(ChatColor.RED + "The path " + ChatColor.GRAY + s + ChatColor.RED + " does not exist!");
            return this;
        } else {
            UUID uuid = (UUID)conversationContext.getSessionData(SelectPlayerPrompt.KEY_UUID);
            String playerName = (String)conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME);
            if (path.isActive(uuid)) {
                conversable.sendRawMessage(ChatColor.GRAY + playerName + ChatColor.RED + " is already on that path.");
                return new EditorMenuPrompt();
            } else {
                conversationContext.setSessionData(KEY_PATH_TO_BE_ASSIGNED, path.getInternalName());
                return !path.isEligible(uuid) ? new AssignPathByForcePrompt() : END_OF_CONVERSATION;
            }
        }
    }
}
