package me.armar.plugins.autorank.pathbuilder.playerdata;

import io.reactivex.annotations.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerDataStorage {
    Collection<Integer> getCompletedRequirements(@NonNull UUID var1, @NonNull String var2);

    boolean hasCompletedRequirement(@NonNull UUID var1, @NonNull String var2, int var3);

    void addCompletedRequirement(@NonNull UUID var1, @NonNull String var2, int var3);

    void setCompletedRequirements(@NonNull UUID var1, @NonNull String var2, @NonNull Collection<Integer> var3);

    Collection<Integer> getCompletedRequirementsWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    void addCompletedRequirementWithMissingResults(@NonNull UUID var1, @NonNull String var2, int var3);

    void removeCompletedRequirementWithMissingResults(@NonNull UUID var1, @NonNull String var2, int var3);

    boolean hasCompletedRequirementWithMissingResults(@NonNull UUID var1, @NonNull String var2, int var3);

    Collection<Integer> getCompletedPrerequisites(@NonNull UUID var1, @NonNull String var2);

    boolean hasCompletedPrerequisite(@NonNull UUID var1, @NonNull String var2, int var3);

    void addCompletedPrerequisite(@NonNull UUID var1, @NonNull String var2, int var3);

    void setCompletedPrerequisites(@NonNull UUID var1, @NonNull String var2, @NonNull Collection<Integer> var3);

    Collection<String> getChosenPathsWithMissingResults(@NonNull UUID var1);

    void addChosenPathWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    void removeChosenPathWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    boolean hasChosenPathWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    Collection<String> getActivePaths(@NonNull UUID var1);

    boolean hasActivePath(@NonNull UUID var1, @NonNull String var2);

    void addActivePath(@NonNull UUID var1, @NonNull String var2);

    void removeActivePath(@NonNull UUID var1, @NonNull String var2);

    void setActivePaths(@NonNull UUID var1, @NonNull Collection<String> var2);

    Collection<String> getCompletedPaths(@NonNull UUID var1);

    boolean hasCompletedPath(@NonNull UUID var1, @NonNull String var2);

    void addCompletedPath(@NonNull UUID var1, @NonNull String var2);

    void removeCompletedPath(@NonNull UUID var1, @NonNull String var2);

    void setCompletedPaths(@NonNull UUID var1, @NonNull Collection<String> var2);

    int getTimesCompletedPath(@NonNull UUID var1, @NonNull String var2);

    Optional<Long> getTimeSinceCompletionOfPath(UUID var1, String var2);

    void resetProgressOfAllPaths(UUID var1);

    void resetProgressOfPath(UUID var1, String var2);

    Collection<String> getCompletedPathsWithMissingResults(@NonNull UUID var1);

    void addCompletedPathWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    void removeCompletedPathWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    boolean hasCompletedPathWithMissingResults(@NonNull UUID var1, @NonNull String var2);

    boolean hasLeaderboardExemption(@NonNull UUID var1);

    void setLeaderboardExemption(@NonNull UUID var1, boolean var2);

    boolean hasAutoCheckingExemption(@NonNull UUID var1);

    void setAutoCheckingExemption(@NonNull UUID var1, boolean var2);

    boolean hasTimeAdditionExemption(@NonNull UUID var1);

    void setTimeAdditionExemption(@NonNull UUID var1, boolean var2);

    PlayerDataManager.PlayerDataStorageType getDataStorageType();
}
