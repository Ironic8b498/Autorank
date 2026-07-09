package me.armar.plugins.autorank.util;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArUtil {
    public static boolean isHigherVersion(String versionCheck) {
        String currentVersion = Bukkit.getBukkitVersion();
     //   versionCheck = "1.20.5";
        String versionCheck26 = "26.1";

        if (currentVersion.equalsIgnoreCase("Unknown") || !currentVersion.contains("."))
            return false;

        List<String> splitCur = new ArrayList<>();
        Collections.addAll(splitCur, currentVersion.split("\\."));

        List<String> splitCheck = new ArrayList<>();
        Collections.addAll(splitCheck, versionCheck.split("\\."));

        List<String> splitCheck26 = new ArrayList<>();
        Collections.addAll(splitCheck26, versionCheck26.split("\\."));

        if (splitCur.isEmpty() || splitCheck.isEmpty()) {
            return false;
        }

        if (splitCur.size() == 1) {
            splitCur.add("0");
            splitCur.add("0");
        } else if (splitCur.size() == 2) {
            splitCur.add("0");
        }

        if (splitCheck.size() == 1) {
            splitCheck.add("0");
            splitCheck.add("0");
        } else if (splitCheck.size() == 2) {
            splitCheck.add("0");
        }

        if (splitCheck26.size() == 1) {
            splitCheck26.add("0");
            splitCheck26.add("0");
        } else if (splitCheck26.size() == 2) {
            splitCheck26.add("0");
        }

        if (1 == Integer.parseInt(splitCur.getFirst().replaceAll("[^\\d.]", ""))) {

            // Check if the second digit of the version to be checked is higher than the second digit of the current
            // version of MC
            if (Integer.parseInt(splitCheck.get(1).replaceAll("[^\\d.]", "")) > Integer.parseInt(splitCur.get(1)
                    .replaceAll("[^\\d.]", ""))) {
                return false;
            }

            // Check if the third digit of the version to be checked is higher than the third digit of the current
            // version of MC
            return Integer.parseInt(splitCheck.get(2).replaceAll("[^\\d.]", "")) <= Integer.parseInt(splitCur.get(2)
                    .replaceAll("[^\\d.]", ""));
        }

        // Check if the first digit of the version to be checked is higher than the first digit of the current
        // version of MC
        if (26 == Integer.parseInt(splitCur.getFirst().replaceAll("[^\\d.]", ""))) {

            // Check if the second digit of the version to be checked is higher than the second digit of the current
            // version of MC
            if (Integer.parseInt(splitCheck26.get(1).replaceAll("[^\\d.]", "")) > Integer.parseInt(splitCur.get(1)
                    .replaceAll("[^\\d.]", ""))) {
                return false;
            }
            return true;//Integer.parseInt(splitCheck26.get(2).replaceAll("[^\\d.]", "")) <= Integer.parseInt(splitCur.get(2).replaceAll("[^\\d.]", ""));
        }

        return true;
    }
}