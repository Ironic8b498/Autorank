package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptRequirement extends AbstractRequirement {
    String code = null;
    private String description = null;
    private ScriptEngine engine = null;

    public JavaScriptRequirement() {
    }

    public String getDescription() {
        return this.description;
    }

    public String getProgressString(Player player) {
        return "unknown";
    }

    public boolean meetsRequirement(Player player) {
        this.engine.put("Player", player);
        Object result = null;

        try {
            result = this.engine.eval(this.code);
            if (!(result instanceof Boolean)) {
                this.getAutorank().getLogger().warning("The expression '" + this.code + "' is not a valid expression!");
                return false;
            }
        } catch (ScriptException var4) {
            var4.printStackTrace();
            return false;
        }

        return (Boolean)result;
    }

    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            this.code = options[0];
        }

        if (options.length > 1) {
            this.description = options[1];
        }

        this.engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
        this.engine.put("Server", Bukkit.getServer());
        if (this.code == null) {
            this.registerWarningMessage("No expression provided");
            return false;
        } else if (this.engine == null) {
            this.registerWarningMessage("Could not obtain Javascript Engine");
            return false;
        } else {
            return true;
        }
    }

    public boolean needsOnlinePlayer() {
        return true;
    }
}
