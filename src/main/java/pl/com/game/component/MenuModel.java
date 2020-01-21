package pl.com.game.component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MenuModel {

    @FXML
    private Label menuLabel;

    @FXML
    private Button newGameButton;

    @FXML
    private Button exitButton;

    public Label getMenuLabel() {
        return menuLabel;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
