package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class BlockTypeParameter extends StatisticParameter {
    public BlockTypeParameter(String value) {
        super(value);
    }

    public String getKey() {
        return "block";
    }

    public void setKey(String key) {
    }
}
