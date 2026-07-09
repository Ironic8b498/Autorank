package me.armar.plugins.autorank.pathbuilder.builders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PathBuilder {
    private final Autorank plugin;

    public PathBuilder(Autorank plugin) {
        this.plugin = plugin;
    }

    public List<Path> initialisePaths() {
        List<Path> paths = new ArrayList<>();
        if (this.plugin.getPathsConfig().isLoaded()) {
            for(String pathName : this.plugin.getPathsConfig().getPaths()) {
                Path path = new Path(this.plugin);
                path.setInternalName(pathName);

                for(AbstractResult result : this.getResults(pathName)) {
                    path.addResult(result);
                }

                for(CompositeRequirement requirement : this.getPrerequisites(pathName)) {
                    path.addPrerequisite(requirement);
                }

                for(CompositeRequirement requirement : this.getRequirements(pathName)) {
                    path.addRequirement(requirement);
                }

                for(Object result : this.getResultsUponChoosing(pathName)) {
                    path.addResultUponChoosing((AbstractResult) result);
                }

                path.setDisplayName(this.plugin.getPathsConfig().getDisplayName(pathName));
                path.setDescription(this.plugin.getPathsConfig().getPathDescription(pathName));
                path.setRepeatable(this.plugin.getPathsConfig().isPathRepeatable(pathName));
                path.setAutomaticallyAssigned(this.plugin.getPathsConfig().shouldAutoAssignPath(pathName));
                path.setAllowPartialCompletion(this.plugin.getPathsConfig().isPartialCompletionAllowed(pathName));
                path.setOnlyShowIfPrerequisitesMet(this.plugin.getPathsConfig().showBasedOnPrerequisites(pathName));
                path.setStoreProgressOnDeactivation(this.plugin.getPathsConfig().shouldStoreProgressOnDeactivation(pathName));
                this.plugin.getPathsConfig().getCooldownOfPath(pathName).ifPresent((cooldown) -> path.setCooldown(AutorankTools.stringToTime(cooldown, TimeUnit.MINUTES)));
                paths.add(path);
            }
        }

        return paths;
    }

    private List<CompositeRequirement> getPrerequisites(String pathName) {
        List<CompositeRequirement> prerequisites = new ArrayList<>();

        for(String preReqName : this.plugin.getPathsConfig().getRequirements(pathName, true)) {
            CompositeRequirement reqHolder = new CompositeRequirement(this.plugin);

            for(String[] options : this.plugin.getPathsConfig().getRequirementOptions(pathName, preReqName, true)) {
                AbstractRequirement requirement = RequirementBuilder.createRequirement(pathName, preReqName, options, true);
                if (requirement != null) {
                    reqHolder.addRequirement(requirement);
                }
            }

            if (!reqHolder.getRequirements().isEmpty()) {
                prerequisites.add(reqHolder);
            }
        }

        return prerequisites;
    }

    private List<CompositeRequirement> getRequirements(String pathName) {
        List<CompositeRequirement> requirements = new ArrayList<>();

        for(String reqName : this.plugin.getPathsConfig().getRequirements(pathName, false)) {
            CompositeRequirement reqHolder = new CompositeRequirement(this.plugin);

            for(String[] options : this.plugin.getPathsConfig().getRequirementOptions(pathName, reqName, false)) {
                AbstractRequirement requirement = RequirementBuilder.createRequirement(pathName, reqName, options, false);
                if (requirement != null) {
                    reqHolder.addRequirement(requirement);
                }
            }

            if (!reqHolder.getRequirements().isEmpty()) {
                requirements.add(reqHolder);
            }
        }

        return requirements;
    }

    private List<AbstractResult> getResults(String pathName) {
        List<AbstractResult> results = new ArrayList<>();

        for(Object o : this.plugin.getPathsConfig().getResults(pathName)) {
            String resultName = (String)o;
            AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultName, this.plugin.getPathsConfig().getResultOfPath(pathName, resultName));
            if (abstractResult != null) {
                results.add(abstractResult);
            }
        }

        return results;
    }

    private ArrayList getResultsUponChoosing(String pathName) {
        ArrayList<Object> resultsUponChoosing = new ArrayList<>();

        for(Object result : this.plugin.getPathsConfig().getResultsUponChoosing(pathName)) {
            String resultType = (String)result;
            AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultType, this.plugin.getPathsConfig().getResultValueUponChoosing(pathName, resultType));
            if (abstractResult != null) {
                resultsUponChoosing.add(abstractResult);
            }
        }

        return resultsUponChoosing;
    }
}
