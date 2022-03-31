package me.armar.plugins.utils.pluginlibrary.hooks.afkmanager;

import java.util.UUID;

public interface AFKManager {
    boolean isAFK(UUID var1);

    boolean hasAFKData();
}
