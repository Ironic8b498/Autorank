package me.armar.plugins.autorank.commands.conversations;

import org.bukkit.conversations.Conversable;

import java.util.HashMap;
import java.util.Map;

public class ConversationResult {
    private final boolean endedSuccessfully;
    private final Conversable conversable;
    private boolean endedByKeyword;
    private Map<Object, Object> conversationStorage = new HashMap();

    public ConversationResult(boolean endResult, Conversable conversable) {
        this.endedSuccessfully = endResult;
        this.conversable = conversable;
    }

    public boolean wasSuccessful() {
        return this.endedSuccessfully;
    }

    public Conversable getConversable() {
        return this.conversable;
    }

    protected void setConversationStorage(Map<Object, Object> storage) {
        this.conversationStorage = storage;
    }

    public Object getStorageObject(Object key) {
        return this.conversationStorage.get(key);
    }

    public String getStorageString(Object key) {
        Object object = this.getStorageObject(key);
        return object == null ? null : object.toString();
    }

    public boolean getStorageBoolean(Object key) {
        Object object = this.getStorageObject(key);
        return object != null && (Boolean) object;
    }

    public Integer getStorageInteger(Object key) {
        Object object = this.getStorageObject(key);
        return object == null ? null : (Integer)object;
    }

    public boolean isEndedByKeyword() {
        return this.endedByKeyword;
    }

    public void setEndedByKeyword(boolean endedByKeyword) {
        this.endedByKeyword = endedByKeyword;
    }
}
