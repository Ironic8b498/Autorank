package me.armar.plugins.autorank.api.services;

import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;

import java.util.List;
import java.util.Optional;

public interface RequirementManager {
    boolean registerRequirement(String var1, Class<? extends AbstractRequirement> var2);

    boolean unRegisterRequirement(String var1);

    List<Class<? extends AbstractRequirement>> getRegisteredRequirements();

    Optional<Class<? extends AbstractRequirement>> getRequirement(String var1);
}
