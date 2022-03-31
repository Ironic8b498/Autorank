package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BlocksPlacedRequirement extends AbstractRequirement {
    BlocksPlacedWrapper wrapper = null;

    public BlocksPlacedRequirement() {
    }

    public String getDescription() {
        ItemStack item = this.wrapper.getItem();
        StringBuilder arg = new StringBuilder("" + this.wrapper.getBlocksPlaced());
        if (item == null) {
            arg.append(" blocks");
        } else if (this.wrapper.getDisplayName() != null) {
            arg.append(" ").append(this.wrapper.getDisplayName());
        } else {
            arg.append(" ").append(item.getType().name().replace("_", " ").toLowerCase());
        }

        String lang = Lang.PLACED_BLOCKS_REQUIREMENT.getConfigValue(arg.toString());
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        int progress;
        if (this.wrapper.getItem() == null) {
            progress = this.getStatisticsManager().getBlocksPlaced(uuid, this.getWorld(), null);
        } else {
            progress = this.getStatisticsManager().getBlocksPlaced(uuid, this.getWorld(), this.wrapper.getItem().getType());
        }

        return progress + "/" + this.wrapper.getBlocksPlaced();
    }

    protected boolean meetsRequirement(UUID uuid) {
        int progress;
        if (this.wrapper.getItem() == null) {
            progress = this.getStatisticsManager().getBlocksPlaced(uuid, this.getWorld(), null);
        } else {
            progress = this.getStatisticsManager().getBlocksPlaced(uuid, this.getWorld(), this.wrapper.getItem().getType());
        }

        return progress >= this.wrapper.getBlocksPlaced();
    }

    public boolean initRequirement(String[] options) {
        String materialName = null;
        int amount = 1;
        String displayName = null;
        boolean useDisplayName = false;
        if (options.length == 1) {
            amount = Integer.parseInt(options[0].trim());
        }

        if (options.length > 1) {
            materialName = options[0].trim().toUpperCase().replace(" ", "_");
            amount = Integer.parseInt(options[1].trim());
        }

        if (options.length > 2) {
            displayName = options[2];
        }

        if (options.length > 3) {
            useDisplayName = options[3].equalsIgnoreCase("true");
        }

        ItemStack itemStack = null;
        if (materialName != null) {
            Material matchedMaterial = Material.matchMaterial(materialName);
            if (matchedMaterial == null) {
                this.registerWarningMessage("Material '" + materialName + "' is not a valid material.");
                return false;
            }

            itemStack = new ItemStack(matchedMaterial, amount);
        }

        this.wrapper = new BlocksPlacedWrapper(itemStack, displayName, false, useDisplayName);
        this.wrapper.setBlocksPlaced(amount);
        if (amount < 0) {
            this.registerWarningMessage("Amount is not provided or smaller than 0.");
            return false;
        } else if (this.wrapper == null) {
            this.registerWarningMessage("No valid block provided.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int progress;
        if (this.wrapper.getItem() == null) {
            progress = this.getStatisticsManager().getBlocksPlaced(uuid, this.getWorld(), null);
        } else {
            progress = this.getStatisticsManager().getBlocksPlaced(uuid, this.getWorld(), this.wrapper.getItem().getType());
        }

        return (double)progress * 1.0D / (double)this.wrapper.getBlocksPlaced();
    }
}
