package me.armar.plugins.autorank.validations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.InGroupRequirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ValidateHandler {
    private final Autorank plugin;

    public ValidateHandler(Autorank instance) {
        this.plugin = instance;
    }

    public boolean startValidation() {
        boolean correctSetup;
        correctSetup = this.validatePermGroups() && this.validateSettingsConfig();
        return correctSetup;
    }

    public boolean validatePermGroups() {
        List<Path> paths = this.plugin.getPathManager().getAllPaths();
        List<String> permGroups = new ArrayList<>();
        Collection<String> vaultGroups = this.plugin.getPermPlugHandler().getPermissionPlugin().getGroups();

        for(Path path : paths) {
            List<CompositeRequirement> holders = new ArrayList<>();
            holders.addAll(path.getPrerequisites());
            holders.addAll(path.getRequirements());

            for(CompositeRequirement reqHolder : holders) {
                for(AbstractRequirement req : reqHolder.getRequirements()) {
                    if (req instanceof InGroupRequirement) {
                        String requirementName = this.plugin.getPathsConfig().getRequirementName(path.getInternalName(), req.getId(), reqHolder.isPrerequisite());
                        if (requirementName != null && requirementName.toLowerCase().contains("in group")) {
                            for(String[] option : this.plugin.getPathsConfig().getRequirementOptions(path.getInternalName(), requirementName, reqHolder.isPrerequisite())) {
                                if (option.length > 0) {
                                    permGroups.add(option[0]);
                                }
                            }
                        }
                    }
                }
            }
        }

        for(String group : permGroups) {
            boolean found = false;

            for(String vaultGroup : vaultGroups) {
                if (group.equalsIgnoreCase(vaultGroup)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                this.plugin.getWarningManager().registerWarning("You used the '" + group + "' group, but it was not recognized in your permission plugin!", 10);
                return false;
            }
        }

        return true;
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
