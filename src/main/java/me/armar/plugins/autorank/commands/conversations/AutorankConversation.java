package me.armar.plugins.autorank.commands.conversations;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutorankConversation {
    public static String CONVERSATION_SUCCESSFUL_IDENTIFIER = "success";
    static String CONVERSATION_IDENTIFIER = "autorankConversation";
    private static final Map<UUID, Boolean> isInConversation = new HashMap();
    private ConversationCallback callback;
    private ConversationFactory factory;
    private boolean started;
    private boolean ended;

    public AutorankConversation(Autorank plugin) {
        this.factory = new ConversationFactory(plugin);
        this.setupConversationFactory();
    }

    public static AutorankConversation fromFirstPrompt(Prompt prompt) {
        Autorank plugin = (Autorank)Bukkit.getServer().getPluginManager().getPlugin("Autorank");
        AutorankConversation conversation = new AutorankConversation(plugin);
        conversation.setFirstPrompt(prompt);
        return conversation;
    }

    public static boolean isInConversation(UUID uuid) {
        return isInConversation.containsKey(uuid) && isInConversation.get(uuid);
    }

    public static void setInConversation(UUID uuid, boolean inConversation) {
        isInConversation.put(uuid, inConversation);
    }

    private void setupConversationFactory() {
        Map<Object, Object> initialData = new HashMap();
        initialData.put(CONVERSATION_IDENTIFIER, this);
        this.factory = this.factory.withModality(false).withEscapeSequence("stop").withInitialSessionData(initialData).addConversationAbandonedListener(new ConversationAbandonedEvent()).withTimeout(30).withLocalEcho(false);
    }

    public void setEscapeSequence(String escapeSequence) {
        this.factory = this.factory.withEscapeSequence(escapeSequence);
    }

    public void setFirstPrompt(Prompt firstPrompt) {
        this.factory = this.factory.withFirstPrompt(firstPrompt);
    }

    public void setTimeout(int seconds) {
        this.factory = this.factory.withTimeout(seconds);
    }

    public void startConversation(Conversable conversable) {
        if (conversable.isConversing()) {
            conversable.sendRawMessage(ChatColor.RED + "You are already in a conversation.");
        } else {
            if (conversable instanceof Player) {
                if (isInConversation(((Player)conversable).getUniqueId())) {
                    conversable.sendRawMessage(ChatColor.RED + "You are already in a conversation.");
                    return;
                }

                setInConversation(((Player)conversable).getUniqueId(), true);
            }

            this.setEnded(false);
            this.setStarted(true);
            this.factory.buildConversation(conversable).begin();
        }
    }

    public void startConversationAsSender(CommandSender sender) {
        if (sender instanceof Player) {
            this.startConversation((Player)sender);
        } else {
            this.startConversation(sender.getServer().getConsoleSender());
        }

    }

    public void afterConversationEnded(ConversationCallback callback) {
        this.callback = callback;
    }

    public void conversationEnded(ConversationResult conversationResult) {
        if (conversationResult.getConversable() instanceof Player) {
            setInConversation(((Player)conversationResult.getConversable()).getUniqueId(), false);
        }

        this.setEnded(true);
        this.setStarted(false);
        if (this.callback != null) {
            this.callback.conversationEnded(conversationResult);
        }

    }

    public boolean hasStarted() {
        return this.started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    public boolean hasEnded() {
        return this.ended;
    }

    private void setEnded(boolean ended) {
        this.ended = ended;
    }
}
