package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.statsmanager.query.parameter.implementation.MovementTypeParameter;

class BlocksMovedWrapper {
    private int blocksMoved = 0;
    private String movementType = "";
    private int rawMovementType = 0;

    public BlocksMovedWrapper(int blocksMoved, int moveType) {
        this.blocksMoved = blocksMoved;
        this.movementType = this.getMovementString(moveType);
        this.rawMovementType = moveType;
    }

    public int getBlocksMoved() {
        return this.blocksMoved;
    }

    private String getMovementString(int moveType) {
        switch(moveType) {
            case 1:
                return "by boat";
            case 2:
                return "by cart";
            case 3:
                return "by pig";
            case 4:
                return "by piggy-cart";
            case 5:
                return "by horse";
            default:
                return "by foot";
        }
    }

    public String getMovementType() {
        return this.movementType;
    }

    public MovementTypeParameter.MovementType getRawMovementType() {
        switch(this.rawMovementType) {
            case 1:
                return MovementTypeParameter.MovementType.BOAT;
            case 2:
                return MovementTypeParameter.MovementType.MINECART;
            case 3:
                return MovementTypeParameter.MovementType.PIG;
            case 4:
                return MovementTypeParameter.MovementType.PIG_IN_MINECART;
            case 5:
                return MovementTypeParameter.MovementType.HORSE;
            default:
                return MovementTypeParameter.MovementType.FOOT;
        }
    }
}
