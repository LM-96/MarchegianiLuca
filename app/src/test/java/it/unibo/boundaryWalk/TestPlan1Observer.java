package it.unibo.boundaryWalk;

import it.unibo.boundaryWalk.controller.RobotObserver;

public class TestPlan1Observer implements RobotObserver {

    private String moves;

    public TestPlan1Observer() {
        moves = "";
    }

    public String getActualMoves() {
        return moves;
    }
    public void resetMoves() {this.moves = "";}


    @Override
    public void onMovedForward(boolean collision, int duration) {
        moves += 'w';
    }

    @Override
    public void onMovedBackward(boolean collision, int duration) {
        moves += 's';
    }

    @Override
    public void onMovedLeft(boolean collision, int duration) {
       moves += 'l';
    }

    @Override
    public void onMovedRight(boolean collision, int duration) {
        moves += 'r';
    }

    @Override
    public void onMovedStop(boolean collision, int duration) {
        moves += 'h';
    }
}
