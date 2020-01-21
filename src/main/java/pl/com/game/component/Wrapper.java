package pl.com.game.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class Wrapper extends Pane {

    private final String fxmlName;
    private final FXMLLoader loader;
    private AnchorPane component;

    public Wrapper(String fxmlName) {
        this.fxmlName = fxmlName;
        this.loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlName + ".fxml"));
        try {
            component = component();
            getChildren().add(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AnchorPane component() throws Exception {
        return loader.load();
    }

    public double getComponentWidth() {
        if (component != null) {
            return component.getPrefWidth();
        }
        return 0.0;
    }

    public double getComponentHeight() {
        if (component != null)
            return component.getPrefHeight();
        return 0.0;
    }

    public FXMLLoader getLoader() {
        return loader;
    }
}
