package me.armar.plugins.autorank.pathbuilder.builders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.util.AutorankTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PathBuilder {
    private final Autorank plugin;

    public PathBuilder(Autorank plugin) {
        this.plugin = plugin;
    }

    public List<Path> initialisePaths() {
        List<Path> paths = new ArrayList();
        if (!this.plugin.getPathsConfig().isLoaded()) {
            return paths;
        } else {
            Iterator var2 = this.plugin.getPathsConfig().getPaths().iterator();

            while(var2.hasNext()) {
                String pathName = (String)var2.next();
                Path path = new Path(this.plugin);
                path.setInternalName(pathName);
                Iterator var5 = this.getResults(pathName).iterator();

                AbstractResult result;
                while(var5.hasNext()) {
                    result = (AbstractResult)var5.next();
                    path.addResult(result);
                }

                var5 = this.getPrerequisites(pathName).iterator();

                CompositeRequirement requirement;
                while(var5.hasNext()) {
                    requirement = (CompositeRequirement)var5.next();
                    path.addPrerequisite(requirement);
                }

                var5 = this.getRequirements(pathName).iterator();

                while(var5.hasNext()) {
                    requirement = (CompositeRequirement)var5.next();
                    path.addRequirement(requirement);
                }

                var5 = this.getResultsUponChoosing(pathName).iterator();

                while(var5.hasNext()) {
                    result = (AbstractResult)var5.next();
                    path.addResultUponChoosing(result);
                }

                path.setDisplayName(this.plugin.getPathsConfig().getDisplayName(pathName));
                path.setDescription(this.plugin.getPathsConfig().getPathDescription(pathName));
                path.setRepeatable(this.plugin.getPathsConfig().isPathRepeatable(pathName));
                path.setAutomaticallyAssigned(this.plugin.getPathsConfig().shouldAutoAssignPath(pathName));
                path.setAllowPartialCompletion(this.plugin.getPathsConfig().isPartialCompletionAllowed(pathName));
                path.setOnlyShowIfPrerequisitesMet(this.plugin.getPathsConfig().showBasedOnPrerequisites(pathName));
                path.setStoreProgressOnDeactivation(this.plugin.getPathsConfig().shouldStoreProgressOnDeactivation(pathName));
                this.plugin.getPathsConfig().getCooldownOfPath(pathName).ifPresent((cooldown) -> {
                    path.setCooldown(AutorankTools.stringToTime(cooldown, TimeUnit.MINUTES));
                });
                paths.add(path);
            }

            return paths;
        }
    }

    private List<CompositeRequirement> getPrerequisites(String pathName) {
        List<CompositeRequirement> prerequisites = new ArrayList();
        Iterator var3 = this.plugin.getPathsConfig().getRequirements(pathName, true).iterator();

        while(var3.hasNext()) {
            String preReqName = (String)var3.next();
            CompositeRequirement reqHolder = new CompositeRequirement(this.plugin);
            List<String[]> optionsList = this.plugin.getPathsConfig().getRequirementOptions(pathName, preReqName, true);
            Iterator var7 = optionsList.iterator();

            while(var7.hasNext()) {
                String[] options = (String[])var7.next();
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
        List<CompositeRequirement> requirements = new ArrayList();
        Iterator var3 = this.plugin.getPathsConfig().getRequirements(pathName, false).iterator();

        while(var3.hasNext()) {
            String reqName = (String)var3.next();
            CompositeRequirement reqHolder = new CompositeRequirement(this.plugin);
            List<String[]> optionsList = this.plugin.getPathsConfig().getRequirementOptions(pathName, reqName, false);
            Iterator var7 = optionsList.iterator();

            while(var7.hasNext()) {
                String[] options = (String[])var7.next();
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
        List<AbstractResult> results = new ArrayList();
        Iterator var3 = this.plugin.getPathsConfig().getResults(pathName).iterator();

        while(var3.hasNext()) {
            String resultName = (String)var3.next();
            AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultName, this.plugin.getPathsConfig().getResultOfPath(pathName, resultName));
            if (abstractResult != null) {
                results.add(abstractResult);
            }
        }

        return results;
    }

    private List<AbstractResult> getResultsUponChoosing(String pathName) {
        List<AbstractResult> resultsUponChoosing = new ArrayList();
        List<String> results = this.plugin.getPathsConfig().getResultsUponChoosing(pathName);
        Iterator var4 = results.iterator();

        while(var4.hasNext()) {
            String resultType = (String)var4.next();
            AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultType, this.plugin.getPathsConfig().getResultValueUponChoosing(pathName, resultType));
            if (abstractResult != null) {
                resultsUponChoosing.add(abstractResult);
            }
        }

        return resultsUponChoosing;
    }
}
