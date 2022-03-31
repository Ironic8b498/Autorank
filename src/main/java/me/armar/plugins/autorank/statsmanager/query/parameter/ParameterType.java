package me.armar.plugins.autorank.statsmanager.query.parameter;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.statsmanager.query.parameter.implementation.*;
import org.apache.commons.lang.Validate;

import java.lang.reflect.InvocationTargetException;

public enum ParameterType {
    WORLD(WorldTypeParameter.class),
    MOB_TYPE(MobTypeParameter.class),
    BLOCK_TYPE(BlockTypeParameter.class),
    MOVEMENT_TYPE(MovementTypeParameter.class),
    FOOD_TYPE(FoodTypeParameter.class);

    private final Class<? extends StatisticParameter> matchingParameter;

    ParameterType(Class<? extends StatisticParameter> parameter) {
        this.matchingParameter = parameter;
    }

    public static ParameterType getParameterType(@NonNull String key) {
        Validate.notNull(key);
        ParameterType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ParameterType type = var1[var3];

            try {
                if (type.getMatchingParameter().getDeclaredConstructor(String.class).newInstance("").getKey().equalsIgnoreCase(key)) {
                    return type;
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException var6) {
                var6.printStackTrace();
            }
        }

        return null;
    }

    @NonNull
    public Class<? extends StatisticParameter> getMatchingParameter() {
        return this.matchingParameter;
    }

    public String getKey() {
        try {
            return this.getMatchingParameter().getDeclaredConstructor(String.class).newInstance("").getKey();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
