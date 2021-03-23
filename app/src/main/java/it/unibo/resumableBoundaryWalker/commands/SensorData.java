package it.unibo.resumableBoundaryWalker.commands;

public class SensorData {

    private boolean collision;
    private Move move;

    protected SensorData(boolean collision, Move move) {
        this.collision = collision;
        this.move = move;
    }

    public boolean isCollision() {
        return collision;
    }

    public Move getMove() {
        return move;
    }
}
