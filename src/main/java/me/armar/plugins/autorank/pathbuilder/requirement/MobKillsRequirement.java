package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class MobKillsRequirement extends AbstractRequirement {
    private String mobType = null;
    private int totalMobsKilled = -1;

    public MobKillsRequirement() {
    }

    public String getDescription() {
        String desc = "";
        if (this.mobType != null && !this.mobType.trim().equals("")) {
            desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT.getConfigValue(this.totalMobsKilled + " " + this.mobType.toLowerCase().replace("_", " ") + "(s)");
        } else {
            desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT.getConfigValue(this.totalMobsKilled + " mobs");
        }

        if (this.isWorldSpecific()) {
            desc = desc.concat(" (in world '" + this.getWorld() + "')");
        }

        return desc;
    }

    public String getProgressString(UUID uuid) {
        EntityType type = null;
        int killed = 0;

        try {
            type = EntityType.valueOf(this.mobType);
        } catch (Exception var5) {
            killed = this.getStatisticsManager().getMobsKilled(uuid, this.getWorld(), null);
        }

        if (type != null) {
            killed = this.getStatisticsManager().getMobsKilled(uuid, this.getWorld(), type);
        }

        String entityType = this.mobType;
        if (this.mobType == null) {
            entityType = "mobs";
        }

        return killed + "/" + this.totalMobsKilled + " " + entityType.replace("_", " ") + "(s)";
    }

    protected boolean meetsRequirement(UUID uuid) {
        EntityType type = null;
        int killed = 0;

        try {
            type = EntityType.valueOf(this.mobType);
        } catch (Exception var5) {
            killed = this.getStatisticsManager().getMobsKilled(uuid, this.getWorld(), null);
        }

        if (type != null) {
            killed = this.getStatisticsManager().getMobsKilled(uuid, this.getWorld(), type);
        }

        return killed >= this.totalMobsKilled;
    }

    public boolean initRequirement(String[] options) {
        this.totalMobsKilled = Integer.parseInt(options[0]);
        if (options.length > 1) {
            this.mobType = options[1].trim().replace(" ", "_");
            if (this.mobType.equalsIgnoreCase("charged_creeper")) {
                this.mobType = "POWERED CREEPER";
            } else if (this.mobType.equalsIgnoreCase("spider_jockey")) {
                this.mobType = "SPIDER JOCKEY";
            } else if (this.mobType.equalsIgnoreCase("chicken_jockey")) {
                this.mobType = "CHICKEN JOCKEY";
            } else if (this.mobType.equalsIgnoreCase("killer_rabbit")) {
                this.mobType = "KILLER RABBIT";
            } else {
                this.mobType = EntityType.valueOf(this.mobType.toUpperCase()).name();
            }
        }

        if (this.totalMobsKilled < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        EntityType type = null;
        int killed = 0;

        try {
            type = EntityType.valueOf(this.mobType);
        } catch (Exception var5) {
            killed = this.getStatisticsManager().getMobsKilled(uuid, this.getWorld(), null);
        }

        if (type != null) {
            killed = this.getStatisticsManager().getMobsKilled(uuid, this.getWorld(), type);
        }

        return (double)killed * 1.0D / (double)this.totalMobsKilled;
    }
}
