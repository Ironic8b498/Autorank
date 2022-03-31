package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.inventory.ItemStack;

class BlocksBrokenWrapper extends ItemWrapper {
    private int blocksBroken;

    public BlocksBrokenWrapper(ItemStack item, String displayName, boolean showShortValue, boolean useDisplayName) {
        super(item, displayName, showShortValue, useDisplayName);
    }

    public int getBlocksBroken() {
        return this.blocksBroken;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }
}
