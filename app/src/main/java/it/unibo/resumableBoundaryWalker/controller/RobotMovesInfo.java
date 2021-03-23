package it.unibo.resumableBoundaryWalker.controller;

import it.unibo.resumableBoundaryWalker.commands.Move;
import it.unibo.resumableBoundaryWalker.commands.RobotMovement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RobotMovesInfo {

    private static Map<Move, String> TRANSLATIONS = new HashMap<>();
    static {
        TRANSLATIONS.put(Move.MOVE_FORWARD, "w");
        TRANSLATIONS.put(Move.MOVE_BACKWARD, "b");
        TRANSLATIONS.put(Move.TURN_LEFT, "l");
        TRANSLATIONS.put(Move.TURN_RIGHT, "r");
        TRANSLATIONS.put(Move.ALARM, "a");
    }

    private List<RobotMovement> journey;
    private boolean doMap;

    public RobotMovesInfo(boolean doMap) {
        this.doMap = doMap;
        journey = new ArrayList<>();
    }

    public List<RobotMovement> getJourney() {
        return journey;
    }

    public RobotMovement getLastMove() {
        if(journey.size() == 0)
            return null;

        return journey.get(journey.size() - 1);
    }

    public String getMovesRepresentation() {
        if(doMap) {
            //TODO...
        }

        return journey
                .stream()
                .map(m -> TRANSLATIONS.get(m.getMove()))
                .collect(Collectors.joining());
    }

    public String getMovesRepresentationAndClean() {
        String res = getMovesRepresentation();
        journey.clear();

        return res;
    }

    public void addNewExecutedMovement(RobotMovement movement) {
        this.journey.add(movement);
    }
}
