package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.scene.SubScene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GolfRangeScene extends SubScene {

    private enum GameState { IDLE, POWER_AIMING, DIRECTION_AIMING, SWINGING, FLYING, FINISHED }
    private GameState currentState = GameState.IDLE;

    private double swingTimer = 0;
    private double powerValue = 0;
    private double directionValue = 0;
    private boolean barIncreasing = true;

    private double ballVelocityY = 0;
    private double ballVelocityX = 0;

    private Group worldGroup;
    private Group uiGroup;
    private Group powerBarGroup;
    private Group directionBarGroup;
    private Rectangle powerBarFill;
    private Rectangle directionIndicator;
    private Text feedbackText;

    private Circle golfBall;
    private double startBallY;

    public GolfRangeScene() {
        worldGroup = new Group();
        uiGroup = new Group();
        getContentRoot().getChildren().addAll(worldGroup, uiGroup);

        buildWorld();
        buildUI();

        getContentRoot().setOnMouseClicked(e -> handleMouseClick());
    }

    private void buildWorld() {
        Rectangle background = new Rectangle(getAppWidth(), 10000, Color.web("#2e8b57"));
        background.setTranslateY(-9000);
        worldGroup.getChildren().add(background);

        for (int i = 50; i <= 1500; i += 50) {
            double yPos = getAppHeight() - 200 - (i * 10);
            Text marker = new Text(i + "m");
            marker.setFill(Color.WHITE); marker.setTranslateX(20); marker.setTranslateY(yPos);
            Rectangle line = new Rectangle(getAppWidth(), 1, Color.color(1, 1, 1, 0.2));
            line.setTranslateY(yPos);
            worldGroup.getChildren().addAll(line, marker);
        }

        Rectangle player = new Rectangle(30, 60, Color.BLUE);
        player.setTranslateX(getAppWidth() / 2.0 - 15);
        player.setTranslateY(getAppHeight() - 210);

        golfBall = new Circle(5, Color.WHITE);
        golfBall.setTranslateX(getAppWidth() / 2.0);
        startBallY = getAppHeight() - 150;
        golfBall.setTranslateY(startBallY);

        worldGroup.getChildren().addAll(player, golfBall);
    }

    private void buildUI() {
        Button backButton = new Button("Beenden");
        backButton.setTranslateX(10); backButton.setTranslateY(10);
        backButton.setOnAction(e -> getSceneService().popSubScene());

        Button resetButton = new Button("Nächster Versuch");
        resetButton.setTranslateX(120); resetButton.setTranslateY(10);
        resetButton.setVisible(false);
        resetButton.setOnAction(e -> resetGame(resetButton));

        feedbackText = new Text("Klicke für POWER");
        feedbackText.setFont(Font.font(22)); feedbackText.setFill(Color.YELLOW);
        feedbackText.setTranslateX(getAppWidth() / 2.0 - 100); feedbackText.setTranslateY(60);

        powerBarGroup = new Group();
        Rectangle pBg = new Rectangle(30, 200, Color.BLACK);
        pBg.setStroke(Color.WHITE);
        powerBarFill = new Rectangle(30, 0, Color.RED);
        powerBarFill.setTranslateY(200);
        powerBarGroup.getChildren().addAll(pBg, powerBarFill);
        powerBarGroup.setTranslateX(getAppWidth() - 60);
        powerBarGroup.setTranslateY(getAppHeight() - 300);
        powerBarGroup.setVisible(false);

        directionBarGroup = new Group();
        Rectangle dBg = new Rectangle(200, 20, Color.BLACK);
        dBg.setStroke(Color.WHITE);
        Rectangle dCenter = new Rectangle(10, 20, Color.LIMEGREEN);
        dCenter.setTranslateX(95);
        directionIndicator = new Rectangle(4, 30, Color.WHITE);
        directionIndicator.setTranslateY(-5);
        directionBarGroup.getChildren().addAll(dBg, dCenter, directionIndicator);
        directionBarGroup.setTranslateX(getAppWidth() / 2.0 - 100);
        directionBarGroup.setTranslateY(getAppHeight() - 60);
        directionBarGroup.setVisible(false);

        uiGroup.getChildren().addAll(backButton, resetButton, feedbackText, powerBarGroup, directionBarGroup);
    }

    private void handleMouseClick() {
        switch (currentState) {
            case IDLE -> {
                currentState = GameState.POWER_AIMING;
                powerValue = 0;
                barIncreasing = true;
                powerBarGroup.setVisible(true);
                feedbackText.setText("STOPPE POWER!");
            }
            case POWER_AIMING -> {
                currentState = GameState.DIRECTION_AIMING;
                directionValue = 0;
                barIncreasing = true;
                directionBarGroup.setVisible(true);
                feedbackText.setText("STOPPE RICHTUNG!");
            }
            case DIRECTION_AIMING -> {
                currentState = GameState.SWINGING;
                powerBarGroup.setVisible(false);
                directionBarGroup.setVisible(false);
                feedbackText.setText("SCHWUNG!");

                double powerPct = powerValue / 200.0;
                ballVelocityY = -(1200 * powerPct + 400);

                // --- EFFEKT: In der Mitte (100) mehr Power, am Rand weniger ---
                double distFromCenter = Math.abs(100 - directionValue); // 0 bis 100
                double sidePenalty = 1.0 - (distFromCenter / 150.0); // Verliert bis zu 66% Power am Rand
                ballVelocityY *= sidePenalty;

                ballVelocityX = (directionValue - 100) * 4.5;

                swingTimer = 0.3;
            }
        }
    }

    private void resetGame(Button btn) {
        currentState = GameState.IDLE;
        worldGroup.setTranslateY(0);
        golfBall.setTranslateY(startBallY);
        golfBall.setTranslateX(getAppWidth() / 2.0);
        golfBall.setScaleX(1); golfBall.setScaleY(1);
        btn.setVisible(false);
        feedbackText.setText("Klicke für POWER");
    }

    @Override
    public void onUpdate(double tpf) {
        if (currentState == GameState.POWER_AIMING) {
            updatePowerBar(tpf);
        } else if (currentState == GameState.DIRECTION_AIMING) {
            updateDirectionBar(tpf);
        } else if (currentState == GameState.SWINGING) {
            swingTimer -= tpf;
            if (swingTimer <= 0) currentState = GameState.FLYING;
        } else if (currentState == GameState.FLYING) {
            updateBallMovement(tpf);
        }
    }

    private void updatePowerBar(double tpf) {
        // --- SPRUNG-EFFEKT: Je höher powerValue, desto schneller die Änderung ---
        // Wir nehmen einen Basiswert und multiplizieren ihn mit dem Fortschritt
        double progress = (powerValue / 200.0);
        double speed = (200 + (600 * progress)) * tpf;

        if (barIncreasing) {
            powerValue += speed;
            if (powerValue >= 200) { powerValue = 200; barIncreasing = false; }
        } else {
            // Beim Runtergehen bleibt es schnell oder wird wieder langsamer?
            // Hier: Es bleibt "unberechenbar" schnell oben.
            powerValue -= speed;
            if (powerValue <= 0) { powerValue = 0; barIncreasing = true; }
        }
        powerBarFill.setHeight(powerValue);
        powerBarFill.setTranslateY(200 - powerValue);
    }

    private void updateDirectionBar(double tpf) {
        // Hier lassen wir den Zeiger normal laufen
        double speed = 250 * tpf;
        if (barIncreasing) {
            directionValue += speed;
            if (directionValue >= 200) barIncreasing = false;
        } else {
            directionValue -= speed;
            if (directionValue <= 0) barIncreasing = true;
        }
        directionIndicator.setTranslateX(directionValue);
    }

    private void updateBallMovement(double tpf) {
        golfBall.setTranslateY(golfBall.getTranslateY() + ballVelocityY * tpf);
        golfBall.setTranslateX(golfBall.getTranslateX() + ballVelocityX * tpf);

        double ballRadius = golfBall.getRadius();
        if (golfBall.getTranslateX() - ballRadius <= 0) {
            golfBall.setTranslateX(ballRadius);
            ballVelocityX = -ballVelocityX * 0.6;
        }
        if (golfBall.getTranslateX() + ballRadius >= getAppWidth()) {
            golfBall.setTranslateX(getAppWidth() - ballRadius);
            ballVelocityX = -ballVelocityX * 0.6;
        }

        ballVelocityY *= 0.993; // Noch weniger Reibung für Weltrekorde
        ballVelocityX *= 0.993;

        // Flug-Höhe Animation
        if (Math.abs(ballVelocityY) > 150) {
            golfBall.setScaleX(1.8); golfBall.setScaleY(1.8);
        } else {
            golfBall.setScaleX(1.0); golfBall.setScaleY(1.0);
        }

        double camY = -(golfBall.getTranslateY() - 400);
        if (camY > 0) worldGroup.setTranslateY(camY);

        if (Math.abs(ballVelocityY) < 15) {
            currentState = GameState.FINISHED;
            double dist = (startBallY - golfBall.getTranslateY()) / 10.0;
            feedbackText.setText(String.format("Distanz: %.1f m", dist));
            uiGroup.getChildren().get(1).setVisible(true);
        }
    }
}