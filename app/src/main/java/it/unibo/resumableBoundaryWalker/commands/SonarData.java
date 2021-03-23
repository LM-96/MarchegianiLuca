package it.unibo.resumableBoundaryWalker.commands;

public class SonarData {
    private String sonarName;
    private int distance;
    private String axis;

    public SonarData(String sonarName, int distance, String axis) {
        this.sonarName = sonarName;
        this.distance = distance;
        this.axis = axis;
    }

    protected String getSonarName() {
        return sonarName;
    }

    public int getDistance() {
        return distance;
    }

    public String getAxis() {
        return axis;
    }
}
