package me.armar.plugins.autorank.api.services;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RequirementService implements RequirementManager {
    private final Autorank plugin;

    public RequirementService(Autorank instance) {
        this.plugin = instance;
    }

    public boolean registerRequirement(String identifier, Class<? extends AbstractRequirement> requirementClass) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(requirementClass);
        RequirementBuilder.registerRequirement(identifier, requirementClass);
        return true;
    }

    public boolean unRegisterRequirement(String identifier) {
        Objects.requireNonNull(identifier);
        return RequirementBuilder.unRegisterRequirement(identifier);
    }

    public List<Class<? extends AbstractRequirement>> getRegisteredRequirements() {
        return RequirementBuilder.getRegisteredRequirements();
    }

    public Optional<Class<? extends AbstractRequirement>> getRequirement(String name) {
        return RequirementBuilder.getRegisteredRequirement(name);
    }
}
