package me.armar.plugins.autorank.pathbuilder.builders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class RequirementBuilder {
    private static final Map<String, Class<? extends AbstractRequirement>> reqs = new HashMap();
    private AbstractRequirement requirement = null;
    private boolean isValid = false;
    private String pathName;
    private String requirementType;
    private String originalPathString;
    private boolean isPreRequisite = false;

    public RequirementBuilder() {
    }

    public static void registerRequirement(String type, Class<? extends AbstractRequirement> requirement) {
        reqs.put(type, requirement);
        AutorankTools.registerRequirement(type);
    }

    public static boolean unRegisterRequirement(String type) {
        if (!reqs.containsKey(type)) {
            return false;
        } else {
            reqs.remove(type);
            return true;
        }
    }

    public static List<Class<? extends AbstractRequirement>> getRegisteredRequirements() {
        return new ArrayList(reqs.values());
    }

    public static Optional<Class<? extends AbstractRequirement>> getRegisteredRequirement(String type) {
        return Optional.ofNullable(reqs.getOrDefault(type, null));
    }

    public static AbstractRequirement createRequirement(String pathName, String requirementType, String[] options, boolean isPreRequisite) {
        RequirementBuilder builder = (new RequirementBuilder()).createEmpty(pathName, requirementType, isPreRequisite).populateRequirement(options);
        return !builder.isValid() ? null : builder.finish();
    }

    public RequirementBuilder createEmpty(String pathName, String requirementType, boolean isPreRequisite) {
        this.pathName = pathName;
        this.requirementType = requirementType;
        this.isPreRequisite = isPreRequisite;
        this.originalPathString = requirementType;
        String originalReqType = requirementType;
        requirementType = AutorankTools.findMatchingRequirementName(requirementType);
        if (requirementType == null) {
            Autorank.getInstance().getWarningManager().registerWarning(String.format("You are using a '%s' requirement in path '%s', but that requirement doesn't exist!", originalReqType, pathName), 10);
            return this;
        } else {
            Class<? extends AbstractRequirement> c = reqs.get(requirementType);
            if (c != null) {
                try {
                    this.requirement = c.newInstance();
                } catch (Exception var7) {
                    var7.printStackTrace();
                }

                return this;
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Requirement '" + originalReqType + "' is not a valid requirement type!");
                return null;
            }
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public RequirementBuilder populateRequirement(String[] options) {
        if (this.requirement == null) {
            return this;
        } else if (options == null) {
            return this;
        } else {
            String dependencyNotFoundMessage = "Requirement '%s' relies on a third-party plugin being installed, but that plugin is not installed!";

            String resultType;
            try {
                if (!this.requirement.initRequirement(options)) {
                    String primaryErrorMessage = "unknown error (check wiki)";
                    if (this.requirement.getErrorMessages().size() > 0) {
                        primaryErrorMessage = this.requirement.getErrorMessages().get(0);
                    }

                    String invalidRequirementMessage = "Could not set up requirement '%s' of %s! Autorank reported the following error: '%s'";
                    resultType = String.format(invalidRequirementMessage, this.originalPathString, this.pathName, primaryErrorMessage);
                    Autorank.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + resultType);
                    Autorank.getInstance().getWarningManager().registerWarning(resultType, 10);
                }
            } catch (NoClassDefFoundError var9) {
                Autorank.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + String.format(dependencyNotFoundMessage, this.requirementType));
                Autorank.getInstance().getWarningManager().registerWarning(String.format(dependencyNotFoundMessage, this.requirementType), 10);
                return this;
            }

            this.requirement.setOptional(Autorank.getInstance().getPathsConfig().isOptionalRequirement(this.pathName, this.requirementType, this.isPreRequisite));
            this.requirement.setPreRequisite(this.isPreRequisite);
            List<AbstractResult> abstractResultList = new ArrayList();
            Iterator var11 = Autorank.getInstance().getPathsConfig().getResultsOfRequirement(this.pathName, this.requirementType, this.isPreRequisite).iterator();

            while(var11.hasNext()) {
                resultType = (String)var11.next();
                AbstractResult abstractResult = ResultBuilder.createResult(this.pathName, resultType, Autorank.getInstance().getPathsConfig().getResultOfRequirement(this.pathName, this.requirementType, resultType, this.isPreRequisite));
                if (abstractResult != null) {
                    abstractResultList.add(abstractResult);
                }
            }

            this.requirement.setAbstractResults(abstractResultList);
            this.requirement.setAutoComplete(Autorank.getInstance().getPathsConfig().useAutoCompletion(this.pathName, this.requirementType, this.isPreRequisite));
            int requirementId = Autorank.getInstance().getPathsConfig().getRequirementId(this.pathName, this.requirementType, this.isPreRequisite);
            if (requirementId < 0) {
                throw new IllegalStateException("Requirement ID of a requirement could not be found. This means there is something wrong with your configuration. Path: " + this.pathName + ", Requirement: " + this.requirementType);
            } else {
                this.requirement.setId(requirementId);
                if (Autorank.getInstance().getPathsConfig().isRequirementWorldSpecific(this.pathName, this.requirementType, this.isPreRequisite)) {
                    this.requirement.setWorld(Autorank.getInstance().getPathsConfig().getWorldOfRequirement(this.pathName, this.requirementType, this.isPreRequisite));
                }

                if (Autorank.getInstance().getPathsConfig().hasCustomRequirementDescription(this.pathName, this.requirementType, this.isPreRequisite)) {
                    this.requirement.setCustomDescription(Autorank.getInstance().getPathsConfig().getCustomRequirementDescription(this.pathName, this.requirementType, this.isPreRequisite));
                }

                DependencyManager dependencyManager = Autorank.getInstance().getDependencyManager();
                List<Library> missingDependencies = new ArrayList();
                Iterator var7 = this.requirement.getDependencies().iterator();

                while(var7.hasNext()) {
                    Library dependency = (Library)var7.next();
                    if (!dependencyManager.isAvailable(dependency)) {
                        missingDependencies.add(dependency);
                    }
                }

                if (missingDependencies.size() > 0 && missingDependencies.size() == this.requirement.getDependencies().size()) {
                    Autorank.getInstance().getLogger().severe(String.format("Requirement '%s' relies on '%s' being installed, but that plugin is not installed!", this.requirementType, missingDependencies.get(0).getHumanPluginName()));
                    Autorank.getInstance().getWarningManager().registerWarning(String.format("Requirement '%s' relies on '%s' being installed, but that plugin is not installed!", this.requirementType, missingDependencies.get(0).getHumanPluginName()), 10);
                    return this;
                } else {
                    this.isValid = true;
                    return this;
                }
            }
        }
    }

    public AbstractRequirement finish() throws IllegalStateException {
        if (this.isValid && this.requirement != null) {
            return this.requirement;
        } else {
            throw new IllegalStateException("Requirement '" + this.requirementType + "' of '" + this.pathName + "' was not valid and could not be finished.");
        }
    }
}
