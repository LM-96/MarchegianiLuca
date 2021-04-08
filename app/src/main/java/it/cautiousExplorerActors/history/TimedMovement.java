package it.cautiousExplorerActors.history;

import it.unibo.issRobotSpeaker.RobotMovement;

public class TimedMovement {
	
	private RobotMovement movement;
	private long time;
	
	public TimedMovement(RobotMovement movement, long time) {
		this.movement = movement;
		this.time = time;
	}

	public RobotMovement getMovement() {
		return movement;
	}

	public long getTime() {
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((movement == null) ? 0 : movement.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimedMovement other = (TimedMovement) obj;
		if (movement == null) {
			if (other.movement != null)
				return false;
		} else if (!movement.equals(other.movement))
			return false;
		if (time != other.time)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimedMovement [movement=" + movement + ", time=" + time + "]";
	}

}
