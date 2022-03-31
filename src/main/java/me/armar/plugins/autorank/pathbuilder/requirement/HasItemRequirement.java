package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HasItemRequirement extends AbstractRequirement {
    ItemWrapper neededItem = null;

    public HasItemRequirement() {
    }

    public String getDescription() {
        ItemStack item = this.neededItem.getItem();
        StringBuilder arg = new StringBuilder(item.getAmount() + " ");
        if (this.neededItem.getDisplayName() != null) {
            arg.append(this.neededItem.getDisplayName());
        } else {
            arg.append(item.getType().toString().replace("_", " ").toLowerCase());
        }

        String lang = Lang.ITEM_REQUIREMENT.getConfigValue(arg.toString());
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        ItemStack item = this.neededItem.getItem();
        int slotAmount = 0;
        ItemStack[] var4 = player.getInventory().getStorageContents();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ItemStack itemInInv = var4[var6];
            if (itemInInv != null && itemInInv.isSimilar(item)) {
                slotAmount += itemInInv.getAmount();
            }
        }

        return slotAmount + "/" + item.getAmount();
    }

    public boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else {
            ItemStack item = this.neededItem.getItem();
            if (item == null) {
                return false;
            } else {
                return !this.neededItem.useDisplayName() ? player.getInventory().containsAtLeast(item, item.getAmount()) : AutorankTools.containsAtLeast(player, item, item.getAmount(), this.neededItem.getDisplayName());
            }
        }
    }

    public boolean initRequirement(String[] options) {
        String materialName = null;
        int amount = 1;
        String displayName = null;
        boolean useDisplayName = false;
        if (options.length > 0) {
            materialName = options[0].trim().toUpperCase().replace(" ", "_");
        }

        if (options.length > 1) {
            amount = (int)AutorankTools.stringToDouble(options[1]);
        }

        if (options.length > 2) {
            displayName = options[2];
        }

        if (options.length > 3) {
            useDisplayName = options[3].equalsIgnoreCase("true");
        }

        if (materialName == null) {
            this.registerWarningMessage("There is no material specified.");
            return false;
        } else {
            Material matchedMaterial = Material.matchMaterial(materialName);
            if (matchedMaterial == null) {
                this.registerWarningMessage("Material '" + materialName + "' is not a valid material.");
                return false;
            } else {
                ItemStack item = new ItemStack(matchedMaterial, amount);
                this.neededItem = new ItemWrapper(item, displayName, false, useDisplayName);
                if (amount <= 0) {
                    this.registerWarningMessage("Amount must be strictly higher than 0");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
