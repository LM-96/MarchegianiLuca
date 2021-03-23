package it.unibo.resumableBoundaryWalker.supports;

import it.unibo.resumableBoundaryWalker.commands.CrilRobotSpeaker;
import it.unibo.resumableBoundaryWalker.commands.RobotSpeaker;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class IssObservableSupportFactory {

    /*
    private static final IssProtocol DEFAULT_PROTOCOL = IssProtocol.HTTP;
    private static final String DEFAULT_PROTOCOL_URL = "http://localHost:8090/api/move"; */

    private static final IssProtocol DEFAULT_PROTOCOL = IssProtocol.WS;
    private static final String DEFAULT_PROTOCOL_URL = "ws://localhost:8091";

    public static IssObservableSupport createSupport() throws NoSuchIssProtocolException,
            DeploymentException, IOException, URISyntaxException {
        return createSupport(DEFAULT_PROTOCOL, DEFAULT_PROTOCOL_URL);
    }

    public static IssObservableSupport createSupport(IssProtocol protocol, String url)
            throws NoSuchIssProtocolException, DeploymentException, IOException, URISyntaxException {
        RobotSpeaker speaker = CrilRobotSpeaker.getSpeaker();
        switch (protocol) {
            case WS:
                return new IssWsSupport(url, speaker);

            case HTTP:
                return new IssHttpSupport(url, speaker);

            default:
                throw new NoSuchIssProtocolException(protocol + " not available.");
        }
    }
}
