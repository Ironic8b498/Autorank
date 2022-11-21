package me.armar.plugins.autorank.commands.conversations.prompts;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ConfirmPrompt extends FixedSetPrompt {
    private String message;
    private static final List<String> confirmWords = Arrays.asList("yes", "confirm", "allow");
    private final Prompt confirmPrompt;
    private final Prompt denyPrompt;
    private final List<String> denyWords;
    private final ConfirmPromptCallback callback;

    public ConfirmPrompt(String message, @NonNull Prompt confirmPrompt, @NonNull Prompt denyPrompt, ConfirmPromptCallback callback) {
        super("yes", "confirm", "allow", "no", "deny", "disallow");
        this.message = ChatColor.GOLD + Lang.NCC_ARE_YOU_SURE_PERFORM.getConfigValue() + ChatColor.GREEN + "yes" + ChatColor.GOLD + " or " + ChatColor.RED + "no" + ChatColor.GOLD + ".";
        this.denyWords = Arrays.asList("no", "deny", "disallow");
        if (message != null) {
            this.message = message;
        }

        if (confirmPrompt == null) {
            this.confirmPrompt = Prompt.END_OF_CONVERSATION;
        } else {
            this.confirmPrompt = confirmPrompt;
        }

        if (denyPrompt == null) {
            this.denyPrompt = Prompt.END_OF_CONVERSATION;
        } else {
            this.denyPrompt = denyPrompt;
        }

        this.callback = callback;
    }

    public ConfirmPrompt(@NonNull Prompt confirmPrompt, @NonNull Prompt denyPrompt) {
        this(null, confirmPrompt, denyPrompt, null);
    }

    public ConfirmPrompt(@NonNull Prompt confirmPrompt) {
        this(null, confirmPrompt, END_OF_CONVERSATION, null);
    }

    public ConfirmPrompt(String message, ConfirmPromptCallback callback) {
        this(message, null, null, callback);
    }

    @Nullable
    protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
        if (confirmWords.stream().anyMatch((confirmWord) -> {
            return confirmWord.equalsIgnoreCase(s);
        })) {
            if (this.callback != null) {
                this.callback.promptConfirmed();
            }

            return this.confirmPrompt;
        } else {
            if (this.callback != null) {
                this.callback.promptDenied();
            }

            return this.denyPrompt;
        }
    }

    @NotNull
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return this.message;
    }

    public String getMessage() {
        return this.message;
    }
}
