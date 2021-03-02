package it.unibo.boundaryWalk;

import it.unibo.boundaryWalk.communication.HTTPCommunicator;
import it.unibo.boundaryWalk.communication.WebSocketCommunicator;
import it.unibo.boundaryWalk.controller.JsonRobotController;
import it.unibo.boundaryWalk.controller.RobotController;

public class BoundaryWalk {

    private static final int MOVE_TIME = 600;
    private static final int TURN_TIME = 300;

    private RobotController controller;

    public static void main(String[] args) {

        System.out.println("BoundaryWalk: starting controller...");
        //RobotController controller = new JsonRobotController(new HTTPCommunicator());
        RobotController controller = new JsonRobotController(new WebSocketCommunicator());
        BoundaryWalk bw = new BoundaryWalk(controller);

        System.out.println("BoundaryWalk: starting walk...");
        bw.boundaryWalk();


        System.out.println("BoundaryWalk: walk ended.");


    }

    public BoundaryWalk(RobotController controller) {
        this.controller = controller;
    }

    public void boundaryWalk() {
        for(int i=0; i<4; i++) {
            while(!controller.moveForward(MOVE_TIME));
            controller.moveLeft(TURN_TIME);
        }
    }
}
