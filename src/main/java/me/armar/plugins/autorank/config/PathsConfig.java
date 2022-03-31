package me.armar.plugins.autorank.config;

import com.google.common.collect.Lists;
import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class PathsConfig extends AbstractConfig {
    public PathsConfig(Autorank instance) {
        this.setPlugin(instance);
        this.setFileName("Paths.yml");
    }

    public void reloadConfig() {
        this.loadConfig();
    }

    public boolean isPathRepeatable(String pathName) {
        boolean allowInfinitePathing = this.getConfig().getBoolean(pathName + ".options.infinite pathing", false);
        boolean defaultValue = this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.ALLOW_INFINITE_PATHING);
        boolean isRepeatable = this.getConfig().getBoolean(pathName + ".options.is repeatable", false);
        return allowInfinitePathing || isRepeatable || defaultValue;
    }

    public String getDisplayName(String pathName) {
        return this.getConfig().getString(pathName + ".options.display name", pathName);
    }

    public Optional<String> getCooldownOfPath(String pathName) {
        String cooldown = this.getConfig().getString(pathName + ".options.cooldown", null);
        return cooldown == null ? Optional.empty() : Optional.of(cooldown);
    }

    public List<String> getPaths() {
        return new ArrayList(this.getConfig().getKeys(false));
    }

    public int getRequirementId(String pathName, String reqName, boolean isPreRequisite) {
        Object[] reqs = this.getRequirements(pathName, isPreRequisite).toArray();

        for(int i = 0; i < reqs.length; ++i) {
            String reqString = (String)reqs[i];
            if (reqName.equalsIgnoreCase(reqString)) {
                return i;
            }
        }

        return -1;
    }

    public String getRequirementName(String pathName, int reqId, boolean isPreRequisite) {
        List<String> reqs = this.getRequirements(pathName, isPreRequisite);
        return reqId >= 0 && reqId < reqs.size() ? reqs.get(reqId) : null;
    }

    public List<String[]> getRequirementOptions(String pathName, String reqName, boolean isPreRequisite) {
        String org = this.getRequirementValue(pathName, reqName, isPreRequisite);
        List<String[]> list = new ArrayList();
        String[] split = org.split(",");
        String[] var7 = split;
        int var8 = split.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String sp = var7[var9];
            StringBuilder builder = new StringBuilder(sp);
            if (builder.charAt(0) == '(') {
                builder.deleteCharAt(0);
            }

            if (builder.charAt(builder.length() - 1) == ')') {
                builder.deleteCharAt(builder.length() - 1);
            }

            String[] splitArray = builder.toString().trim().split(";");
            list.add(splitArray);
        }

        return list;
    }

    public List<String> getRequirements(String pathName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        ConfigurationSection section = this.getConfig().getConfigurationSection(pathName + "." + keyType);
        return section == null ? new ArrayList() : new ArrayList(section.getKeys(false));
    }

    private String getRequirementValue(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        return this.getConfig().get(pathName + "." + keyType + "." + reqName + ".value") != null ? this.getConfig().get(pathName + "." + keyType + "." + reqName + ".value").toString() : this.getConfig().getString(pathName + "." + keyType + "." + reqName);
    }

    public String getResultOfPath(String pathName, String resultName) {
        String result = this.getConfig().get(pathName + ".results." + resultName + ".value") != null ? this.getConfig().get(pathName + ".results." + resultName + ".value").toString() : this.getConfig().getString(pathName + ".results." + resultName);
        return result;
    }

    public String getResultOfRequirement(String pathName, String reqName, String resName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        return this.getConfig().get(pathName + "." + keyType + "." + reqName + ".results." + resName + ".value") != null ? this.getConfig().get(pathName + "." + keyType + "." + reqName + ".results." + resName + ".value").toString() : this.getConfig().getString(pathName + "." + keyType + "." + reqName + ".results." + resName);
    }

    public List<String> getResults(String pathName) {
        ConfigurationSection section = this.getConfig().getConfigurationSection(pathName + ".results");
        return section != null ? new ArrayList(section.getKeys(false)) : new ArrayList();
    }

    public List<String> getResultsOfRequirement(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        Set<String> results = this.getConfig().getConfigurationSection(pathName + "." + keyType + "." + reqName + ".results") != null ? this.getConfig().getConfigurationSection(pathName + "." + keyType + "." + reqName + ".results").getKeys(false) : new HashSet();
        return Lists.newArrayList((Iterable)results);
    }

    public String getWorldOfRequirement(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        return this.getConfig().getString(pathName + "." + keyType + "." + reqName + ".options.world", null);
    }

    public boolean isOptionalRequirement(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        return this.getConfig().getBoolean(pathName + "." + keyType + "." + reqName + ".options.optional", this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.IS_OPTIONAL_REQUIREMENT));
    }

    public boolean isRequirementWorldSpecific(String pathName, String reqName, boolean isPreRequisite) {
        return this.getWorldOfRequirement(pathName, reqName, isPreRequisite) != null;
    }

    public boolean useAutoCompletion(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        return this.getConfig().get(pathName + "." + keyType + "." + reqName + ".options.auto complete") == null ? this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.AUTO_COMPLETE_REQUIREMENT) : this.getConfig().getBoolean(pathName + "." + keyType + "." + reqName + ".options.auto complete");
    }

    public ArrayList<String> getResultsUponChoosing(String pathName) {
        Set<String> results = this.getConfig().getConfigurationSection(pathName + ".upon choosing") != null ? this.getConfig().getConfigurationSection(pathName + ".upon choosing").getKeys(false) : new HashSet();
        return Lists.newArrayList((Iterable)results);
    }

    public String getResultValueUponChoosing(String pathName, String resName) {
        String result = this.getConfig().get(pathName + ".upon choosing." + resName + ".value") != null ? this.getConfig().get(pathName + ".upon choosing." + resName + ".value").toString() : this.getConfig().getString(pathName + ".upon choosing." + resName);
        return result;
    }

    public boolean shouldAutoAssignPath(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.auto choose", this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.AUTO_CHOOSE_PATH));
    }

    public int getPriorityOfPath(String pathName) {
        return this.getConfig().getInt(pathName + ".options.priority", this.getPlugin().getDefaultBehaviorConfig().getDefaultIntegerBehaviorOfOption(DefaultBehaviorOption.PRIORITY_PATH));
    }

    public boolean showBasedOnPrerequisites(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.show based on prerequisites", this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.SHOW_PATH_BASED_ON_PREREQUISITES));
    }

    public boolean isPartialCompletionAllowed(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.allow partial completion", this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.ALLOW_PARTIAL_COMPLETION));
    }

    public String getPathDescription(String pathName) {
        return this.getConfig().getString(pathName + ".options.description", "");
    }

    public boolean hasCustomRequirementDescription(String pathName, String reqName, boolean isPreRequisite) {
        return this.getCustomRequirementDescription(pathName, reqName, isPreRequisite) != null;
    }

    public String getCustomRequirementDescription(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = isPreRequisite ? "prerequisites" : "requirements";
        return this.getConfig().getString(pathName + "." + keyType + "." + reqName + ".options.description", null);
    }

    public boolean hasCustomResultDescription(String pathName, String resName) {
        return this.getCustomResultDescription(pathName, resName) != null;
    }

    public String getCustomResultDescription(String pathName, String resName) {
        return this.getConfig().getString(pathName + ".results." + resName + ".options.description", null);
    }

    public boolean shouldStoreProgressOnDeactivation(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.store progress on deactivation", this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption.STORE_PROGRESS_ON_DEACTIVATION));
    }

    public boolean isResultGlobal(String pathName, String resultName) {
        return this.getConfig().getBoolean(pathName + ".results." + resultName + ".options.global", false);
    }
}
