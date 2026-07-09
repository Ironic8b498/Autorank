package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LocationRequirement extends AbstractRequirement {
    private String world;
    private int xLocation = 0;
    private int yLocation = 0;
    private int zLocation = 0;
    int xRadiusP;
    int yRadiusP;
    int zRadiusP;
    int xRadiusN;
    int yRadiusN;
    int zRadiusN;

    public LocationRequirement() {
    }

    public String getDescription() {
        return Lang.LOCATION_REQUIREMENT.getConfigValue(this.xLocation + ", " + this.yLocation + ", " + this.zLocation + " in " + this.world);
    }

    public String getProgressString(Player player) {
        Location playerLoc = player.getLocation();
        int pX = playerLoc.getBlockX();
        int pY = playerLoc.getBlockY();
        int pZ = playerLoc.getBlockZ();
        String plurOrSing;
        int distance = (int)Math.sqrt(Math.pow(pX - this.xLocation, 2.0F) + Math.pow(pY - this.yLocation, 2.0F) + Math.pow(pZ - this.zLocation, 2.0F));
        if (distance > 1) {
            plurOrSing = "meters";
        } else {
            plurOrSing = "meter";
        }

        return distance + " " + plurOrSing + " away";
    }

    public boolean meetsRequirement(Player player) {
        Location pLocation = player.getLocation();
        World realWorld = Bukkit.getWorld(this.world);
        if (realWorld == null) {
            return false;
        } else if (!realWorld.getName().equals(Objects.requireNonNull(pLocation.getWorld()).getName())) {
            return false;
        } else if (pLocation.getBlockX() >= this.xRadiusN && pLocation.getBlockX() <= this.xRadiusP && pLocation.getBlockY() >= this.yRadiusN && pLocation.getBlockY() <= this.yRadiusP) {
            return pLocation.getBlockZ() >= this.zRadiusN && pLocation.getBlockZ() <= this.zRadiusP;
        } else {
            return false;
        }
    }

    public boolean initRequirement(String[] options) {
        if (options.length != 5) {
            return false;
        } else {
            this.xLocation = Integer.parseInt(options[0]);
            this.yLocation = Integer.parseInt(options[1]);
            this.zLocation = Integer.parseInt(options[2]);
            this.world = options[3].trim();
            int radius = Integer.parseInt(options[4]);
            if (radius < 0) {
                radius = 0;
            }

            this.xRadiusN = this.xLocation - radius;
            this.yRadiusN = this.yLocation - radius;
            this.zRadiusN = this.zLocation - radius;
            this.xRadiusP = this.xLocation + radius;
            this.yRadiusP = this.yLocation + radius;
            this.zRadiusP = this.zLocation + radius;
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
