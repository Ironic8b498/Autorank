package me.armar.plugins.autorank.statsmanager.query.parameter;

import java.lang.reflect.InvocationTargetException;

public abstract class StatisticParameter {
    String key = null;
    String value = null;

    public StatisticParameter(String key, String value) {
        this.setKey(key);
        this.setValue(value);
    }

    public StatisticParameter(String value) {
        this.setValue(value);
    }

    public static StatisticParameter createInstance(ParameterType parameterType, String value) {
        try {
            return parameterType.getMatchingParameter().getDeclaredConstructor(String.class).newInstance(value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ParameterType getParameterType() {
        ParameterType[] var1 = ParameterType.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ParameterType type = var1[var3];
            if (type.getMatchingParameter().isAssignableFrom(this.getClass())) {
                return type;
            }
        }

        return null;
    }
}
