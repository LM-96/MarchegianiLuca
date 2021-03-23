package it.unibo.resumableBoundaryWalker.supports;

import it.unibo.resumableBoundaryWalker.commands.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class IssHttpSupport extends IssObservableSupport implements Runnable{

    private static final int BUFFER_CAPACITY = 10;

    private CloseableHttpClient httpClient;
    private URI URL;

    private BlockingQueue<RobotCommand> commands;
    private AtomicBoolean service;

    protected IssHttpSupport(String URL, RobotSpeaker speaker) throws URISyntaxException {
        super(speaker);
        this.httpClient = HttpClients.createDefault();
        this.URL = new URI(URL);

        this.commands = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
        service = new AtomicBoolean(false);
        new Thread(this).start();
    }

    @Override
    public void requestAsync(RobotCommand command) throws  IOException{
        try {
            commands.put(command);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        notifyAsyncRequest(command);
    }

    @Override
    public RobotMovement requestSync(RobotCommand command) throws IOException {
        StringEntity entity     = new StringEntity(speaker.encode(command));
        HttpUriRequest httppost = null;
        httppost = RequestBuilder.post()
                .setUri(URL)
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .setEntity(entity)
                .build();
        CloseableHttpResponse response = httpClient.execute(httppost);
        String jsonResponse = EntityUtils.toString( response.getEntity() );

        RobotMovement movement = speaker.parseMovement(jsonResponse);
        if(movement != null) {
            notifySyncRequest(command, movement);
        }

        return movement;
    }

    @Override
    public void onReceived(String message) {
        RobotMovement movement = speaker.parseMovement(message);
        if(movement != null)
            notifyRobotMovementReceived(movement);
    }

    @Override
    public void close() throws IOException {
        httpClient.close();

        service.set(false);
        try {
            commands.put(CommandFactory.createCommand(Move.MOVE_FORWARD, -1));
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void run() {
        service.set(true);

        RobotCommand command = null;
        try {
            while(true) {
                command = commands.take();
                if(service.get() == false || command.getTime() <= 0) {
                    commands.clear();
                    return;
                }

                StringEntity entity     = new StringEntity(speaker.encode(command));
                HttpUriRequest httppost = RequestBuilder.post()
                        .setUri(URL)
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
                        .setEntity(entity)
                        .build();
                CloseableHttpResponse response = httpClient.execute(httppost);
                String jsonResponse = EntityUtils.toString( response.getEntity() );

                onReceived(jsonResponse);

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
