package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.World;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.level.Level;

import java.util.UUID;

public class BentoBoxHook extends LibraryHook {
    private BentoBox bentoBox;

    public BentoBoxHook() {
    }

    public boolean isHooked() {
        return this.bentoBox != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.BENTOBOX)) {
            return false;
        } else {
            this.bentoBox = (BentoBox)this.getServer().getPluginManager().getPlugin(Library.BENTOBOX.getInternalPluginName());
            return this.bentoBox != null;
        }
    }

    public Long getIslandLevel(World world, UUID uuid) {
        UUID owner = BentoBox.getInstance().getIslandsManager().getOwner(world, uuid);
        return owner == null ? null : BentoBox.getInstance().getAddonsManager().getAddonByName("Level").map((l) -> {
            return ((Level)l).getIslandLevel(world, uuid);
        }).orElse(1L);
    }

    public boolean gethasIsland(World world, UUID uuid) {
        return BentoBox.getInstance().getIslandsManager().hasIsland(world, uuid);
    }
}
