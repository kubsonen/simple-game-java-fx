package pl.com.game.controller;

import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import pl.com.game.Starter;
import pl.com.game.component.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GameArea {

    private boolean gameRun = false;
    private double gameSpeed = 5.0;
    private int level = 1;
    private int levelSecondsInterval = 10;
    private long startTime = System.currentTimeMillis();

    @FXML
    private AnchorPane anchorPane;

    private static double anchorPaneWidth;
    private static double anchorPaneHeight;

    private final AnchorPane BOLID = bolid();
    private final Double BOLID_WIDTH = BOLID.getPrefWidth();
    private final Double BOLID_HEIGHT = BOLID.getPrefHeight();

    private static final Double BOLID_X = 360.0;
    private static final Double BOLID_Y = 630.0;
    private static final Double MAX_TIMEOUT = 200.0;
    private static final Double MULTIPLIER = 7.0;
    private static final Double BASIS = 8.0;

    private final double BOLID_CENTER_X = BOLID_X + (BOLID_WIDTH / 2);
    private final double BOLID_CENTER_Y = BOLID_Y + (BOLID_HEIGHT / 2);

    private int direction = 0;
    private double angle = 0.0;
    private double move = 8.0;
    private long lastUpdate = System.currentTimeMillis();

    private double CURSOR_X = BOLID_CENTER_X;
    private double CURSOR_Y = BOLID_CENTER_Y;

    //Borders
    private static final int BORDERS_COUNT = 25;
    private static final int BORDER_MIN_FIRST_Y_TO_UP = -900;
    private static RoadBorder[] bordersLeft = new RoadBorder[BORDERS_COUNT];
    private static RoadBorder[] bordersRight = new RoadBorder[BORDERS_COUNT];
    private static double borderWidth;
    private static double borderHeight;
    private static double startDistance = 50.0;
    private static double leftBorderTranslationX;
    private static double rightBorderTranslationX;
    private static double leftBorderCrashLine;
    private static double rightBorderCrashLine;

    //Road lines
    private static final int LANE_MIN_FIRST_Y_TO_UP = -900;
    private static final int LANES_COUNT = 25;
    private static final double LANE_BREAK = 50.0;
    private static RoadLane[] lanes = new RoadLane[LANES_COUNT];
    private static double laneWidth;
    private static double laneHeight;
    private static double laneTranslationX;

    //Obstacle
    private static final int OBSTACLE_BREAK_FROM_ROAD_LINE = 20;
    private static final int OBSTACLE_BREAK_BETWEEN = 400;
    private static final int INIT_HOLE_WIDTH = 200;
    private static final int OBSTACLES_COUNT = 30;
    private static int obstacleWidth;
    private static int obstacleTxX;
    private Obstacle[] obstacles = new Obstacle[OBSTACLES_COUNT];

    //Board
    private static final int BOARD_MARGIN = 20;
    private Board board;

    //Menu
    private Menu menu;

    public GameArea() throws Exception {
        initAnimationTimer(this::performHorizontalMove);
        initAnimationTimer(this::performRotate);
        initAnimationTimer(this::performRoadBorders);
        initAnimationTimer(this::performLanes);
        initAnimationTimer(this::performObstacles);
        initAnimationTimer(this::gameLogic);
    }

    @FXML
    private void initialize() {

        calculations();
        initBorders();
        initLanes();
        initObstacles();
        initBoard();
        initMenu();

        //Initial move bolid
        BOLID.setTranslateX(BOLID_X);
        BOLID.setTranslateY(BOLID_Y);

        anchorPane.getChildren().add(BOLID);
        anchorPane.setOnMouseMoved(event -> {
            CURSOR_X = event.getSceneX();
            CURSOR_Y = event.getSceneY();
        });

        anchorPane.getChildren().add(menu);
        menu.getMenuModel().getNewGameButton().setOnMouseClicked(event -> {
            anchorPane.getChildren().remove(menu);
            gameRun = true;
        });

    }

    private void pause() {
        gameRun = !gameRun;
        menu.getMenuModel().getMenuLabel().setText("GAME OVER!");
        menu.getMenuModel().getNewGameButton().setOnMouseClicked(event -> {
            anchorPane.getChildren().remove(menu);
            startNewGame();
        });

        if (!anchorPane.getChildren().contains(menu))
            anchorPane.getChildren().add(menu);
    }

    private void startNewGame() {
        initObstacles();
        gameSpeed = 5.0;
        level = 1;
        levelSecondsInterval = 10;
        startTime = System.currentTimeMillis();
        gameRun = true;
    }

    private void calculations() {
        //Anchor game area - dimensions
        anchorPaneWidth = anchorPane.getPrefWidth();
        anchorPaneHeight = anchorPane.getPrefHeight();

        borderWidth = new RoadBorder().getComponentWidth();
        borderHeight = new RoadBorder().getComponentHeight();

        leftBorderTranslationX = startDistance;
        rightBorderTranslationX = anchorPaneWidth - startDistance - borderWidth;

        laneWidth = new RoadLane().getComponentWidth();
        laneHeight = new RoadLane().getComponentHeight();
        laneTranslationX = (anchorPaneWidth / 2.0) - (laneWidth / 2.0);

        leftBorderCrashLine = leftBorderTranslationX + borderWidth;
        rightBorderCrashLine = rightBorderTranslationX;

        obstacleTxX = Double.valueOf(leftBorderTranslationX).intValue() + Double.valueOf(borderWidth).intValue() + (OBSTACLE_BREAK_FROM_ROAD_LINE);
        obstacleWidth = Double.valueOf(anchorPaneWidth).intValue() - 2 * obstacleTxX;
    }

    private void initMenu() {
        menu = new Menu();
        menu.setTranslateX(300);
        menu.setTranslateY(320);
        menu.getMenuModel().getExitButton().setOnMouseClicked(event -> {
            try {
                Starter.getApplication().getStage().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initBoard() {
        board = new Board();
        board.setTranslateX(BOARD_MARGIN);
        board.setTranslateY(BOARD_MARGIN);

        anchorPane.getChildren().add(board);
    }

    private void initObstacles() {

        List<Node> toRemove = new ArrayList<>();
        for (Node n : anchorPane.getChildren()) {
            if (n instanceof Obstacle)
                toRemove.add(n);
        }

        anchorPane.getChildren().removeAll(toRemove);

        for (int i = 0; i < OBSTACLES_COUNT; i++) {
            final Obstacle obstacle = new Obstacle(INIT_HOLE_WIDTH, obstacleWidth, obstacleTxX);
            obstacle.setTranslateY(-i * (obstacle.getPrefHeight() + OBSTACLE_BREAK_BETWEEN));
            obstacles[i] = obstacle;
            anchorPane.getChildren().add(obstacles[i]);
        }
    }

    private void initLanes() {
        for (int i = 0; i < lanes.length; i++) {
            final RoadLane lane = new RoadLane();
            lane.setTranslateX(laneTranslationX);
            lane.setTranslateY((((laneHeight + LANE_BREAK) * i)) - 980);
            lanes[i] = lane;
        }

        Arrays.stream(lanes).forEach(roadLane -> anchorPane.getChildren().add(roadLane));

    }

    private void initBorders() {
        initBorder(bordersRight, rightBorderTranslationX);
        initBorder(bordersLeft, leftBorderTranslationX);

        Stream<RoadBorder> leftBorders = Arrays.stream(bordersLeft);
        Stream<RoadBorder> rightBorders = Arrays.stream(bordersRight);
        Stream<RoadBorder> border = Stream.concat(leftBorders, rightBorders);
        border.forEach(roadBorder -> anchorPane.getChildren().add(roadBorder));
    }

    private void initBorder(RoadBorder[] borders, double translation) {
        for (int i = 0; i < borders.length; i++) {
            final RoadBorder leftBorder = new RoadBorder();
            leftBorder.setTranslateX(translation);
            leftBorder.setTranslateY(((i) * borderHeight) - 1000);
            borders[i] = leftBorder;
        }
    }

    private AnchorPane bolid() throws Exception {
        return FXMLLoader.load(getClass().getResource("/fxml/game-bolid.fxml"));
    }

    private void performRotate() {

        double x = CURSOR_X;
        double y = CURSOR_Y;

        double xBolidTranslation = BOLID.getTranslateX() + (BOLID_WIDTH / 2);
        double xTrianglePosition = x - xBolidTranslation;
        double yTrianglePosition = BOLID_CENTER_Y - y;

        if (yTrianglePosition < 0) return;

        double tg = yTrianglePosition / Math.abs(xTrianglePosition);
        double angle;

        if (xTrianglePosition > 0) {
            angle = 90.0 - Math.toDegrees(Math.atan(tg));
            direction = 1;
        } else {
            angle = 360 - (90.0 - Math.toDegrees(Math.atan(tg)));
            direction = -1;
        }


        double angleBuffer = 90.0 - Math.toDegrees(Math.atan(tg));
        if (angle > 2.0) {
            this.angle = angleBuffer;
        } else {
            this.angle = 0.0;
        }

        BOLID.setRotate(angle);
    }

    private void performRoadBorders() {
        moveUpLast(bordersLeft);
        moveUpLast(bordersRight);
    }

    private void moveUpLast(RoadBorder[] borders) {

        //Get First and Last Y
        double firstY = 9999.0;
        double lastY = 0.0;
        RoadBorder lastRb = null;
        for (RoadBorder rb : borders) {
            if (rb.getTranslateY() < firstY)
                firstY = rb.getTranslateY();
            if (rb.getTranslateY() > lastY) {
                lastY = rb.getTranslateY();
                lastRb = rb;
            }
        }

        //If Y is higher than -900 move last up
        if (firstY > BORDER_MIN_FIRST_Y_TO_UP && lastRb != null) {
            lastRb.setTranslateY(firstY - borderHeight);
        }

        //Move all
        for (RoadBorder rb : borders)
            rb.setTranslateY(rb.getTranslateY() + gameSpeed + level);

    }

    private void performLanes() {
        //Get First and Last Y
        double firstY = 9999.0;
        double lastY = 0.0;
        RoadLane lastRl = null;
        for (RoadLane rl : lanes) {
            if (rl.getTranslateY() < firstY)
                firstY = rl.getTranslateY();
            if (rl.getTranslateY() > lastY) {
                lastY = rl.getTranslateY();
                lastRl = rl;
            }
        }

        //If Y is higher than -900 move last up
        if (firstY > LANE_MIN_FIRST_Y_TO_UP && lastRl != null) {
            lastRl.setTranslateY(firstY - laneHeight - LANE_BREAK);
        }

        //Move all
        for (RoadLane rl : lanes)
            rl.setTranslateY(rl.getTranslateY() + gameSpeed + level);
    }

    private void performObstacles() {

        double minY = 0.0;
        double maxY = 0.0;
        Obstacle obMax = null;

        for (Obstacle ob : obstacles) {
            double txY = ob.getTranslateY();
            if (minY > txY) minY = txY;
            if (maxY < txY) {
                maxY = txY;
                obMax = ob;
            }
        }

        if (maxY > anchorPaneHeight && obMax != null) {
            obMax.setTranslateY(minY - (obMax.getPrefHeight() + OBSTACLE_BREAK_BETWEEN));
        }

        for (Obstacle ob : obstacles)
            ob.setTranslateY(ob.getTranslateY() + gameSpeed + level);
    }

    private void initAnimationTimer(final Runnable runnable) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRun && runnable != null) runnable.run();
            }
        }.start();
    }

    private double getSpeedPortion() {

        //Angle portion
        double ap = angle / 90;

        //Calc
        double max = Math.pow(BASIS, MULTIPLIER);
        double calc = Math.pow((1.0 + ((max - 1.0) * ap)), (1.0 / MULTIPLIER));

        return calc;
    }

    private long timeoutForAngle() {
        double portion = (BASIS - 1) - (getSpeedPortion() - 1.0); //From 0 to basics
        //Timout percentage for portion
        double percentage = (portion * 100) / (BASIS - 1);
        double timeoutPortion = (percentage / 100.0) * MAX_TIMEOUT;
        return Double.valueOf(timeoutPortion).longValue();
    }

    private void performHorizontalMove() {
        if (direction == 0) return;
        long timeOccurred = System.currentTimeMillis() - lastUpdate;
        if (timeOccurred > timeoutForAngle() && angle != 0.0) {
            double i = BOLID.getTranslateX();
            if (direction > 0) i += move;
            else if (direction < 0) i -= move;
            BOLID.setTranslateX(i);
            lastUpdate = System.currentTimeMillis();
        }
    }

    private void gameLogic() {
        checkCrashBorders();
        checkCrashObstacles();

        long sec = (System.currentTimeMillis() - startTime) / 1000;

        int secondsToNextLvl = 0;
        for (int i = 1; i <= level; i++) {
            secondsToNextLvl += i * levelSecondsInterval;
        }

        if (sec > secondsToNextLvl)
            ++level;

        paintBoard(sec, level);
    }

    private void paintBoard(long sec, int level) {
        board.getBoardModel().getTimeLabel().setText(String.valueOf(sec));
        board.getBoardModel().getLevelLabel().setText(String.valueOf(level));
    }

    private void checkCrashBorders() {

        //Crash border calculations
        double startAngleTop = Math.toDegrees(Math.atan((BOLID_WIDTH / 2.0) / (BOLID_HEIGHT / 2.0)));
        double startAngleBottom = ((90 - startAngleTop) * 2.0) + startAngleTop;
        double originalAngle;
        switch (direction) {
            case -1:
                originalAngle = angle + 90;
                break;
            case 1:
                originalAngle = 90 - angle;
                break;
            default:
                originalAngle = 0.0;
        }

        double cornerRadius = Math.sqrt(Math.pow((BOLID_WIDTH / 2.0), 2.0) + Math.pow((BOLID_HEIGHT / 2.0), 2.0));
        double bolidCenter = BOLID.getTranslateX() + (BOLID_WIDTH / 2.0);

        //Check crash with left border
        double leftTopPointX = cornerRadius * Math.cos(Math.toRadians(originalAngle + startAngleTop));
        double leftBottomPointX = cornerRadius * Math.cos(Math.toRadians(originalAngle + startAngleBottom));
        double leftBolidTop = bolidCenter + leftTopPointX;
        double leftBolidBottom = bolidCenter + leftBottomPointX;

        if (leftBolidTop < leftBorderCrashLine) pause();
        if (leftBolidBottom < leftBorderCrashLine) pause();

        //Check crash with right border
        double rightTopPointX = cornerRadius * Math.cos(Math.toRadians(originalAngle - startAngleTop));
        double rightBottomPointX = cornerRadius * Math.cos(Math.toRadians(180 + startAngleTop + originalAngle));
        double rightBolidTop = bolidCenter + rightTopPointX;
        double rightBolidBottom = bolidCenter + rightBottomPointX;

        if (rightBolidTop > rightBorderCrashLine) pause();
        if (rightBolidBottom > rightBorderCrashLine) pause();

    }

    private void checkCrashObstacles() {

        //Crash border calculations
        double startAngleTop = Math.toDegrees(Math.atan((BOLID_WIDTH / 2.0) / (BOLID_HEIGHT / 2.0)));
        double startAngleBottom = ((90 - startAngleTop) * 2.0) + startAngleTop;
        double originalAngle;
        switch (direction) {
            case -1:
                originalAngle = angle + 90;
                break;
            case 1:
                originalAngle = 90 - angle;
                break;
            default:
                originalAngle = 0.0;
        }

        double cornerRadius = Math.sqrt(Math.pow((BOLID_WIDTH / 2.0), 2.0) + Math.pow((BOLID_HEIGHT / 2.0), 2.0));
        double bolidCenterX = BOLID.getTranslateX() + (BOLID_WIDTH / 2.0);
        double bolidCenterY = BOLID.getTranslateY() + (BOLID_HEIGHT / 2.0);

        //Check crash with left border
        double leftTopPointX = cornerRadius * Math.cos(Math.toRadians(originalAngle + startAngleTop));
        double leftTopPointY = cornerRadius * Math.sin(Math.toRadians(originalAngle + startAngleTop));
        double leftBottomPointX = cornerRadius * Math.cos(Math.toRadians(originalAngle + startAngleBottom));
        double leftBottomPointY = cornerRadius * Math.sin(Math.toRadians(originalAngle + startAngleBottom));

        double leftBolidTopX = bolidCenterX + leftTopPointX;
        double leftBolidTopY = bolidCenterY + leftTopPointY;
        double leftBolidBottomX = bolidCenterX + leftBottomPointX;
        double leftBolidBottomY = bolidCenterY + leftBottomPointY;

        //Check crash with right border
        double rightTopPointX = cornerRadius * Math.cos(Math.toRadians(originalAngle - startAngleTop));
        double rightTopPointY = cornerRadius * Math.sin(Math.toRadians(originalAngle - startAngleTop));
        double rightBottomPointX = cornerRadius * Math.cos(Math.toRadians(180 + startAngleTop + originalAngle));
        double rightBottomPointY = cornerRadius * Math.sin(Math.toRadians(180 + startAngleTop + originalAngle));

        double rightBolidTopX = bolidCenterX + rightTopPointX;
        double rightBolidTopY = bolidCenterY + rightTopPointY;
        double rightBolidBottomX = bolidCenterX + rightBottomPointX;
        double rightBolidBottomY = bolidCenterY + rightBottomPointY;

//        System.out.println("LT: (" + leftBolidTopX + "," + leftBolidTopY + ") LB: (" + leftBolidBottomX + "," + leftBolidBottomY
//                + ") RT: (" + rightBolidTopX + "," + rightBolidTopY + ") RB: (" + rightBolidBottomX + "," + rightBolidBottomY + ") ");

        Polygon polygon = Polygon.Builder()
                .addVertex(new Point(leftBolidTopX, leftBolidTopY))
                .addVertex(new Point(leftBolidBottomX, leftBolidBottomY))
                .addVertex(new Point(rightBolidBottomX, rightBolidBottomY))
                .addVertex(new Point(rightBolidTopX, rightBolidTopY))
                .build();

        for (Obstacle ob : obstacles)
            for (Point p : ob.getObstaclePoints())
                if (polygon.contains(p)) pause();

    }

}
