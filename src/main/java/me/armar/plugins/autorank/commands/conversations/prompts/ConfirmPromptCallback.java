package me.armar.plugins.autorank.commands.conversations.prompts;

public interface ConfirmPromptCallback {
    void promptConfirmed();

    void promptDenied();
}