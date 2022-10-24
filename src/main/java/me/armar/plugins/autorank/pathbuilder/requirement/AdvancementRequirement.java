package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdvancementRequirement extends AbstractRequirement {
    Advancement advancement = null;
    int advancementCount = -1;
    String advancementName = null;

    public AdvancementRequirement() {
    }

    public String getDescription() {
        String lang;
        if (this.advancementCount != -1) {
            lang = Lang.ADVANCEMENT_MULTIPLE_REQUIREMENT.getConfigValue(this.advancementCount);
        } else {
            lang = Lang.ADVANCEMENT_SINGLE_REQUIREMENT.getConfigValue(this.advancementName);
        }

        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    public String getProgressString(Player player) {
        if (this.advancementCount != -1) {
            int count = getCompletedAdvancements(player).size();
            return count + "/" + this.advancementCount;
        } else {
            return !player.getAdvancementProgress(this.advancement).isDone() ? "advancement not yet obtained." : "advancement obtained.";
        }
    }

    protected boolean meetsRequirement(Player player) {
        if (this.isWorldSpecific() && !this.getWorld().equals(player.getWorld().getName())) {
            return false;
        } else if (this.advancementCount != -1) {
            return getCompletedAdvancements(player).size() >= this.advancementCount;
        } else {
            return player.getAdvancementProgress(this.advancement).isDone();
        }
    }

    public static Advancement getAdvancement(String name) {
        Iterator it = Bukkit.getServer().advancementIterator();

        Advancement a;
        do {
            if (!it.hasNext()) {
                return null;
            }

            a = (Advancement)it.next();
        } while(!a.getKey().toString().equalsIgnoreCase(name));

        return a;
    }

    public static List<Advancement> getCompletedAdvancements(Player player) {
        List<Advancement> completedAdvancements = new ArrayList();
        Iterator it = Bukkit.getServer().advancementIterator();

        while(it.hasNext()) {
            Advancement a = (Advancement)it.next();
            if (player.getAdvancementProgress(a).isDone()) {
                completedAdvancements.add(a);
            }
        }

        return completedAdvancements;
    }

    public static boolean hasAdvancement(Player player, String name) {
        Advancement a = getAdvancement(name);
        if (a == null) {
            return false;
        } else {
            AdvancementProgress progress = player.getAdvancementProgress(a);
            return progress.isDone();
        }
    }

    public boolean initRequirement(String[] options) {
        String option = options[0].trim();
        if (NumberUtils.isNumber(option)) {
            this.advancementCount = (int) AutorankTools.stringToDouble(options[0]);
            if (this.advancementCount < 0) {
                this.registerWarningMessage("No number of advancements provided (or smaller than 0).");
                return false;
            }
        } else {
            this.advancement = getAdvancement(options[0].trim());
            if (this.advancement == null) {
                this.registerWarningMessage("No advancement found with that string.");
                return false;
            }

            if (options.length > 1) {
                this.advancementName = options[1].trim();
            }

            if (this.advancementName == null) {
                this.registerWarningMessage("No name for the advancement provided.");
                return false;
            }
        }

        return true;
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
