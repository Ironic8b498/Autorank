package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class FoodWrapper {
    private int amount;
    private ItemStack foodItem;

    public FoodWrapper(Material foodMaterial, int amount) {
        this.setAmount(amount);
        this.setFoodItem(new ItemStack(foodMaterial, amount));
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ItemStack getFoodItem() {
        return this.foodItem;
    }

    public void setFoodItem(ItemStack foodItem) {
        this.foodItem = foodItem;
    }

    public String getFoodName() {
        return this.foodItem.getType().name();
    }
}
