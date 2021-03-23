package it.unibo.resumableBoundaryWalker.controller;

import it.unibo.resumableBoundaryWalker.commands.*;
import it.unibo.resumableBoundaryWalker.supports.IssObservableSupport;
import it.unibo.resumableBoundaryWalker.supports.IssObservableSupportFactory;
import it.unibo.resumableBoundaryWalker.supports.IssSupportObserver;
import it.unibo.resumableBoundaryWalker.supports.NoSuchIssProtocolException;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RobotController implements IssSupportObserver {

    private RobotMovesInfo robotMovesInfo;
    private RobotMoveLogic robotMoveLogic;
    private IssObservableSupport support;

    private AtomicBoolean canProceed;
    private String lastJouney;

    public RobotController() throws NoSuchIssProtocolException, DeploymentException,
            IOException, URISyntaxException {
        support = IssObservableSupportFactory.createSupport();
        canProceed = new AtomicBoolean(false);
        robotMovesInfo = new RobotMovesInfo(false);
        robotMoveLogic = new RobotBoundaryLogic(robotMovesInfo);

        support.attachObserver(this);
    }

    public void setCanProceed(boolean canProceed) throws IOException, IllegalMovementException {
       boolean oldValue = this.canProceed.getAndSet(canProceed);

        if(canProceed == true && oldValue != canProceed) {
            Optional<RobotCommand> cmd = Optional.empty();
            while(!(cmd = robotMoveLogic.askForNext()).isPresent());

            support.requestAsync(cmd.get());
        }

        else if(canProceed == false && oldValue != canProceed) {
            support.requestAsync(CommandFactory.createCommand(Move.ALARM, 10));
        }
    }

    public boolean isProceeding() {
        return canProceed.get();
    }

    public RobotMovesInfo getRobotMovesInfo() {
        return robotMovesInfo;
    }

    public String getLastJouney() {
        return lastJouney;
    }

    public IssObservableSupport getSupport() {
        return support;
    }

    public void close() throws IOException {
        support.detachObserver(this);
        support.close();
    }

    @Override
    public void onAsyncRequest(RobotCommand command) {

    }

    @Override
    public void onSyncRequest(RobotCommand command, RobotMovement movement) {

    }

    @Override
    public void onRobotMovement(RobotMovement movement) {
        robotMovesInfo.addNewExecutedMovement(movement);
        Optional<RobotCommand> cmd = Optional.empty();
        try {
             cmd = robotMoveLogic.askForNext();
        } catch (IllegalMovementException e) {
            e.printStackTrace();
        }

        if(canProceed.get()) {
            if(cmd.isPresent()) {
                try {
                    support.requestAsync(cmd.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                lastJouney = robotMovesInfo.getMovesRepresentationAndClean();
                System.out.println("Boundary done. Jouney: " + lastJouney);
                canProceed.set(false);
            }
        }

    }

    @Override
    public void onRobotInformation(RobotInformation information) {

    }
}
