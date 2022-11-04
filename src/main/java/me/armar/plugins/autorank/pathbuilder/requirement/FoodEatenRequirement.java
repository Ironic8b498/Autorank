package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Material;

import java.util.UUID;

public class FoodEatenRequirement extends AbstractRequirement {
    FoodWrapper foodEaten = null;

    public FoodEatenRequirement() {
    }

    public String getDescription() {
        String desc = "";
        int amount = this.foodEaten.getAmount();
        String foodType = this.foodEaten.getFoodName();
        if (foodType != null && !foodType.trim().equals("")) {
            desc = Lang.FOOD_EATEN_REQUIREMENT.getConfigValue(amount + " " + foodType.toLowerCase().replace("_", " ") + "(s)");
        } else {
            desc = Lang.FOOD_EATEN_REQUIREMENT.getConfigValue(amount + " food");
        }

        if (this.isWorldSpecific()) {
            desc = desc.concat(" (in world '" + this.getWorld() + "')");
        }

        return desc;
    }

    public String getProgressString(UUID uuid) {
        String progress = "";
        int amount = this.foodEaten.getAmount();
        String foodType = this.foodEaten.getFoodName();
        int totalFoodEaten = 0;
  //      int totalFoodEaten;
        if (foodType != null) {
            totalFoodEaten = this.getStatisticsManager().getFoodEaten(uuid, this.getWorld(), Material.getMaterial(foodType));
            foodType = foodType.toLowerCase();
        } else {
            totalFoodEaten = this.getStatisticsManager().getFoodEaten(uuid, this.getWorld(), null);
            foodType = "food";
        }

        progress = progress.concat(totalFoodEaten + "/" + amount + " " + foodType.replace("_", " ") + "(s)");
        return progress;
    }

    protected boolean meetsRequirement(UUID uuid) {
        int amount = this.foodEaten.getAmount();
        String foodType = this.foodEaten.getFoodName();
        int totalFoodEaten = 0;
  //      int totalFoodEaten;
        if (foodType == null) {
            totalFoodEaten = this.getStatisticsManager().getFoodEaten(uuid, this.getWorld(), null);
        } else {
            totalFoodEaten = this.getStatisticsManager().getFoodEaten(uuid, this.getWorld(), Material.getMaterial(foodType));
        }

        return totalFoodEaten >= amount;
    }

    public boolean initRequirement(String[] options) {
        int total = Integer.parseInt(options[0]);
        String foodType = "";
        if (options.length > 1) {
            foodType = options[1].trim();
        }

        Material foodMaterial = Material.matchMaterial(foodType);
        if (foodMaterial == null) {
            this.registerWarningMessage("Food '" + foodType + "' is not a valid type of food.");
            return false;
        } else {
            this.foodEaten = new FoodWrapper(foodMaterial, total);
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int totalFoodEaten = 0;
        String foodType = this.foodEaten.getFoodName();
   //     int totalFoodEaten;
        if (foodType == null) {
            totalFoodEaten = this.getStatisticsManager().getFoodEaten(uuid, this.getWorld(), null);
        } else {
            totalFoodEaten = this.getStatisticsManager().getFoodEaten(uuid, this.getWorld(), Material.getMaterial(foodType));
        }

        return (double) totalFoodEaten / (double)this.foodEaten.getAmount();
    }
}
