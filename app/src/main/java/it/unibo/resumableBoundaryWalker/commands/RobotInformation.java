package it.unibo.resumableBoundaryWalker.commands;

public class RobotInformation<T> {

    private Emitter emitter;
    private T data;

    protected RobotInformation(Emitter emitter, T data) {
        this.emitter = emitter;
        this.data = data;
    }

    public Emitter getEmitter() {
        return emitter;
    }

    public T getData() {
        return data;
    }
}
