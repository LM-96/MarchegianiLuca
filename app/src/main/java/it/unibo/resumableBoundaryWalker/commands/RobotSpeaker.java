package it.unibo.resumableBoundaryWalker.commands;

public interface RobotSpeaker {

    public String encode(RobotCommand command);
    public RobotMovement parseMovement(String jsonString);
    public RobotInformation parseInformation(String jsonString);
}
