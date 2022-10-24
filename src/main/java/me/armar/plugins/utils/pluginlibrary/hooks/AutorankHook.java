package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AutorankHook extends LibraryHook {
    private Autorank autorank;

    public AutorankHook() {
    }

    public boolean isHooked() {
        return this.autorank != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.AUTORANK)) {
            return false;
        } else {
            this.autorank = (Autorank)this.getServer().getPluginManager().getPlugin(Library.AUTORANK.getInternalPluginName());
            return this.autorank != null;
        }
    }

    public int getLocalPlayTime(UUID uuid) throws ExecutionException, InterruptedException {
        return !this.isHooked() ? -1 : this.autorank.getAPI().getLocalPlayTime(uuid).get();
    }

    public int getGlobalPlayTime(UUID uuid) throws ExecutionException, InterruptedException {
        return !this.isHooked() ? -1 : this.autorank.getAPI().getGlobalPlayTime(uuid).get();
    }

    public Collection<String> getPermissionGroups(Player player) {
        return !this.isHooked() ? new ArrayList() : this.autorank.getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);
    }

    public void registerRequirement(String requirementName, Class<? extends AbstractRequirement> req) {
        if (this.isHooked()) {
            this.autorank.registerRequirement(requirementName, req);
        }
    }

    public void registerResult(String resultName, Class<? extends AbstractResult> res) {
        if (this.isHooked()) {
            this.autorank.registerResult(resultName, res);
        }
    }
}
