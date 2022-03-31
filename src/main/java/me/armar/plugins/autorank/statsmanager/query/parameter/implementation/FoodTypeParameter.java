package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class FoodTypeParameter extends StatisticParameter {
    public FoodTypeParameter(String value) {
        super(value);
    }

    public String getKey() {
        return "foodType";
    }

    public void setKey(String key) {
    }
}
