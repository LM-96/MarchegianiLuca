package it.unibo.resumableBoundaryWalker.controller;

import it.unibo.resumableBoundaryWalker.commands.RobotCommand;

import java.util.Optional;

public interface RobotMoveLogic {

    public Optional<RobotCommand> askForNext() throws IllegalMovementException;
}
