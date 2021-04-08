package it.cautiousExplorerActors.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import it.unibo.issRobotSpeaker.RobotMovement;

public class MovementHistory {
	
	private List<TimedMovement> history;
	
	public MovementHistory() {
		this.history = new ArrayList<>();
	}
	
	public void add(RobotMovement move, long timeElapsed) {
		history.add(new TimedMovement(move, timeElapsed));
	}
	
	public TimedMovement[] get() {
		return history.toArray(new TimedMovement[history.size()]);
	}
	
	public Stream<TimedMovement> getAsStream() {
		return history.stream();
	}
	
	public List<TimedMovement> getAsList() {
		return Collections.unmodifiableList(history);
	}
	
	public void clear() {
		history.clear();
	}

}
