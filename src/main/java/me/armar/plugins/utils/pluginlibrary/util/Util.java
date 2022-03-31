package me.armar.plugins.utils.pluginlibrary.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {
    public Util() {
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
}
