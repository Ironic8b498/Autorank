package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.inventory.ItemStack;

class ItemWrapper {
    private String displayName;
    private ItemStack item;
    private boolean showShortValue = false;
    private boolean useDisplayName = false;

    public ItemWrapper(ItemStack item, String displayName, boolean showShortValue, boolean useDisplayName) {
        this.setItem(item);
        this.setDisplayName(displayName);
        this.setShowShortValue(showShortValue);
        this.setUseDisplayName(useDisplayName);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setShowShortValue(boolean showShortValue) {
        this.showShortValue = showShortValue;
    }

    public void setUseDisplayName(boolean useDisplayName) {
        this.useDisplayName = useDisplayName;
    }

    public boolean showShortValue() {
        return this.showShortValue;
    }

    public boolean useDisplayName() {
        return this.useDisplayName;
    }
}
