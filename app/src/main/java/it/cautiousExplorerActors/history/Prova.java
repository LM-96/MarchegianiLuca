package it.cautiousExplorerActors.history;

import it.unibo.issRobotSpeaker.CommandFactory;
import it.unibo.issRobotSpeaker.CrilRobotSpeaker;
import it.unibo.issRobotSpeaker.Move;
import it.unibo.issRobotSpeaker.RobotSpeaker;

public class Prova {

	public static void main(String[] args) {
		MovementHistory h = new MovementHistory();
		JourneyReverter r = new JourneyReverter();
		RobotSpeaker s = CrilRobotSpeaker.newCrilSpeaker();
		
		for(int i=0; i<10; i++)
			h.add(s.parseMovement("{\"endmove\":\"true\",\"move\":\"moveForward\"}"), i);
		
		r.revert(h.getAsStream(), 10);
		while(!r.journeyEnded())
			System.out.println(r.nextCommand());

	}

}
