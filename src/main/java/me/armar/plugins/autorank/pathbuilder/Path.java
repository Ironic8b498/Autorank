package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Path {
    private final Autorank plugin;
    private String displayName = "";
    private String internalName = "";
    private String description = "";
    private boolean isRepeatable = false;
    private boolean isAutomaticallyAssigned = false;
    private final List<CompositeRequirement> prerequisites = new ArrayList();
    private List<CompositeRequirement> requirements = new ArrayList();
    private List<AbstractResult> results = new ArrayList();
    private List<AbstractResult> resultsUponChoosing = new ArrayList();
    private boolean allowPartialCompletion = true;
    private boolean onlyShowIfPrerequisitesMet = false;
    private boolean storeProgressOnDeactivation = false;
    private long cooldown = 0L;

    public Path(Autorank plugin) {
        this.plugin = plugin;
    }

    public void addPrerequisite(CompositeRequirement prerequisite) throws IllegalArgumentException, NullPointerException {
        if (prerequisite == null) {
            throw new NullPointerException("CompositeRequirement is null");
        } else if (!prerequisite.isPrerequisite()) {
            throw new IllegalArgumentException("CompositeRequirement is not a prerequisite.");
        } else {
            this.prerequisites.add(prerequisite);
        }
    }

    public void addRequirement(CompositeRequirement requirement) throws NullPointerException {
        if (requirement == null) {
            throw new NullPointerException("CompositeRequirement is null");
        } else {
            this.requirements.add(requirement);
        }
    }

    public void addResult(AbstractResult result) throws NullPointerException {
        if (result == null) {
            throw new NullPointerException("Given result is null");
        } else {
            this.results.add(result);
        }
    }

    public void addResultUponChoosing(AbstractResult result) throws NullPointerException {
        if (result == null) {
            throw new NullPointerException("Given result is null");
        } else {
            this.resultsUponChoosing.add(result);
        }
    }

    public boolean checkPathProgress(UUID uuid) {
        this.plugin.debugMessage("Checking progress of '" + uuid + "' on path " + this.getDisplayName());
        if (!this.meetsAllRequirements(uuid)) {
            this.plugin.debugMessage("User '" + uuid + "' does not meet all requirements of path " + this.getDisplayName());
            return false;
        } else {
            this.plugin.debugMessage("User '" + uuid + "' meets all requirements of path " + this.getDisplayName());
            this.plugin.getPathManager().completePath(this, uuid);
            return true;
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<CompositeRequirement> getFailedRequirements(UUID uuid, boolean checkProgress) {
        List<CompositeRequirement> failedRequirements = new ArrayList();
        Iterator var4 = this.getRequirements().iterator();

        while(true) {
            CompositeRequirement holder;
            do {
                do {
                    if (!var4.hasNext()) {
                        return failedRequirements;
                    }

                    holder = (CompositeRequirement)var4.next();
                } while(holder.meetsRequirement(uuid));
            } while(checkProgress && this.hasCompletedRequirement(uuid, holder.getRequirementId()));

            failedRequirements.add(holder);
        }
    }

    public List<CompositeRequirement> getCompletedRequirements(UUID uuid) {
        List<CompositeRequirement> completedRequirements = new ArrayList();
        Iterator var3 = this.getRequirements().iterator();

        while(var3.hasNext()) {
            CompositeRequirement requirement = (CompositeRequirement)var3.next();
            if (this.plugin.getPathManager().hasCompletedRequirement(uuid, this, requirement.getRequirementId())) {
                completedRequirements.add(requirement);
            }
        }

        return completedRequirements;
    }

    public List<CompositeRequirement> getPrerequisites() {
        return this.prerequisites;
    }

    public List<CompositeRequirement> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(List<CompositeRequirement> requirements) {
        this.requirements = requirements;
    }

    public List<AbstractResult> getResults() {
        return this.results;
    }

    public void setResults(List<AbstractResult> results) {
        this.results = results;
    }

    public boolean meetsAllRequirements(UUID uuid) {
        if (!this.isActive(uuid)) {
            return false;
        } else {
            boolean meetAllRequirements = true;
            Iterator var3;
            CompositeRequirement holder;
            if (!this.allowPartialCompletion()) {
                this.plugin.debugMessage("User '" + uuid + "' cannot use partial completion on path '" + this.getDisplayName() + "'");
                var3 = this.getRequirements().iterator();

                do {
                    if (!var3.hasNext()) {
                        return true;
                    }

                    holder = (CompositeRequirement)var3.next();
                } while(holder.meetsRequirement(uuid));

                this.plugin.debugMessage("User '" + uuid + "' does not meet requirement '" + holder.getDescription() + "'");
                return false;
            } else {
                this.plugin.debugMessage("User '" + uuid + "' will use partial completion on path '" + this.getDisplayName() + "'");
                var3 = this.getRequirements().iterator();

                while(var3.hasNext()) {
                    holder = (CompositeRequirement)var3.next();
                    if (holder == null) {
                        return false;
                    }

                    if (this.hasCompletedRequirement(uuid, holder.getRequirementId())) {
                        this.plugin.debugMessage("User '" + uuid + "' has already completed '" + holder.getDescription() + "'");
                    } else if (holder.meetsRequirement(uuid)) {
                        if (holder.isOptional()) {
                            this.plugin.debugMessage("User '" + uuid + "' meets requirement '" + holder.getDescription() + "', but it is optional.");
                        } else {
                            this.plugin.debugMessage("User '" + uuid + "' meets requirement '" + holder.getDescription() + "' and the results of the requirement are now applied.");
                            this.completeRequirement(uuid, holder.getRequirementId());
                        }
                    } else {
                        this.plugin.debugMessage("User '" + uuid + "' does not meet requirement '" + holder.getDescription() + "'");
                        meetAllRequirements = false;
                    }
                }

                return meetAllRequirements;
            }
        }
    }

    public void completeRequirement(UUID uuid, int reqId) {
        CompositeRequirement requirement = this.getRequirement(reqId);
        if (requirement != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            Player player = offlinePlayer.getPlayer();
            if (player == null) {
                this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent((s) -> {
                    s.addCompletedRequirementWithMissingResults(uuid, this.getInternalName(), reqId);
                });
            } else {
                player.sendMessage(ChatColor.GREEN + Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT.getConfigValue(new Object[]{reqId + ""}));
                player.sendMessage(ChatColor.AQUA + requirement.getDescription());
            }

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                RequirementCompleteEvent event = new RequirementCompleteEvent(uuid, requirement);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (player != null) {
                        requirement.runResults(player);
                    }

                    this.plugin.getPathManager().addCompletedRequirement(uuid, this, reqId);
                }
            });
        }
    }

    public boolean meetsPrerequisites(UUID uuid) {
        List<CompositeRequirement> preRequisites = this.getPrerequisites();
        Iterator var3 = preRequisites.iterator();

        CompositeRequirement preRequisite;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            preRequisite = (CompositeRequirement)var3.next();
        } while(preRequisite.meetsRequirement(uuid));

        return false;
    }

    public boolean performResultsUponChoosing(Player player) {
        boolean success = true;
        Iterator var3 = this.getResultsUponChoosing().iterator();

        while(true) {
            AbstractResult r;
            boolean hasCompletedPathGlobally;
            do {
                if (!var3.hasNext()) {
                    return success;
                }

                r = (AbstractResult)var3.next();
                if (!r.isGlobal()) {
                    break;
                }

                hasCompletedPathGlobally = this.plugin.getPlayerDataManager().getDataStorage(PlayerDataManager.PlayerDataStorageType.GLOBAL).map((s) -> {
                    return s.hasCompletedPath(player.getUniqueId(), this.getInternalName());
                }).orElse(false);
            } while(hasCompletedPathGlobally);

            if (!r.applyResult(player)) {
                success = false;
            }
        }
    }

    public boolean performResults(Player player) {
        boolean success = true;
        Iterator var3 = this.getResults().iterator();

        while(true) {
            AbstractResult r;
            boolean hasCompletedPathGlobally;
            do {
                if (!var3.hasNext()) {
                    return success;
                }

                r = (AbstractResult)var3.next();
                if (!r.isGlobal()) {
                    break;
                }

                hasCompletedPathGlobally = this.plugin.getPlayerDataManager().getDataStorage(PlayerDataManager.PlayerDataStorageType.GLOBAL).map((s) -> {
                    return s.hasCompletedPath(player.getUniqueId(), this.getInternalName());
                }).orElse(false);
            } while(hasCompletedPathGlobally);

            if (!r.applyResult(player)) {
                success = false;
            }
        }
    }

    public String toString() {
        return this.displayName;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public List<AbstractResult> getResultsUponChoosing() {
        return this.resultsUponChoosing;
    }

    public void setResultsUponChoosing(List<AbstractResult> resultsUponChoosing) {
        this.resultsUponChoosing = resultsUponChoosing;
    }

    public boolean allowPartialCompletion() {
        return this.allowPartialCompletion;
    }

    public void setAllowPartialCompletion(boolean allowPartialCompletion) {
        this.allowPartialCompletion = allowPartialCompletion;
    }

    public boolean isRepeatable() {
        return this.isRepeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.isRepeatable = repeatable;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive(UUID uuid) {
        return this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((storage) -> {
            return storage.hasActivePath(uuid, this.getInternalName());
        }).orElse(false);
    }

    public boolean hasCompletedRequirement(UUID uuid, int reqId) {
        return this.plugin.getPathManager().hasCompletedRequirement(uuid, this, reqId);
    }

    public boolean isAutomaticallyAssigned() {
        return this.isAutomaticallyAssigned;
    }

    public void setAutomaticallyAssigned(boolean automaticallyAssigned) {
        this.isAutomaticallyAssigned = automaticallyAssigned;
    }

    public boolean onlyShowIfPrerequisitesMet() {
        return this.onlyShowIfPrerequisitesMet;
    }

    public void setOnlyShowIfPrerequisitesMet(boolean onlyShowIfPrerequisitesMet) {
        this.onlyShowIfPrerequisitesMet = onlyShowIfPrerequisitesMet;
    }

    public boolean shouldStoreProgressOnDeactivation() {
        return this.storeProgressOnDeactivation;
    }

    public void setStoreProgressOnDeactivation(boolean storeProgressOnDeactivation) {
        this.storeProgressOnDeactivation = storeProgressOnDeactivation;
    }

    public double getProgress(UUID uuid) {
        double progressSum = 0.0D;
        Iterator var4 = this.getRequirements().iterator();

        while(var4.hasNext()) {
            CompositeRequirement requirement = (CompositeRequirement)var4.next();
            if (this.hasCompletedRequirement(uuid, requirement.getRequirementId())) {
                ++progressSum;
            } else {
                progressSum += requirement.getProgressPercentage(uuid);
            }
        }

        return progressSum / (double)this.requirements.size();
    }

    public int getTimesCompleted(UUID uuid) {
        return this.plugin.getPlayerDataManager().getPrimaryDataStorage().map((s) -> {
            return s.getTimesCompletedPath(uuid, this.getInternalName());
        }).orElse(0);
    }

    public boolean hasCompletedPath(UUID uuid) {
        return this.getTimesCompleted(uuid) > 0;
    }

    public long getTimeLeftForCooldown(UUID uuid) {
        if (!this.isOnCooldown(uuid)) {
            return 0L;
        } else {
            long timeSinceCompletion = this.plugin.getPlayerDataManager().getPrimaryDataStorage().flatMap((primaryDataStorage) -> {
                return primaryDataStorage.getTimeSinceCompletionOfPath(uuid, this.getInternalName());
            }).orElse(0L);
            return this.getCooldown() - timeSinceCompletion;
        }
    }

    public boolean isDeactivated(UUID uuid) {
        return !this.isActive(uuid) && this.getCompletedRequirements(uuid).size() > 0;
    }

    public boolean isOnCooldown(UUID uuid) {
        if (!this.hasCooldown()) {
            return false;
        } else {
            Optional<Long> timePathIsCompleted = this.plugin.getPlayerDataManager().getPrimaryDataStorage().flatMap((s) -> {
                return s.getTimeSinceCompletionOfPath(uuid, this.getInternalName());
            });
            return timePathIsCompleted.filter((timeSinceCompletion) -> {
                return timeSinceCompletion < this.getCooldown();
            }).isPresent();
        }
    }

    public boolean isEligible(UUID uuid) {
        this.plugin.debugMessage("Checking if '" + uuid + "' is eligible for path '" + this.getDisplayName() + "'.");
        if (this.isActive(uuid)) {
            this.plugin.debugMessage("Path '" + this.getDisplayName() + "' is already active for '" + uuid + "'.");
            return false;
        } else if (this.isOnCooldown(uuid)) {
            this.plugin.debugMessage("Path '" + this.getDisplayName() + "' is on cooldown for '" + uuid + "'.");
            return false;
        } else if (this.hasCompletedPath(uuid) && !this.isRepeatable()) {
            this.plugin.debugMessage("Path '" + this.getDisplayName() + "' is already completed for '" + uuid + "' and not repeatable.");
            return false;
        } else if (!this.meetsPrerequisites(uuid)) {
            this.plugin.debugMessage("Player '" + uuid + "' does not meet all prerequisites of path '" + this.getDisplayName() + "'.");
            return false;
        } else {
            this.plugin.debugMessage("Player '" + uuid + "' meets all prerequisites of path '" + this.getDisplayName() + "'.");
            return true;
        }
    }

    public CompositeRequirement getRequirement(int id) {
        return this.getRequirements().stream().filter((compositeRequirement) -> {
            return compositeRequirement.getRequirementId() == id;
        }).findFirst().orElse(null);
    }

    public boolean hasCooldown() {
        return this.cooldown > 0L;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        if (cooldown < 0L) {
            cooldown = 0L;
        }

        this.cooldown = cooldown;
    }
}
