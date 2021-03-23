package it.unibo.resumableBoundaryWalker.controller;

import it.unibo.resumableBoundaryWalker.commands.*;

import java.util.Optional;

public class RobotBoundaryLogic implements RobotMoveLogic{

    private final static int DEFAULT_WTIME = 330;
    private final static int DEFAULT_LTIME = 200;

    private RobotMovesInfo robotMovesInfo;
    private int step;

    public RobotBoundaryLogic(RobotMovesInfo robotMovesInfo) {
        this.robotMovesInfo = robotMovesInfo;
        step = 0;
    }

    @Override
    public Optional<RobotCommand> askForNext() throws IllegalMovementException {
        RobotMovement lastMove = robotMovesInfo.getLastMove();

        if(lastMove == null)
            return Optional.of(CommandFactory.createCommand(Move.MOVE_FORWARD, DEFAULT_WTIME));

        if(lastMove.getMove() == Move.TURN_LEFT && lastMove.getEndMove() == MoveResult.SUCCEDED) {
            step++;
            if(step >= 4) {
                step = 0;
                return Optional.empty();
            }

            return Optional.of(CommandFactory.createCommand(Move.MOVE_FORWARD, DEFAULT_WTIME));
        }

        if(lastMove.getMove() == Move.MOVE_FORWARD || lastMove.getMove() == Move.ALARM) {
            if(lastMove.getEndMove() == MoveResult.SUCCEDED ||
                    lastMove.getEndMove() == MoveResult.HALTED)
                return Optional.of(CommandFactory.createCommand(Move.MOVE_FORWARD, DEFAULT_WTIME));
            else if(lastMove.getEndMove() == MoveResult.FAILED)
                return Optional.of(CommandFactory.createCommand(Move.TURN_LEFT, DEFAULT_LTIME));
        }

        throw new IllegalMovementException(lastMove.getMove() + ": robot has executed an illegal movement.");
    }
}
