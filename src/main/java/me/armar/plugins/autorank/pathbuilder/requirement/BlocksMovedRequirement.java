package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class BlocksMovedRequirement extends AbstractRequirement {
    BlocksMovedWrapper wrapper = null;

    public BlocksMovedRequirement() {
    }

    public String getDescription() {
        String desc = Lang.BLOCKS_MOVED_REQUIREMENT.getConfigValue(this.wrapper.getBlocksMoved() + "", this.wrapper.getMovementType());
        if (this.isWorldSpecific()) {
            desc = desc.concat(" (in world '" + this.getWorld() + "')");
        }

        return desc;
    }

    public String getProgressString(UUID uuid) {
        int progressBar = this.getStatisticsManager().getBlocksMoved(uuid, this.getWorld());
        return progressBar + "/" + this.wrapper.getBlocksMoved() + " (" + this.wrapper.getMovementType() + ")";
    }

    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getBlocksMoved(uuid, this.getWorld()) >= this.wrapper.getBlocksMoved();
    }

    public boolean initRequirement(String[] options) {
        int blocksMoved = 0;
        int movementType = 0;
        if (options.length > 0) {
            blocksMoved = Integer.parseInt(options[0].trim());
        }

        if (options.length > 1) {
            movementType = Integer.parseInt(options[1].trim());
        }

        this.wrapper = new BlocksMovedWrapper(blocksMoved, movementType);
        if (this.wrapper == null) {
            this.registerWarningMessage("No valid block provided.");
            return false;
        } else {
            return true;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        int progressBar = this.getStatisticsManager().getBlocksMoved(uuid, this.getWorld());
        return (double)progressBar * 1.0D / (double)this.wrapper.getBlocksMoved();
    }
}
