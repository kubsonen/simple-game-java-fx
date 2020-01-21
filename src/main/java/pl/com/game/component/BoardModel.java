package pl.com.game.component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BoardModel {

    @FXML
    private Label timeLabel;

    @FXML
    private Label levelLabel;

    public Label getTimeLabel() {
        return timeLabel;
    }

    public Label getLevelLabel() {
        return levelLabel;
    }
}
