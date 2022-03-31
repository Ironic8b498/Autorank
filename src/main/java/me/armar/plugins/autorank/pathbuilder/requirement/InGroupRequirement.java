package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;

public class InGroupRequirement extends AbstractRequirement {
    String group = null;

    public InGroupRequirement() {
    }

    public String getDescription() {
        return Lang.GROUP_REQUIREMENT.getConfigValue(this.group);
    }

    public String getProgressString(Player player) {
        Collection<String> groups = this.getAutorank().getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);
        Iterator var3 = groups.iterator();

        String groupString;
        do {
            if (!var3.hasNext()) {
                return "you're not in the group";
            }

            groupString = (String)var3.next();
        } while(!groupString.equalsIgnoreCase(this.group));

        return "you're in the group!";
    }

    public boolean meetsRequirement(Player player) {
        Iterator var2 = this.getAutorank().getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player).iterator();

        String groupString;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            groupString = (String)var2.next();
        } while(!groupString.equalsIgnoreCase(this.group));

        return true;
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.group = options[0].trim();
        }

        if (this.group == null) {
            this.registerWarningMessage("No group is provided");
            return false;
        } else {
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
