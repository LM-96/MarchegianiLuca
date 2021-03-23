package it.unibo.resumableBoundaryWalker.commands;

public class RobotMovement {

    private MoveResult endMove;
    private Move move;

    protected RobotMovement(MoveResult endMove, Move move) {
        this.endMove = endMove;
        this.move = move;
    }

    public MoveResult getEndMove() {
        return endMove;
    }

    public Move getMove() {
        return move;
    }
}
