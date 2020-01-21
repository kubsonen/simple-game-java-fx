package pl.com.game.component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MenuHighModel {

    @FXML
    private Label menuLabel;

    @FXML
    private TextField nickField;

    @FXML
    private Button saveRecord;

    @FXML
    private Label infoLabel;

    @FXML
    private Button newGameButton;

    @FXML
    private Button exitButton;

    public Label getMenuLabel() {
        return menuLabel;
    }

    public TextField getNickField() {
        return nickField;
    }

    public Button getSaveRecord() {
        return saveRecord;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
