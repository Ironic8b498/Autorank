package me.armar.plugins.autorank.pathbuilder.holders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CompositeRequirement {
    private final Autorank plugin;
    private List<AbstractRequirement> requirements = new ArrayList();

    public CompositeRequirement(Autorank plugin) {
        this.plugin = plugin;
    }

    public void addRequirement(AbstractRequirement req) {
        this.requirements.add(req);
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        List<AbstractRequirement> reqs = this.getRequirements();
        int size = reqs.size();
        if (size > 0 && reqs.get(0).hasCustomDescription()) {
            return reqs.get(0).getCustomDescription();
        } else if (size == 0) {
            return "";
        } else if (size == 1) {
            return reqs.get(0).getDescription();
        } else {
            String original = reqs.get(0).getDescription();

            for(int i = 0; i < size; ++i) {
                AbstractRequirement r = reqs.get(i);
                String desc = r.getDescription();
                if (i == 0) {
                    builder.append(desc).append(" or ");
                } else {
                    int difIndex = this.getDifferenceIndex(original, desc);
                    if (difIndex >= 0) {
                        desc = desc.substring(difIndex);
                        if (i == size - 1) {
                            builder.append(desc);
                        } else {
                            builder.append(desc).append(" or ");
                        }
                    }
                }
            }

            return builder.toString();
        }
    }

    private int getDifferenceIndex(String s1, String s2) {
        for(int i = 0; i < s1.length(); ++i) {
            try {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                if (Character.isDigit(c1) || Character.isDigit(c2)) {
                    return i;
                }

                if (c2 != c1) {
                    return i;
                }
            } catch (IndexOutOfBoundsException var6) {
                return -1;
            }
        }

        return -1;
    }

    public String getProgress(UUID uuid) {
        StringBuilder builder = new StringBuilder();
        List<AbstractRequirement> reqs = this.getRequirements();
        int size = reqs.size();
        if (size == 0) {
            return "";
        } else {
            String startingString = "";
            boolean needsPlayerOnline = reqs.get(0).needsOnlinePlayer();
            Player player = null;
            if (needsPlayerOnline) {
                player = Bukkit.getOfflinePlayer(uuid).getPlayer();
                boolean isPlayerOnline = player != null;
                if (!isPlayerOnline) {
                    return "The player should be online to display progress.";
                }

                startingString = reqs.get(0).getProgressString(player);
            } else {
                startingString = reqs.get(0).getProgressString(uuid);
            }

            for(int i = 0; i < size; ++i) {
                AbstractRequirement r = reqs.get(i);
                String progress = needsPlayerOnline ? r.getProgressString(player) : r.getProgressString(uuid);
                if (i == 0) {
                    builder.append(progress);
                    if (i != size - 1) {
                        builder.append(" or ");
                    }
                } else {
                    int difIndex = this.getDifferenceIndex(startingString, progress);
                    progress = progress.substring(difIndex);
                    if (i == size - 1) {
                        builder.append(progress);
                    } else {
                        builder.append(progress).append(" or ");
                    }
                }
            }

            return builder.toString();
        }
    }

    public int getRequirementId() {
        Iterator var1 = this.getRequirements().iterator();
        if (var1.hasNext()) {
            AbstractRequirement r = (AbstractRequirement)var1.next();
            return r.getId();
        } else {
            return -1;
        }
    }

    public List<AbstractRequirement> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(List<AbstractRequirement> requirements) {
        this.requirements = requirements;
    }

    public List<AbstractResult> getResults() {
        Iterator var1 = this.getRequirements().iterator();
        if (var1.hasNext()) {
            AbstractRequirement r = (AbstractRequirement)var1.next();
            return r.getAbstractResults();
        } else {
            return new ArrayList();
        }
    }

    public boolean isOptional() {
        Iterator var1 = this.getRequirements().iterator();

        AbstractRequirement r;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            r = (AbstractRequirement)var1.next();
        } while(!r.isOptional());

        return true;
    }

    public boolean meetsRequirement(UUID uuid) {
        Iterator var2 = this.getRequirements().iterator();

        AbstractRequirement r;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            r = (AbstractRequirement)var2.next();
            if (r.isMet(uuid)) {
                return true;
            }
        } while(!r.isOptional());

        return true;
    }

    public boolean useAutoCompletion() {
        Iterator var1 = this.getRequirements().iterator();

        AbstractRequirement r;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            r = (AbstractRequirement)var1.next();
        } while(!r.useAutoCompletion());

        return true;
    }

    public void runResults(Player player) {
        Iterator var2 = this.getResults().iterator();

        while(var2.hasNext()) {
            AbstractResult realAbstractResult = (AbstractResult)var2.next();
            realAbstractResult.applyResult(player);
        }

    }

    public boolean isPrerequisite() {
        Iterator var1 = this.requirements.iterator();

        AbstractRequirement req;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            req = (AbstractRequirement)var1.next();
        } while(!req.isPreRequisite());

        return true;
    }

    public double getProgressPercentage(UUID uuid) {
        double biggestPercentage = 0.0D;

        double progressPercentage;
        for(Iterator var4 = this.requirements.iterator(); var4.hasNext(); biggestPercentage = Math.max(progressPercentage, biggestPercentage)) {
            AbstractRequirement requirement = (AbstractRequirement)var4.next();
            progressPercentage = Math.min(requirement.getProgressPercentage(uuid), 1.0D);
        }

        return biggestPercentage;
    }
}
