package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.entity.Player;

public abstract class AbstractResult {
    private boolean isGlobal = false;
    private String customDescription;

    public AbstractResult() {
    }

    public abstract boolean applyResult(Player var1);

    public final Autorank getAutorank() {
        return Autorank.getInstance();
    }

    public abstract String getDescription();

    public abstract boolean setOptions(String[] var1);

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public boolean hasCustomDescription() {
        return this.getCustomDescription() != null;
    }

    public String getCustomDescription() {
        return this.customDescription;
    }

    public void setCustomDescription(String description) {
        this.customDescription = description;
    }

    public boolean isGlobal() {
        return this.isGlobal;
    }

    public void setGlobal(boolean global) {
        this.isGlobal = global;
    }
}
