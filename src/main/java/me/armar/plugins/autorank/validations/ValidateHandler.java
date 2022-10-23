package me.armar.plugins.autorank.validations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.InGroupRequirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ValidateHandler {
    private final Autorank plugin;

    public ValidateHandler(Autorank instance) {
        this.plugin = instance;
    }

    public boolean startValidation() {
        boolean correctSetup = false;
        correctSetup = this.validatePermGroups() && this.validateSettingsConfig();
        return correctSetup;
    }

    public boolean validatePermGroups() {
        List<Path> paths = this.plugin.getPathManager().getAllPaths();
        List<String> permGroups = new ArrayList();
        Collection<String> vaultGroups = this.plugin.getPermPlugHandler().getPermissionPlugin().getGroups();
        Iterator var4 = paths.iterator();

        Iterator var7;
        while(var4.hasNext()) {
            Path path = (Path)var4.next();
            List<CompositeRequirement> holders = new ArrayList();
            holders.addAll(path.getPrerequisites());
            holders.addAll(path.getRequirements());
            var7 = holders.iterator();

            label73:
            while(var7.hasNext()) {
                CompositeRequirement reqHolder = (CompositeRequirement)var7.next();
                Iterator var9 = reqHolder.getRequirements().iterator();

                while(true) {
                    String requirementName;
                    do {
                        do {
                            AbstractRequirement req;
                            do {
                                if (!var9.hasNext()) {
                                    continue label73;
                                }

                                req = (AbstractRequirement)var9.next();
                            } while(!(req instanceof InGroupRequirement));

                            requirementName = this.plugin.getPathsConfig().getRequirementName(path.getInternalName(), req.getId(), reqHolder.isPrerequisite());
                        } while(requirementName == null);
                    } while(!requirementName.toLowerCase().contains("in group"));

                    List<String[]> options = this.plugin.getPathsConfig().getRequirementOptions(path.getInternalName(), requirementName, reqHolder.isPrerequisite());
                    Iterator var13 = options.iterator();

                    while(var13.hasNext()) {
                        String[] option = (String[])var13.next();
                        if (option.length > 0) {
                            permGroups.add(option[0]);
                        }
                    }
                }
            }
        }

        var4 = permGroups.iterator();

        String group;
        boolean found;
        do {
            if (!var4.hasNext()) {
                return true;
            }

            group = (String)var4.next();
            found = false;
            var7 = vaultGroups.iterator();

            while(var7.hasNext()) {
                String vaultGroup = (String)var7.next();
                if (group.equalsIgnoreCase(vaultGroup)) {
                    found = true;
                    break;
                }
            }
        } while(found);

        this.plugin.getWarningManager().registerWarning("You used the '" + group + "' group, but it was not recognized in your permission plugin!", 10);
        return false;
    }

    public boolean validateSettingsConfig() {
        SettingsConfig config = this.plugin.getSettingsConfig();
        if (config != null && config.getConfig() != null) {
            if (config.getConfig().get("use time of") != null) {
                this.plugin.getWarningManager().registerWarning("You are using the 'use time of' setting in the Settings.yml but it doesn't work anymore. Please remove it!");
                return false;
            } else if (config.getIntervalTime() < 1) {
                this.plugin.getWarningManager().registerWarning("The time between time checks is less than 1, which is illegal!", 10);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
