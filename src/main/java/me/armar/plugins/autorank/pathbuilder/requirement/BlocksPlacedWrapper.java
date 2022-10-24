package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.inventory.ItemStack;

class BlocksPlacedWrapper extends ItemWrapper {
    private int blocksPlaced;

    public BlocksPlacedWrapper(ItemStack item, String displayName, boolean showShortValue, boolean useDisplayName) {
        super(item, displayName, showShortValue, useDisplayName);
    }

    public int getBlocksPlaced() {
        return this.blocksPlaced;
    }

    public void setBlocksPlaced(int blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }
}
