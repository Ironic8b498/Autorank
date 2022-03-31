package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportResult extends AbstractResult {
    private Location location;

    public TeleportResult() {
    }

    public boolean applyResult(Player player) {
        if (player == null) {
            return false;
        } else {
            player.teleport(this.location);
            return this.location != null;
        }
    }

    public String getDescription() {
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        } else {
            String locationString = this.location.getBlockX() + ", " + this.location.getBlockY() + ", " + this.location.getBlockZ() + " on " + this.location.getWorld().getName();
            return Lang.TELEPORT_RESULT.getConfigValue(locationString);
        }
    }

    public boolean setOptions(String[] options) {
        if (options.length < 4) {
            return false;
        } else {
            if (options.length == 6) {
                this.location = new Location(Bukkit.getServer().getWorld(options[3]), Integer.parseInt(options[0]), Integer.parseInt(options[1]), Integer.parseInt(options[2]), Float.parseFloat(options[4]), Float.parseFloat(options[5]));
            } else {
                this.location = new Location(Bukkit.getServer().getWorld(options[3]), Integer.parseInt(options[0]), Integer.parseInt(options[1]), Integer.parseInt(options[2]));
            }

            return this.location != null;
        }
    }
}
