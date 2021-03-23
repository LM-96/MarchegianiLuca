package it.unibo.resumableBoundaryWalker.gui;

import it.unibo.resumableBoundaryWalker.controller.IllegalMovementException;
import it.unibo.resumableBoundaryWalker.controller.RobotController;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class MainPane extends VBox {

    private static final int SPACING = 5;
    private static final int BUTTONS_HEIGHT = 40;
    private static final int BUTTONS_WIDTH = 140;
    private static final String BUTTONS_STYLE = "-fx-font-size: 2em;";

    private Button resumeBtn;
    private Button stopBtn;

    private RobotController controller;

    public MainPane() {
        super(SPACING);
        initPane();
    }

    public void attachController(RobotController controller) {
        this.controller = controller;

        resumeBtn.setOnAction(this::onResumeButtonPressed);
        stopBtn.setOnAction(this::onStopButtonPressed);
    }

    private void initPane() {
        setAlignment(Pos.CENTER);

        resumeBtn = new Button("RESUME");
        resumeBtn.setMinSize(BUTTONS_WIDTH, BUTTONS_HEIGHT);
        resumeBtn.setStyle(BUTTONS_STYLE);

        stopBtn = new Button("STOP");
        stopBtn.setMinSize(BUTTONS_WIDTH, BUTTONS_HEIGHT);
        stopBtn.setStyle(BUTTONS_STYLE);

        getChildren().addAll(resumeBtn, stopBtn);
        setPadding(new Insets(10, 10, 10, 10));
    }

    private void onResumeButtonPressed(ActionEvent event) {
        try {
           if(controller != null)
               controller.setCanProceed(true);
        } catch (IOException e) {
            AlertManager.showErrorPopup("I/O Error", e.getClass().getSimpleName(), e.getLocalizedMessage());
        } catch (IllegalMovementException e) {
            AlertManager.showErrorPopup("Illegal Movement Done", e.getClass().getSimpleName(), e.getLocalizedMessage());
        }
    }

    private void onStopButtonPressed(ActionEvent event) {
        try {
            if(controller != null)
                controller.setCanProceed(false);
        } catch (Exception e) {
            //No exception can be thrown
        }
    }
}
