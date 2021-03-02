package it.unibo.boundaryWalk.controller;

public interface RobotController {

    public boolean moveForward(int duration);
    public boolean moveBackward(int duration);
    public boolean moveLeft(int duration);
    public boolean moveRight(int duration);
    public boolean moveStop(int duration);

    public void addRobotObserver(RobotObserver observer);
    public void detachRobotObserver(RobotObserver observer);
}
