package it.unibo.resumableBoundaryWalker.supports;

import it.unibo.resumableBoundaryWalker.commands.RobotCommand;
import it.unibo.resumableBoundaryWalker.commands.RobotInformation;
import it.unibo.resumableBoundaryWalker.commands.RobotMovement;

public interface IssSupportObserver {

    public void onAsyncRequest(RobotCommand command);
    public void onSyncRequest(RobotCommand command, RobotMovement movement);
    public void onRobotMovement(RobotMovement movement);
    public void onRobotInformation(RobotInformation information);
}
