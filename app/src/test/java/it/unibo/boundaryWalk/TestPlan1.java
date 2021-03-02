package it.unibo.boundaryWalk;

import it.unibo.boundaryWalk.communication.HTTPCommunicator;
import it.unibo.boundaryWalk.communication.WebSocketCommunicator;
import it.unibo.boundaryWalk.controller.JsonRobotController;
import it.unibo.boundaryWalk.controller.RobotController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestPlan1 {

    private TestPlan1Observer observer;

    private BoundaryWalk bwHTTP;
    private BoundaryWalk bwWS;

    @Before
    public void systemSetup() {
        System.out.println("TestPan1 | setup: robot should be at START POINT");

        observer = new TestPlan1Observer();
        RobotController controllerHTTP = new JsonRobotController(new HTTPCommunicator());
        controllerHTTP.addRobotObserver(observer);

        bwHTTP = new BoundaryWalk(controllerHTTP);

        RobotController controllerWS = new JsonRobotController(new WebSocketCommunicator());
        controllerWS.addRobotObserver(observer);

        bwWS = new BoundaryWalk(controllerWS);

    }

    @After
    public void terminate() {
        System.out.println("TestPan1 | terminates");
    }

    @Test
    public void testBoundaryWalk() {
        bwHTTP.boundaryWalk();

        String moves = observer.getActualMoves();
        System.out.println("testBoundaryWalk (HTTP) | moves = " + moves);
        assertTrue(moves.matches("w+lw+lw+lw+l"));

        observer.resetMoves();
        bwWS.boundaryWalk();

        moves = observer.getActualMoves();
        System.out.println("testBoundaryWalk (WS) | moves = " + moves);
        assertTrue(moves.matches("w+lw+lw+lw+l"));
    }
}
