package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class SpawnFireworkResult extends AbstractResult {
    private Color colour;
    private Location location;
    private int power;
    private String target;
    private Type type;

    public SpawnFireworkResult() {
        this.colour = Color.ORANGE;
        this.power = 1;
        this.target = "player";
        this.type = Type.BALL;
    }

    public boolean applyResult(Player player) {
        if (player == null) {
            return false;
        } else {
            Location loc = this.target.equals("player") ? player.getLocation() : player.getWorld().getSpawnLocation();
            this.getAutorank().getServer().getScheduler().runTask(this.getAutorank(), () -> {
                Firework fw = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                FireworkEffect effect = FireworkEffect.builder().withColor(this.colour).with(this.type).build();
                fwm.addEffect(effect);
                fwm.setPower(this.power);
                fw.setFireworkMeta(fwm);
                fw.detonate();
            });
            return this.location != null;
        }
    }

    public String getDescription() {
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        } else {
            String targetLocation = this.target.equals("player") ? "your location" : "at spawn";
            return Lang.SPAWN_FIREWORK_RESULT.getConfigValue(targetLocation);
        }
    }

    public boolean setOptions(String[] options) {
        if (options.length < 6) {
            return false;
        } else {
            this.target = options[0];
            this.power = Integer.parseInt(options[1]);
            this.type = Type.valueOf(options[2].toUpperCase().replace(" ", "_"));
            this.colour = Color.fromRGB(Integer.parseInt(options[3]), Integer.parseInt(options[4]), Integer.parseInt(options[5]));
            return this.target != null;
        }
    }
}
