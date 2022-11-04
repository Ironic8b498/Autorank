package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class GroupManagerHandler extends PermissionsHandler {
    private GroupManager groupManager;

    public GroupManagerHandler(Autorank plugin) {
        super(plugin);
    }

    public String getGroup(Player player) {
        AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissions(player);
        return handler == null ? null : handler.getGroup(player.getName());
    }

    public Collection<String> getGroups() {
        List<String> groups = new ArrayList();
        Iterator var2 = this.getPlugin().getServer().getWorlds().iterator();

        while(var2.hasNext()) {
            World world = (World)var2.next();
            String worldName = world.getName();
            Collection<Group> worldGroup = this.groupManager.getWorldsHolder().getWorldData(worldName).getGroupList();
            List<Group> list = new ArrayList(worldGroup);
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
                Group group = (Group)var7.next();
                groups.add(group.getName());
            }
        }

        return Collections.unmodifiableCollection(groups);
    }

    public String getName() {
        return "GroupManager";
    }

    public Collection<String> getPlayerGroups(Player player) {
        AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissions(player);
        if (handler == null) {
            return null;
        } else {
            List<String> groups = Collections.singletonList(handler.getPrimaryGroup(player.getName()));
            return Collections.unmodifiableCollection(groups);
        }
    }

    public String getPrefix(Player player) {
        AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissions(player);
        return handler == null ? null : handler.getUserPrefix(player.getName());
    }

    public String getSuffix(Player player) {
        AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissions(player);
        return handler == null ? null : handler.getUserSuffix(player.getName());
    }

    public Collection<String> getWorldGroups(Player player, String world) {
        List<String> groups = new ArrayList();
        String[] var4 = this.groupManager.getWorldsHolder().getWorldPermissions(world).getGroups(player.getName());
        int var5 = var4.length;

        groups.addAll(Arrays.asList(var4).subList(0, var5));

        return Collections.unmodifiableCollection(groups);
    }

    public boolean hasPermission(Player player, String node) {
        AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissions(player);
        return handler != null && handler.has(player, node);
    }

    public boolean replaceGroup(Player player, String world, String groupFrom, String groupTo) {
        return this.setGroup(player, groupTo, world);
    }

    public boolean setGroup(Player player, String group, String world) {
        OverloadedWorldHolder handler;
        if (world != null) {
            handler = this.groupManager.getWorldsHolder().getWorldData(world);
        } else {
            handler = this.groupManager.getWorldsHolder().getWorldData(player);
        }

        if (handler == null) {
            return false;
        } else {
            handler.getUser(player.getName()).setGroup(handler.getGroup(group));
            return true;
        }
    }

    public boolean setupPermissionsHandler() {
        PluginManager pluginManager = this.getPlugin().getServer().getPluginManager();
        Plugin GMplugin = pluginManager.getPlugin("GroupManager");
        if (GMplugin != null && GMplugin.isEnabled()) {
            this.groupManager = (GroupManager)GMplugin;
        }

        return this.groupManager != null;
    }
}
