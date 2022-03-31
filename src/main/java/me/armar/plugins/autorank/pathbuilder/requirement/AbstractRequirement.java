package me.armar.plugins.autorank.pathbuilder.requirement;

import io.reactivex.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.statsmanager.StatisticsManager;
import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class AbstractRequirement {
    private final List<String> errorMessages = new ArrayList();
    private final List<Library> dependencies = new ArrayList();
    private boolean optional = false;
    private boolean autoComplete = false;
    private boolean isPreRequisite = false;
    private int reqId;
    private List<AbstractResult> abstractResults = new ArrayList();
    private String world;
    private String customDescription;

    public AbstractRequirement() {
    }

    public final Autorank getAutorank() {
        return Autorank.getInstance();
    }

    public final DependencyManager getDependencyManager() {
        return this.getAutorank().getDependencyManager();
    }

    public abstract String getDescription();

    public String getProgressString(@NonNull Player player) {
        return "No progress provided";
    }

    public String getProgressString(@NonNull UUID uuid) {
        return "No progress provided";
    }

    public int getId() {
        return this.reqId;
    }

    public void setId(int reqId) {
        this.reqId = reqId;
    }

    public List<AbstractResult> getAbstractResults() {
        return this.abstractResults;
    }

    public void setAbstractResults(List<AbstractResult> abstractResults) {
        this.abstractResults = abstractResults;
    }

    public StatisticsManager getStatisticsManager() {
        return this.getAutorank().getStatisticsManager();
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isWorldSpecific() {
        return this.world != null;
    }

    public boolean isMet(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.getAutorank().debugMessage("Checking if user '" + uuid + "' meets requirement '" + this.getDescription() + "'.");
        boolean meetsRequirement;
        if (this.needsOnlinePlayer()) {
            this.getAutorank().debugMessage("User needs to be online for requirement to check.");
            if (offlinePlayer.isOnline()) {
                meetsRequirement = this.meetsRequirement(offlinePlayer.getPlayer());
                if (meetsRequirement) {
                    this.getAutorank().debugMessage("User is online and meets requirement.");
                } else {
                    this.getAutorank().debugMessage("User is online, but does not meet requirement.");
                }

                return meetsRequirement;
            } else {
                this.getAutorank().debugMessage("User needed to be online for requirement to check, but he was not online.");
                return false;
            }
        } else {
            this.getAutorank().debugMessage("User does not need to be online");
            meetsRequirement = this.meetsRequirement(uuid);
            if (meetsRequirement) {
                this.getAutorank().debugMessage("User meets requirement");
            } else {
                this.getAutorank().debugMessage("User doesn't meet requirement");
            }

            return meetsRequirement;
        }
    }

    protected boolean meetsRequirement(UUID uuid) {
        return false;
    }

    protected boolean meetsRequirement(Player player) {
        return false;
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public abstract boolean initRequirement(String[] var1);

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public boolean useAutoCompletion() {
        return this.autoComplete;
    }

    public boolean isPreRequisite() {
        return this.isPreRequisite;
    }

    public void setPreRequisite(boolean preRequisite) {
        this.isPreRequisite = preRequisite;
    }

    public void registerWarningMessage(String message) {
        if (message != null && !this.errorMessages.contains(message)) {
            this.errorMessages.add(message);
        }

    }

    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    public List<Library> getDependencies() {
        return this.dependencies;
    }

    public void addDependency(Library library) {
        if (library != null) {
            this.dependencies.add(library);
        }

    }

    public boolean hasCustomDescription() {
        return this.getCustomDescription() != null;
    }

    public String getCustomDescription() {
        return this.customDescription;
    }

    public void setCustomDescription(String description) {
        this.customDescription = description;
    }

    public boolean needsOnlinePlayer() {
        return false;
    }

    public double getProgressPercentage(UUID uuid) {
        return this.isMet(uuid) ? 1.0D : this.getProgressInPercentage(uuid);
    }

    private double getProgressInPercentage(UUID uuid) {
        return 0.0D;
    }
}
