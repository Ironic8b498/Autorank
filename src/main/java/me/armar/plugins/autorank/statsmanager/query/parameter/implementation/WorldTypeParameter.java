package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class WorldTypeParameter extends StatisticParameter {
    public WorldTypeParameter(String value) {
        super(value);
    }

    public String getKey() {
        return "world";
    }

    public void setKey(String key) {
    }
}