package it.unibo.resumableBoundaryWalker.commands;

public class RobotCommand {

    private Move move;
    private int time;

    protected RobotCommand(Move move, int time) {
        this.move = move;
        this.time = time;
    }

    public Move getMove() {
        return move;
    }

    public int getTime() {
        return time;
    }
}
