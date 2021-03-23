package it.unibo.resumableBoundaryWalker.supports;

import it.unibo.resumableBoundaryWalker.commands.RobotCommand;
import it.unibo.resumableBoundaryWalker.commands.RobotInformation;
import it.unibo.resumableBoundaryWalker.commands.RobotMovement;
import it.unibo.resumableBoundaryWalker.commands.RobotSpeaker;

import java.util.ArrayList;
import java.util.List;

public abstract class IssObservableSupport implements IssSupport{

    private final List<IssSupportObserver> observers;
    protected RobotSpeaker speaker;

    protected IssObservableSupport(RobotSpeaker speaker) {
        observers = new ArrayList<>();
        this.speaker = speaker;
    }

    protected void notifyAsyncRequest(RobotCommand command) {
        observers.forEach(o -> o.onAsyncRequest(command));
    }

    protected void notifySyncRequest(RobotCommand command, RobotMovement movement) {
        observers.forEach(o -> o.onSyncRequest(command, movement));
    }

    protected void notifyRobotMovementReceived(RobotMovement movement) {
        observers.forEach(o -> o.onRobotMovement(movement));
    }

    protected void notifyRobotInformationReceived(RobotInformation<?> information) {
        observers.forEach(o -> o.onRobotInformation(information));
    }

    public void attachObserver(IssSupportObserver observer) {
        observers.add(observer);
    }

    public void detachObserver(IssSupportObserver observer) {
        observers.remove(observer);
    }
}
