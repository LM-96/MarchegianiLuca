package it.unibo.boundaryWalk.communication;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

public class HTTPCommunicator implements RobotCommunicator{

    private final String localHostName = "localhost";
    private  final int port = 8090;
    private  final String URL = "http://"+localHostName+":"+port+"/api/move";

    private CloseableHttpClient client;

    public  HTTPCommunicator() {
        this.client = HttpClients.createDefault();
    }

    @Override
    public boolean sendCommand(String data) {
        try {

            StringEntity entity = new StringEntity(data);
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI(URL))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Accept", "application/json")
                    .setEntity(entity)
                    .build();
            CloseableHttpResponse response = client.execute(httppost);
            //System.out.println( "MoveVirtualRobot | sendCmd response= " + response );
            boolean collision = checkCollision(response);
            return collision;
        } catch(Exception e){
            System.out.println("ERROR:" + e.getMessage());
            return true;
        }
    }

    private boolean checkCollision(CloseableHttpResponse response) throws IOException {
        try{
            //response.getEntity().getContent() is an InputStream
            String jsonStr = EntityUtils.toString( response.getEntity() );
            System.out.println( "MoveVirtualRobot | checkCollision_simple jsonStr= " +  jsonStr );
            //jsonStr = {"endmove":true,"move":"moveForward"}
            JSONObject jsonObj = new JSONObject(jsonStr) ;
            boolean collision = false;
            if( jsonObj.get("endmove") != null ) {
                collision = ! jsonObj.get("endmove").toString().equals("true");
                System.out.println("MoveVirtualRobot | checkCollision_simple collision=" + collision);
            }
            return collision;
        }catch(Exception e){
            System.out.println("MoveVirtualRobot | checkCollision_simple ERROR:" + e.getMessage());
            throw(e);
        }
    }
}
