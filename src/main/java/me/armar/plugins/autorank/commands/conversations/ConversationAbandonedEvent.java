package me.armar.plugins.autorank.commands.conversations;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;

public class ConversationAbandonedEvent implements ConversationAbandonedListener {
    public ConversationAbandonedEvent() {
    }

    public void conversationAbandoned(org.bukkit.conversations.ConversationAbandonedEvent conversationAbandonedEvent) {
        Object conversationObject = conversationAbandonedEvent.getContext().getSessionData(AutorankConversation.CONVERSATION_IDENTIFIER);
        if (conversationObject != null) {
            AutorankConversation conversation = (AutorankConversation)conversationObject;
            Object endedSuccesfully = conversationAbandonedEvent.getContext().getSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER);
            Conversable conversable = conversationAbandonedEvent.getContext().getForWhom();
            ConversationResult result;
            if (endedSuccesfully == null) {
                result = new ConversationResult(false, conversable);
            } else {
                result = new ConversationResult((Boolean)endedSuccesfully, conversable);
            }

            result.setConversationStorage(conversationAbandonedEvent.getContext().getAllSessionData());
            ConversationCanceller canceller = conversationAbandonedEvent.getCanceller();
            if (canceller instanceof InactivityConversationCanceller) {
                conversable.sendRawMessage(ChatColor.GRAY + Lang.NCC_CONVERSATION_HAS_ENDED_BECAUSE.getConfigValue());
            } else if (canceller instanceof ExactMatchConversationCanceller) {
                conversable.sendRawMessage(ChatColor.GRAY + Lang.NCC_CONVERSATION_HAS_BEEN.getConfigValue());
                result.setEndedByKeyword(true);
            } else {
                conversable.sendRawMessage(ChatColor.GRAY + Lang.NCC_CONVERSATION_HAS_ENDED.getConfigValue());
            }

            conversation.conversationEnded(result);
        }
    }
}
