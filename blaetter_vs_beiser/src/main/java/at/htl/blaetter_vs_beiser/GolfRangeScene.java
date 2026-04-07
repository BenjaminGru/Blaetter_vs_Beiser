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

    // --- State Machine ---
    private enum GameState { IDLE, AIMING, SWINGING, FLYING, FINISHED }
    private GameState currentState = GameState.IDLE;

    // --- Logik Variablen ---
    private String selectedClub = "Driver";
    private double swingTimer = 0;
    private double powerValue = 0;
    private boolean powerIncreasing = true;
    private double ballVelocityY = 0; // Wie schnell der Ball nach "vorne" (oben) fliegt
    private double ballVelocityX = 0; // Ob der Ball nach links/rechts abdriftet

    // --- Szene & UI ---
    private Group worldGroup; // Bewegt sich (Kamera-Effekt)
    private Group uiGroup;    // Bleibt starr auf dem Bildschirm
    private Group powerBarGroup;
    private Rectangle powerBarFill;
    private Text feedbackText;

    // --- Spielobjekte ---
    private Rectangle playerSprite; // Später deine AnimatedTexture
    private Circle golfBall;
    private double startBallY; // Um zu wissen, wo der Abschlag war

    public GolfRangeScene() {
        worldGroup = new Group();
        uiGroup = new Group();
        getContentRoot().getChildren().addAll(worldGroup, uiGroup);

        buildWorld();
        buildUI();

        // Klick-Logik für den ganzen Bildschirm
        getContentRoot().setOnMouseClicked(e -> handleMouseClick());
    }

    private void buildWorld() {
        // 1. Eine sehr lange Wiese (4000 Pixel lang, Start ist ganz unten)
        Rectangle background = new Rectangle(getAppWidth(), 4000, Color.web("#2e8b57"));
        background.setTranslateY(-3000); // Verschieben, damit wir unten starten
        worldGroup.getChildren().add(background);

        // Meter-Markierungen alle 50 Meter
        for (int i = 50; i <= 350; i += 50) {
            double yPos = getAppHeight() - 200 - (i * 10); // 10 Pixel pro Meter als Maßstab

            Text leftMarker = new Text(i + "m");
            leftMarker.setFill(Color.WHITE); leftMarker.setTranslateX(50); leftMarker.setTranslateY(yPos);

            Text rightMarker = new Text(i + "m");
            rightMarker.setFill(Color.WHITE); rightMarker.setTranslateX(getAppWidth() - 80); rightMarker.setTranslateY(yPos);

            // Eine feine Linie quer über den Platz
            Rectangle line = new Rectangle(getAppWidth(), 2, Color.color(1, 1, 1, 0.3));
            line.setTranslateY(yPos);

            worldGroup.getChildren().addAll(line, leftMarker, rightMarker);
        }

        // 2. Der Spieler
        playerSprite = new Rectangle(40, 80, Color.BLUE);
        playerSprite.setTranslateX(getAppWidth() / 2.0 - 40);
        playerSprite.setTranslateY(getAppHeight() - 200);

        // 3. Der Ball
        golfBall = new Circle(5, Color.WHITE);
        golfBall.setTranslateX(getAppWidth() / 2.0 + 10);
        startBallY = getAppHeight() - 150;
        golfBall.setTranslateY(startBallY);

        worldGroup.getChildren().addAll(playerSprite, golfBall);
    }

    private void buildUI() {
        // Zurück-Button
        Button backButton = new Button("Zurück zu PvZ");
        backButton.setTranslateX(10); backButton.setTranslateY(10);
        backButton.setOnAction(e -> getSceneService().popSubScene());

        // Reset-Button (um neuen Ball zu schlagen)
        Button resetButton = new Button("Neuer Ball");
        resetButton.setTranslateX(120); resetButton.setTranslateY(10);
        resetButton.setVisible(false); // Erst am Ende sichtbar
        resetButton.setOnAction(e -> resetGame(resetButton));

        // Feedback Text
        feedbackText = new Text("Klicke, um den Schwung zu starten!");
        feedbackText.setFont(Font.font(20)); feedbackText.setFill(Color.YELLOW);
        feedbackText.setTranslateX(getAppWidth() / 2.0 - 180); feedbackText.setTranslateY(80);

        // --- SCHWUNG-LEISTE ---
        powerBarGroup = new Group();
        Rectangle powerBarBg = new Rectangle(300, 30, Color.BLACK);
        powerBarBg.setStroke(Color.WHITE);
        powerBarFill = new Rectangle(0, 30, Color.RED);
        Rectangle perfectZone = new Rectangle(20, 30, Color.LIMEGREEN);
        perfectZone.setTranslateX(140);
        powerBarGroup.getChildren().addAll(powerBarBg, perfectZone, powerBarFill);
        powerBarGroup.setTranslateX(getAppWidth() / 2.0 - 150);
        powerBarGroup.setTranslateY(getAppHeight() - 80);
        powerBarGroup.setVisible(false);

        uiGroup.getChildren().addAll(backButton, resetButton, feedbackText, powerBarGroup);
    }

    private void handleMouseClick() {
        if (currentState == GameState.IDLE) {
            currentState = GameState.AIMING;
            powerValue = 0; powerIncreasing = true;
            powerBarGroup.setVisible(true);
            feedbackText.setText("Klicke nochmal im grünen Bereich!");

        } else if (currentState == GameState.AIMING) {
            currentState = GameState.SWINGING;
            powerBarGroup.setVisible(false);

            // Berechnung der Schuss-Qualität
            double accuracy = Math.abs(150 - powerValue); // 0 ist perfekt
            double powerPercentage = powerValue / 300.0; // 0.0 bis 1.0

            // Flugbahn berechnen
            ballVelocityY = - (500 * powerPercentage + 300); // Basis-Kraft + Extra

            // Wenn nicht perfekt getroffen, fliegt er nach links oder rechts!
            if (powerValue < 140) ballVelocityX = - (accuracy * 2); // Hook
            else if (powerValue > 160) ballVelocityX = (accuracy * 2);  // Slice
            else ballVelocityX = 0; // Geradeaus

            feedbackText.setText("Schwung!");

            feedbackText.setText("Schwung!");

            // FIX: Wir setzen unseren eigenen Timer auf 0.5 Sekunden
            currentState = GameState.SWINGING;
            swingTimer = 0.5;
        }
    }

    private void resetGame(Button btn) {
        currentState = GameState.IDLE;
        worldGroup.setTranslateY(0); // Kamera zurück
        golfBall.setTranslateY(startBallY);
        golfBall.setTranslateX(getAppWidth() / 2.0 + 10);
        golfBall.setScaleX(1); golfBall.setScaleY(1); // Größe zurücksetzen
        btn.setVisible(false);
        feedbackText.setText("Klicke, um den Schwung zu starten!");
    }

    @Override
    public void onUpdate(double tpf) {
        if (currentState == GameState.AIMING) {
            // Powerbar wackeln lassen
            double speed = 300 * tpf;
            if (powerIncreasing) {
                powerValue += speed;
                if (powerValue >= 300) { powerValue = 300; powerIncreasing = false; }
            } else {
                powerValue -= speed;
                if (powerValue <= 0) { powerValue = 0; powerIncreasing = true; }
            }
            powerBarFill.setWidth(powerValue);
            if(Math.abs(150 - powerValue) < 15) powerBarFill.setFill(Color.LIMEGREEN);
            else powerBarFill.setFill(Color.RED);

        } else if (currentState == GameState.FLYING) {
            // Ball bewegen
            golfBall.setTranslateY(golfBall.getTranslateY() + ballVelocityY * tpf);
            golfBall.setTranslateX(golfBall.getTranslateX() + ballVelocityX * tpf);

            // "3D-Effekt": Ball wird beim Fliegen kurz größer, dann kleiner
            golfBall.setScaleX(golfBall.getScaleX() * 0.995);
            golfBall.setScaleY(golfBall.getScaleY() * 0.995);

            // Reibung (Ball wird langsamer)
            ballVelocityY *= 0.98;
            ballVelocityX *= 0.98;

            // Kamera-Verfolgung (Die "Drohne")
            // Wir verschieben die ganze Welt so, dass der Ball bei Y = 300 auf dem Bildschirm bleibt
            double cameraTargetY = -(golfBall.getTranslateY() - 300);

            // Kamera darf aber nicht weiter nach unten als der Startpunkt
            if (cameraTargetY > 0) {
                worldGroup.setTranslateY(cameraTargetY);
            }

            // Stoppen, wenn der Ball fast keine Geschwindigkeit mehr hat
            if (Math.abs(ballVelocityY) < 10) {
                currentState = GameState.FINISHED;
                double distance = (startBallY - golfBall.getTranslateY()) / 10.0;
                feedbackText.setText(String.format("Gelandet! Distanz: %.1f Meter", distance));

                // Reset Button anzeigen (das 2. Element in der uiGroup)
                uiGroup.getChildren().get(1).setVisible(true);
            }
        }
    }
}