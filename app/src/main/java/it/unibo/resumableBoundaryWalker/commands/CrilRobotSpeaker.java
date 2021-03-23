package it.unibo.resumableBoundaryWalker.commands;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CrilRobotSpeaker implements RobotSpeaker{

    private final static BiMap<Move, String> MOVES = HashBiMap.create(5);
    private final static Map<String, MoveResult> MOVE_RESULTS = new HashMap<String, MoveResult>();
    static {
        MOVES.put(Move.MOVE_FORWARD, "moveForward");
        MOVES.put(Move.MOVE_BACKWARD, "moveBackward");
        MOVES.put(Move.TURN_LEFT, "turnLeft");
        MOVES.put(Move.TURN_RIGHT, "turnRight");
        MOVES.put(Move.ALARM, "alarm");

        MOVE_RESULTS.put("true", MoveResult.SUCCEDED);
        MOVE_RESULTS.put("false", MoveResult.FAILED);
        MOVE_RESULTS.put("halted", MoveResult.HALTED);
        MOVE_RESULTS.put("notallowed", MoveResult.NOT_ALLOWED);
    }

    private static RobotSpeaker instance = null;
    public static RobotSpeaker getSpeaker() {
        return new CrilRobotSpeaker();
    }

    private CrilRobotSpeaker() {}

    @Override
    public String encode(RobotCommand command) {
        return "{\"robotmove\":\"" + MOVES.get(command.getMove()) + "\", \"time\": " +
                Integer.toString(command.getTime()) + "}";
    }

    @Override
    public RobotMovement parseMovement(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        if(json.has("endmove") && json.has("move")) {
            return new RobotMovement(
                    MOVE_RESULTS.get(json.getString("endmove")),
                    MOVES.inverse().get(json.getString("move"))
            );
        }

        return null;
    }

    @Override
    public RobotInformation parseInformation(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        if(json.has("sonarName") && json.has("distance") && json.has("axis")) {
            SonarData sonarData = new SonarData(json.getString("sonarName"),
                    json.getInt("distance"), json.getString("axis"));
            return new RobotInformation<SonarData>(Emitter.SONAR, sonarData);
        }

        if(json.has("collision") && json.has("move")) {
            SensorData sensorData = new SensorData(json.getBoolean("collision"),
                    MOVES.inverse().get(json.getString("move")));

            return new RobotInformation<SensorData>(Emitter.SENSOR, sensorData);
        }

        return null;
    }
}
