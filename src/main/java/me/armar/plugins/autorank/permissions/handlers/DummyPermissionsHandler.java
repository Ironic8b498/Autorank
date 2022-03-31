package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class DummyPermissionsHandler extends PermissionsHandler {
    public DummyPermissionsHandler(Autorank plugin) {
        super(plugin);
    }

    public Collection<String> getGroups() {
        return Collections.emptyList();
    }

    public String getName() {
        return "Dummy Permissions Handler";
    }

    public Collection<String> getPlayerGroups(Player player) {
        return Collections.emptyList();
    }

    public Collection<String> getWorldGroups(Player player, String world) {
        return Collections.emptyList();
    }

    public boolean replaceGroup(Player player, String world, String deletedGroup, String addedGroup) {
        return false;
    }

    public boolean setupPermissionsHandler() {
        return true;
    }
}
