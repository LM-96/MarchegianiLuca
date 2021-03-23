package it.unibo.resumableBoundaryWalker.supports;

import it.unibo.resumableBoundaryWalker.commands.RobotCommand;
import it.unibo.resumableBoundaryWalker.commands.RobotMovement;

import java.io.IOException;

public interface IssSupport {

    public void requestAsync(RobotCommand command) throws IOException;
    public RobotMovement requestSync(RobotCommand command) throws IOException;
    public void onReceived(String message);

    public void close() throws IOException;
}
