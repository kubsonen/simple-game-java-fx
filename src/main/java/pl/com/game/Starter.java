package pl.com.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class Starter extends Application {

    private static Starter application;
    private Parent root;
    private Stage stage;
    private Scene scene;

    public void start(Stage primaryStage) throws Exception {

        application = this;

        root = FXMLLoader.load(getClass().getResource("/fxml/game-area.fxml"));
        scene = new Scene(root);

        stage = primaryStage;
        stage.setScene(scene);
        stage.show();

    }

    public Parent getRoot() {
        return root;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public static Starter getApplication() {
        return application;
    }
}
