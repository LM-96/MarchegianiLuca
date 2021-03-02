package it.unibo.boundaryWalk.communication;

import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ClientEndpoint
public class WebSocketCommunicator implements RobotCommunicator {

    private static final String localHostName = "localhost";
    private static final int port = 8091;
    private static URI URL;

    static {
        try {
            URL = new URI("ws://"+localHostName+":"+port);
        } catch (URISyntaxException e) {
            System.out.println("WebSocketCommunicator | Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private RemoteEndpoint.Basic endpoint;
    private Lock lock;
    private Condition condition;
    private boolean collision;
    private boolean waiting;

    public WebSocketCommunicator() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, URL);
        } catch (Exception e) {
            System.out.println("WebSocketCommunicator | Exception: " + e.getLocalizedMessage());
        }

        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();

        this.collision = false;
        this.waiting = false;
    }

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("WebSocketCommunicator | Opened websocket ");
        this.endpoint = userSession.getBasicRemote();
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("WebSocketCommunicator | Socket closed ");
        this.endpoint= null;
    }

    @OnMessage
    public void onMessage(String message) {
        lock.lock();
        try {
            JSONObject jsonObj = new JSONObject(message);
            this.collision = false;
               if( message.contains("endmove") ) {
                   this.collision = !jsonObj.get("endmove").toString().equals("true");
                   if(waiting) {
                       condition.signal();
                       waiting = false;
                   }
                   System.out.println("WebSocketCommunicator | checkCollision_simple collision=" + collision);
               }
        } finally {lock.unlock();}
    }


    @Override
    public boolean sendCommand(String data) {
        boolean res = true;
        lock.lock();
        try {
            this.endpoint.sendText(data);
            waiting = true;
            condition.await();

            res = collision;
        } catch (Exception e) {
            System.out.println("WebSocketCommunicator | sendCommand exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {lock.unlock();}

        return res;
    }
}
