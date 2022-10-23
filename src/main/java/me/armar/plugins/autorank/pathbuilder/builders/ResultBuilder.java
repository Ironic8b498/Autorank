package me.armar.plugins.autorank.pathbuilder.builders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class ResultBuilder {
    private static final Map<String, Class<? extends AbstractResult>> results = new HashMap();
    private AbstractResult abstractResult = null;
    private boolean isValid = false;
    private String pathName;
    private String resultName;
    private String originalResultString;

    public ResultBuilder() {
    }

    public static void registerResult(String type, Class<? extends AbstractResult> result) {
        results.put(type, result);
        AutorankTools.registerResult(type);
    }

    public static boolean unRegisterResult(String type) {
        if (!results.containsKey(type)) {
            return false;
        } else {
            results.remove(type);
            return true;
        }
    }

    public static List<Class<? extends AbstractResult>> getRegisteredResults() {
        return new ArrayList(results.values());
    }

    public static Optional<Class<? extends AbstractResult>> getRegisteredResult(String type) {
        return Optional.ofNullable(results.getOrDefault(type, null));
    }

    public static AbstractResult createResult(String pathName, String resultType, String stringValue) {
        ResultBuilder builder = (new ResultBuilder()).createEmpty(pathName, resultType).populateResult(stringValue);
        return !builder.isValid() ? null : builder.finish();
    }

    public final Autorank getAutorank() {
        return Autorank.getInstance();
    }

    public ResultBuilder createEmpty(String pathName, String resultType) {
        this.pathName = pathName;
        this.originalResultString = resultType;
        resultType = AutorankTools.findMatchingResultName(resultType);
        this.resultName = resultType;
        if (resultType == null) {
            Autorank.getInstance().getWarningManager().registerWarning(String.format("You are using a '%s' result in path '%s', but that result doesn't exist!", this.originalResultString, pathName), 10);
            return this;
        } else {
            Class<? extends AbstractResult> c = results.get(resultType);
            if (c != null) {
                try {
                    this.abstractResult = c.newInstance();
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Result '" + this.originalResultString + "' is not a valid result type!");
            }

            return this;
        }
    }

    public ResultBuilder populateResult(String stringValue) {
        if (this.abstractResult == null) {
            return this;
        } else if (stringValue == null) {
            return this;
        } else {
            this.abstractResult.setOptions(stringValue.split(";"));
            if (this.getAutorank().getPathsConfig().hasCustomResultDescription(this.pathName, this.originalResultString)) {
                this.abstractResult.setCustomDescription(this.getAutorank().getPathsConfig().getCustomResultDescription(this.pathName, this.originalResultString));
            }

            this.abstractResult.setGlobal(this.getAutorank().getPathsConfig().isResultGlobal(this.pathName, this.originalResultString));
            this.isValid = true;
            return this;
        }
    }

    public AbstractResult finish() throws IllegalStateException {
        if (this.isValid && this.abstractResult != null) {
            return this.abstractResult;
        } else {
            throw new IllegalStateException("Result '" + this.originalResultString + "' of '" + this.pathName + "' was not valid and could not be finished.");
        }
    }

    public boolean isValid() {
        return this.isValid;
    }
}
