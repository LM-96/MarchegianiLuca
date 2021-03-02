package it.unibo.boundaryWalk.controller;

public interface RobotObserver {

    public void onMovedForward(boolean collision, int duration);
    public void onMovedBackward(boolean collision, int duration);
    public void onMovedLeft(boolean collision, int duration);
    public void onMovedRight(boolean collision, int duration);
    public void onMovedStop(boolean collision, int duration);
}
