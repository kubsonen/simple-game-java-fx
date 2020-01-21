package pl.com.game.component;

import com.snatik.polygon.Point;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Obstacle extends Pane {

    private static final Integer MEAS_ERROR = 6;
    private static final Integer THICKNESS = 10;
    private final Integer HOLE_WIDTH;
    private final Integer OBSTACLE_WIDTH;
    private final Integer OBSTACLE_TX;

    private final Pane first;
    private final Pane second;

    public Obstacle(Integer holeWidth, Integer obstacleWidth, Integer obstacleTx) {
        HOLE_WIDTH = holeWidth;
        OBSTACLE_WIDTH = obstacleWidth;
        OBSTACLE_TX = obstacleTx;

        setPrefWidth(OBSTACLE_WIDTH);
        setPrefHeight(THICKNESS);
        setTranslateX(OBSTACLE_TX);
        setStyle("-fx-background-color: rgba(101, 67, 33, 0.1);");

        final int holeTx = Math.abs(randomHoleStart());

        first = new Pane();
        first.setPrefWidth(holeTx);
        first.setPrefHeight(THICKNESS);
        first.setTranslateX(0);
        first.setStyle("-fx-background-color: rgba(101, 67, 33, 1);");
        getChildren().add(first);

        second = new Pane();
        second.setPrefWidth(OBSTACLE_WIDTH - HOLE_WIDTH - holeTx);
        second.setPrefHeight(THICKNESS);
        second.setTranslateX(HOLE_WIDTH + holeTx);
        second.setStyle("-fx-background-color: rgba(101, 67, 33, 1);");
        getChildren().add(second);

    }

    private int randomHoleStart() {
        int maxTx = OBSTACLE_WIDTH - HOLE_WIDTH - MEAS_ERROR;
        return (new Random().nextInt() % maxTx) + 3;
    }

    public List<Point> getObstaclePoints() {

        List<Point> points = new ArrayList<>();

        //Left side
        for (int i = 1; i <= Double.valueOf(first.getPrefWidth()).intValue() - 20; i++) {
            for (int j = 1; j <= Double.valueOf(first.getPrefHeight()).intValue(); j++) {
                points.add(new Point((OBSTACLE_TX + i + 0.0), (getTranslateY() + j + 0.0)));
            }
        }

        //Right side
        for (int i = 20; i <= Double.valueOf(second.getPrefWidth()).intValue(); i++) {
            for (int j = 1; j <= Double.valueOf(second.getPrefHeight()).intValue(); j++) {
                points.add(new Point((OBSTACLE_TX + second.getTranslateX() + i + 0.0), (getTranslateY() + j + 0.0)));
            }
        }

        return points;

    }


}
