package it.unibo.resumableBoundaryWalker.commands;

public interface CommandFactory {

    public static RobotCommand createCommand(Move move, int time) {
        return new RobotCommand(move, time);
    }
}
