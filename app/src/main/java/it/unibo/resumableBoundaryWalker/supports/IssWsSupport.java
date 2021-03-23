package it.unibo.resumableBoundaryWalker.supports;

import it.unibo.resumableBoundaryWalker.commands.RobotCommand;
import it.unibo.resumableBoundaryWalker.commands.RobotInformation;
import it.unibo.resumableBoundaryWalker.commands.RobotMovement;
import it.unibo.resumableBoundaryWalker.commands.RobotSpeaker;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ClientEndpoint
public class IssWsSupport extends IssObservableSupport {
    private URI URL;
    private Session session;

    private RobotMovement response;
    private Lock lock;
    private Condition condition;
    private int suspended;

    protected IssWsSupport(String URL, RobotSpeaker speaker) throws URISyntaxException, IOException, DeploymentException {
        super(speaker);
        this.URL = new URI(URL);
        this.speaker = speaker;

        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        suspended = 0;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, this.URL);
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.session = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.session = null;
    }

    @Override
    public void requestAsync(RobotCommand command) {
        session.getAsyncRemote().sendText(speaker.encode(command));
        notifyAsyncRequest(command);
    }

    @Override
    public RobotMovement requestSync(RobotCommand command) throws IOException {
        RobotMovement res = null;
        try{
            lock.lock();

            session.getBasicRemote().sendText(speaker.encode(command));
            suspended++;
            while(response == null)
                condition.await();
            suspended--;

         res = response;
         response = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {lock.unlock();}

        notifySyncRequest(command, res);
        return res;
    }

    @Override
    @OnMessage
    public void onReceived(String message) {
        RobotMovement movement = null;
        RobotInformation information = null;
        try{
            lock.lock();
            movement = speaker.parseMovement(message);
            if(movement != null && suspended > 0){
                response = movement;
                condition.signalAll();
            }
        } finally {lock.unlock();}

        information = speaker.parseInformation(message);

        if(movement != null)
            notifyRobotMovementReceived(movement);

        if(information != null)
            notifyRobotInformationReceived(information);
    }

    @Override
    public void close() throws IOException {
        session.close();
    }
}
