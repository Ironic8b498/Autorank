package me.armar.plugins.autorank.pathbuilder.playerdata;

import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;

import java.util.*;

public class PlayerData {
    private final UUID uuid = null;
    private final Collection<Path> completedPaths = new ArrayList();
    private final Collection<Path> activePaths = new ArrayList();
    private final Map<Path, Collection<CompositeRequirement>> completedRequirements = new HashMap();
    private final Map<Path, Collection<CompositeRequirement>> completedRequirementsWithoutResults = new HashMap();
    private final Map<Path, Collection<CompositeRequirement>> completedPrerequisites = new HashMap();
    private final Collection<Path> chosenPathsWithoutResults = new ArrayList();
    private final Collection<Path> completedPathsWithoutResults = new ArrayList();
    private final boolean isExemptedFromLeaderboard = false;
    private final boolean isAutoCheckingDisabled = false;
    private final boolean isExemptedFromTimeAddition = false;

    public PlayerData() {
    }
}
