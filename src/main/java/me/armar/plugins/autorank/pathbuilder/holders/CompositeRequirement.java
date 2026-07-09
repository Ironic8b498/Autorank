package me.armar.plugins.autorank.pathbuilder.holders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CompositeRequirement {
    private final Autorank plugin;
    private List<AbstractRequirement> requirements = new ArrayList<>();

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
        if (size > 0 && reqs.getFirst().hasCustomDescription()) {
            return reqs.getFirst().getCustomDescription();
        } else if (size == 0) {
            return "";
        } else if (size == 1) {
            return reqs.getFirst().getDescription();
        } else {
            String original = reqs.getFirst().getDescription();

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
            } catch (IndexOutOfBoundsException var61) {
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
            boolean needsPlayerOnline = reqs.getFirst().needsOnlinePlayer();
            Player player = null;
            String startingString;
            if (needsPlayerOnline) {
                player = Bukkit.getOfflinePlayer(uuid).getPlayer();
                boolean isPlayerOnline = player != null;
                if (!isPlayerOnline) {
                    return "The player should be online to display progress.";
                }

                startingString = reqs.getFirst().getProgressString(player);
            } else {
                startingString = reqs.getFirst().getProgressString(uuid);
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
        Iterator<AbstractRequirement> var1 = this.getRequirements().iterator();
        if (var1.hasNext()) {
            AbstractRequirement r = var1.next();
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
        Iterator<AbstractRequirement> var1 = this.getRequirements().iterator();
        if (var1.hasNext()) {
            AbstractRequirement r = var1.next();
            return r.getAbstractResults();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean isOptional() {
        for(AbstractRequirement r : this.getRequirements()) {
            if (r.isOptional()) {
                return true;
            }
        }

        return false;
    }

    public boolean meetsRequirement(UUID uuid) {
        for(AbstractRequirement r : this.getRequirements()) {
            if (r.isMet(uuid)) {
                return true;
            }

            if (r.isOptional()) {
                return true;
            }
        }

        return false;
    }

    public boolean useAutoCompletion() {
        for(AbstractRequirement r : this.getRequirements()) {
            if (r.useAutoCompletion()) {
                return true;
            }
        }

        return false;
    }

    public void runResults(Player player) {
        for(AbstractResult realAbstractResult : this.getResults()) {
            realAbstractResult.applyResult(player);
        }

    }

    public boolean isPrerequisite() {
        for(AbstractRequirement req : this.requirements) {
            if (req.isPreRequisite()) {
                return true;
            }
        }

        return false;
    }

    public double getProgressPercentage(UUID uuid) {
        double biggestPercentage = 0.0F;

        for(AbstractRequirement requirement : this.requirements) {
            double progressPercentage = Math.min(requirement.getProgressPercentage(uuid), 1.0F);
            biggestPercentage = Math.max(progressPercentage, biggestPercentage);
        }

        return biggestPercentage;
    }
}
