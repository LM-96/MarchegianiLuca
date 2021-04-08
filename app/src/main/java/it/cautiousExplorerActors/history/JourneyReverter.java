package it.cautiousExplorerActors.history;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.issRobotSpeaker.CommandFactory;
import it.unibo.issRobotSpeaker.Move;
import it.unibo.issRobotSpeaker.RobotCommand;

public class JourneyReverter {
	
	private static Map<Move, Move> reverseMove = new HashMap<>();
	static {
		reverseMove.put(Move.MOVE_FORWARD, Move.MOVE_FORWARD);
		reverseMove.put(Move.TURN_LEFT, Move.TURN_RIGHT);
		reverseMove.put(Move.TURN_RIGHT, Move.TURN_LEFT);
		reverseMove.put(Move.MOVE_BACKWARD, Move.MOVE_BACKWARD);
	}
	private Iterator<RobotCommand> reverseIt;
	
	public JourneyReverter() {}
	
	public void revert(Stream<TimedMovement> moves, int turnTime) {
		RobotCommand tl = CommandFactory.createCommand(Move.TURN_LEFT, turnTime);
		LinkedList<RobotCommand> list =
				moves.map(t -> CommandFactory.createCommand(
						reverseMove.get(t.getMovement().getMove()), (int) t.getTime()))
				.collect(Collectors.toCollection(LinkedList::new));
		list.addLast(tl);
		list.addLast(tl);
		list.addFirst(tl);
		list.addFirst(tl);
		
		reverseIt = list.descendingIterator();
	}
	
	public void revert(Collection<TimedMovement> moves, int turnTime) {
		revert(moves.stream(), turnTime);
	}
	
	public boolean journeyEnded() {
		boolean res = reverseIt.hasNext();
		if(!res)
			reverseIt = null;
		
		return !res;
	}
	
	public RobotCommand nextCommand() {
		return reverseIt.next();
	}

}
