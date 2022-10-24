package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.LibraryHook;
import me.armar.plugins.utils.pluginlibrary.hooks.VaultHook;
import org.bukkit.entity.Player;

import java.util.*;

public class VaultPermissionsHandler extends PermissionsHandler {
    public VaultPermissionsHandler(Autorank plugin) {
        super(plugin);
    }

    public boolean addGroup(Player player, String world, String group) {
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            return VaultHook.getPermissions() != null && VaultHook.getPermissions().playerAddGroup(world, player, group);
        } else {
            return false;
        }
    }

    public boolean demotePlayer(Player player, String world, String groupFrom, String groupTo) {
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            if (VaultHook.getPermissions() == null) {
                return false;
            } else {
                if (world == null && VaultHook.getPermissions().getName().toLowerCase().contains("bpermissions")) {
                    world = player.getWorld().getName();
                }

                Collection<String> groupsBeforeAdd = this.getPlayerGroups(player);
                Iterator var7 = groupsBeforeAdd.iterator();

                while(var7.hasNext()) {
                    String group = (String)var7.next();
                    this.getPlugin().debugMessage("Group of " + player.getName() + " before removing: " + group);
                }

                boolean worked1 = this.removeGroup(player, world, groupFrom);
                boolean worked2 = false;
                if (worked1) {
                    Collection<String> groupsAfterAdd = this.getPlayerGroups(player);
                    Iterator var10 = groupsAfterAdd.iterator();

                    while(var10.hasNext()) {
                        String group = (String)var10.next();
                        this.getPlugin().debugMessage("Group of " + player.getName() + " after removing: " + group);
                    }

                    worked2 = this.addGroup(player, world, groupTo);
                }

                return worked1 && worked2;
            }
        } else {
            return false;
        }
    }

    public Collection<String> getGroups() {
        List<String> groups = new ArrayList();
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            if (VaultHook.getPermissions() == null) {
                return Collections.unmodifiableCollection(groups);
            } else {
                groups.addAll(Arrays.asList(VaultHook.getPermissions().getGroups()));
                return Collections.unmodifiableCollection(groups);
            }
        } else {
            return Collections.unmodifiableCollection(groups);
        }
    }

    public String getName() {
        return VaultHook.getPermissions().getName();
    }

    public Collection<String> getPlayerGroups(Player player) {
        List<String> groups = new ArrayList();
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            if (VaultHook.getPermissions() == null) {
                return Collections.unmodifiableCollection(groups);
            } else {
                if (this.getPlugin().getSettingsConfig().onlyUsePrimaryGroupVault()) {
                    groups.add(VaultHook.getPermissions().getPrimaryGroup(player));
                } else {
                    groups.addAll(Arrays.asList(VaultHook.getPermissions().getPlayerGroups(player)));
                }

                return Collections.unmodifiableCollection(groups);
            }
        } else {
            return Collections.unmodifiableCollection(groups);
        }
    }

    public Collection<String> getWorldGroups(Player player, String world) {
        List<String> groups = new ArrayList();
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            if (VaultHook.getPermissions() == null) {
                return Collections.unmodifiableCollection(groups);
            } else {
                groups.addAll(Arrays.asList(VaultHook.getPermissions().getPlayerGroups(world, player.getName())));
                return Collections.unmodifiableCollection(groups);
            }
        } else {
            return Collections.unmodifiableCollection(groups);
        }
    }

    public boolean removeGroup(Player player, String world, String group) {
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            return VaultHook.getPermissions() != null && VaultHook.getPermissions().playerRemoveGroup(world, player, group);
        } else {
            return false;
        }
    }

    public boolean replaceGroup(Player player, String world, String oldGroup, String newGroup) {
        LibraryHook hook = this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).orElse(null);
        if (hook != null && hook.isHooked()) {
            if (VaultHook.getPermissions() == null) {
                return false;
            } else {
                if (world == null && VaultHook.getPermissions().getName().toLowerCase().contains("bpermissions")) {
                    world = player.getWorld().getName();
                }

                Collection<String> groupsBeforeAdd = this.getPlayerGroups(player);
                Iterator var7 = groupsBeforeAdd.iterator();

                while(var7.hasNext()) {
                    String group = (String)var7.next();
                    this.getPlugin().debugMessage("Group of " + player.getName() + " before adding: " + group);
                }

                boolean worked1 = this.addGroup(player, world, newGroup);
                boolean worked2 = false;
                if (worked1) {
                    Collection<String> groupsAfterAdd = this.getPlayerGroups(player);
                    Iterator var10 = groupsAfterAdd.iterator();

                    String group;
                    while(var10.hasNext()) {
                        group = (String)var10.next();
                        this.getPlugin().debugMessage("Group of " + player.getName() + " after adding: " + group);
                    }

                    if (VaultHook.getPermissions().getName().toLowerCase().contains("permissionsex")) {
                        if (groupsAfterAdd.size() >= groupsBeforeAdd.size() + 1) {
                            worked2 = this.removeGroup(player, world, oldGroup);
                        } else if (groupsAfterAdd.size() == 1) {
                            var10 = groupsBeforeAdd.iterator();

                            while(var10.hasNext()) {
                                group = (String)var10.next();
                                if (!group.equalsIgnoreCase(oldGroup)) {
                                    this.addGroup(player, world, group);
                                }
                            }

                            worked2 = true;
                        } else {
                            worked2 = true;
                        }
                    } else {
                        worked2 = this.removeGroup(player, world, oldGroup);
                    }
                }

                return worked1 && worked2;
            }
        } else {
            return false;
        }
    }

    public boolean setupPermissionsHandler() {
        return this.getPlugin().getDependencyManager().getLibraryHook(Library.VAULT).map(LibraryHook::isHooked).orElse(false);
    }
}
