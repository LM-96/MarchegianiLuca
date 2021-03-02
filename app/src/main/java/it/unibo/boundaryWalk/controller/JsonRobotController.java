package it.unibo.boundaryWalk.controller;

import it.unibo.boundaryWalk.communication.RobotCommunicator;

import java.util.ArrayList;
import java.util.List;

public class JsonRobotController implements RobotController {

    private RobotCommunicator communicator;
    private List<RobotObserver> robotObserver;

    public JsonRobotController(RobotCommunicator communicator) {
        this.communicator = communicator;
        this.robotObserver = new ArrayList<RobotObserver>();
    }

    @Override
    public boolean moveForward(int duration) {
       boolean collision = this.communicator.sendCommand(generateJson("moveForward", duration));
       robotObserver.forEach(o -> o.onMovedForward(collision, duration));

       return collision;
    }

    @Override
    public boolean moveBackward(int duration) {
        boolean collision = this.communicator.sendCommand(generateJson("moveBackward", duration));
        robotObserver.forEach(o->o.onMovedBackward(collision, duration));

        return collision;
    }

    @Override
    public boolean moveLeft(int duration) {
        boolean collision = this.communicator.sendCommand(generateJson("turnLeft", duration));
        robotObserver.forEach(o -> o.onMovedLeft(collision, duration));

        return collision;
    }

    @Override
    public boolean moveRight(int duration) {
        boolean collision = this.communicator.sendCommand(generateJson("turnRight", duration));
        robotObserver.forEach(o -> o.onMovedRight(collision, duration));

        return collision;
    }

    @Override
    public boolean moveStop(int duration) {
        boolean collision = this.communicator.sendCommand(generateJson("alarm", duration));
        robotObserver.forEach(o -> o.onMovedStop(collision, duration));

        return collision;
    }

    @Override
    public void addRobotObserver(RobotObserver observer) {
        this.robotObserver.add(observer);
    }

    @Override
    public void detachRobotObserver(RobotObserver observer) {
        this.robotObserver.remove(observer);
    }

    private String generateJson(String move, int time) {
        return "{\"robotmove\":\"" + move + "\" , \"time\": " + time + "}";
    }
}
