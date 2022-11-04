package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Material;

import java.util.UUID;

public class ItemsCraftedRequirement extends AbstractRequirement {
    int timesCrafted = -1;
    Material itemCrafted = null;

    public ItemsCraftedRequirement() {
    }

    public String getDescription() {
        String lang = this.itemCrafted == null ? Lang.ITEMS_CRAFTED_REQUIREMENT.getConfigValue(this.timesCrafted) : Lang.ITEM_CRAFTED_REQUIREMENT.getConfigValue(this.timesCrafted, this.itemCrafted);
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getItemsCrafted(uuid, this.getWorld(), this.itemCrafted) + "/" + this.timesCrafted;
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getItemsCrafted(uuid, this.getWorld(), this.itemCrafted) >= this.timesCrafted;
    }

    public boolean initRequirement(String[] options) {
        if (options.length == 0) {
            return false;
        } else {
            if (options.length == 1) {
                try {
                    this.timesCrafted = Integer.parseInt(options[0]);
                } catch (Exception var4) {
                    this.registerWarningMessage("An invalid number is provided");
                    return false;
                }
            } else {
                try {
                    this.itemCrafted = Material.getMaterial(options[0].trim().toUpperCase());
                    this.timesCrafted = Integer.parseInt(options[1]);
                } catch (Exception var3) {
                    this.registerWarningMessage("An invalid number is provided");
                    return false;
                }
            }

            if (this.timesCrafted < 0) {
                this.registerWarningMessage("No number is provided or smaller than 0.");
                return false;
            } else {
                return true;
            }
        }
    }

    public double getProgressPercentage(UUID uuid) {
        return (double) this.getStatisticsManager().getItemsCrafted(uuid, this.getWorld(), this.itemCrafted) / (double)this.timesCrafted;
    }
}
