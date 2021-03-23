package it.unibo.resumableBoundaryWalker.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public interface AlertManager {

    public static void showErrorPopup(String title, String header, String text) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        if(Platform.isFxApplicationThread())
            alert.showAndWait();
        else
            Platform.runLater(
                    new Runnable() {
                        @Override
                        public void run() {
                            alert.showAndWait();
                        }
                    });
    }

}
