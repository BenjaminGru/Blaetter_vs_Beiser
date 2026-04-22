package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.scene.SubScene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GolfRangeScene extends SubScene {

    private enum GameState { IDLE, POWER_AIMING, DIRECTION_AIMING, HEIGHT_AIMING, SWINGING, FLYING, FINISHED }
    private GameState currentState = GameState.IDLE;

    private double ballVelocityY = 0, ballVelocityX = 0, ballHeight = 0, ballVerticalVelocity = 0;
    private double powerValue = 0, directionValue = 0, heightValue = 0;
    private boolean barIncreasing = true;
    private double swingTimer = 0;

    private int coins = 0;
    private int powerLevel = 1, accuracyLevel = 1, coinLevel = 1;

    private Group worldGroup, uiGroup;
    private Rectangle powerBarFill, heightBarFill, directionIndicator;
    private Text feedbackText, coinDisplay;
    private Button nextTryButton, shopButton;
    private Circle golfBall, ballShadow;
    private double startBallY;

    private List<TerrainZone> terrainZones = new ArrayList<>();
    private record TerrainZone(Rectangle shape, double friction, String type) {}
    private Random random = new Random();

    public GolfRangeScene() {
        worldGroup = new Group();
        uiGroup = new Group();
        getContentRoot().getChildren().addAll(worldGroup, uiGroup);

        startBallY = getAppHeight() - 150;
        initStaticBall(); // Ball einmalig erstellen
        buildWorld();     // Hindernisse generieren
        buildUI();

        getContentRoot().setOnMousePressed(e -> handleMouseClick());
    }

    private void initStaticBall() {
        ballShadow = new Circle(5, Color.color(0, 0, 0, 0.4));
        golfBall = new Circle(6, Color.WHITE);
        golfBall.setStroke(Color.BLACK);
        resetBallPosition();
    }

    private void buildWorld() {
        worldGroup.getChildren().clear();
        terrainZones.clear();

        // 1. Hintergrund (Rough)
        Rectangle roughBg = new Rectangle(getAppWidth(), 40000, Color.web("#0e3011"));
        roughBg.setTranslateY(-39000);
        worldGroup.getChildren().add(roughBg);

        // 2. Fairway
        double fairwayWidth = 600;
        addTerrain(getAppWidth() / 2.0 - (fairwayWidth / 2.0), -39000, fairwayWidth, 40000, Color.web("#388e3c"), 0.996, "Fairway");

        // 3. Zufällige Hindernisse (Bunker & Teiche)
        for (int i = 150; i <= 15000; i += 120) {
            if (random.nextDouble() > 0.4) continue; // Nicht jede 100m ein Hindernis (Balancing)

            double yPos = getAppHeight() - 200 - (i * 10);
            double w = 40 + random.nextDouble() * 160;
            double h = 30 + random.nextDouble() * 100;
            // Spawnen überall auf dem Bildschirmbereich
            double xPos = random.nextDouble() * (getAppWidth() - w);

            if (random.nextBoolean()) {
                addTerrain(xPos, yPos, w, h, Color.web("#1e88e5"), 0.92, "Water");
            } else {
                addTerrain(xPos, yPos, w, h, Color.web("#ffecb3"), 0.72, "Bunker");
            }
        }

        // 4. Meter Marker
        for (int i = 50; i <= 15000; i += 50) {
            double yPos = getAppHeight() - 200 - (i * 10);
            Rectangle line = new Rectangle(getAppWidth(), 1, Color.color(1, 1, 1, 0.1));
            line.setTranslateY(yPos);
            Text marker = new Text(i + "m"); marker.setFill(Color.LIGHTGRAY);
            marker.setTranslateX(10); marker.setTranslateY(yPos - 2);
            worldGroup.getChildren().addAll(line, marker);
        }

        // Ball wieder hinzufügen, da worldGroup.clear() ihn gelöscht hätte
        worldGroup.getChildren().addAll(ballShadow, golfBall);
    }

    private void addTerrain(double x, double y, double w, double h, Color color, double friction, String type) {
        Rectangle rect = new Rectangle(w, h, color);
        rect.setTranslateX(x); rect.setTranslateY(y);
        worldGroup.getChildren().add(rect);
        terrainZones.add(new TerrainZone(rect, friction, type));
    }

    private void buildUI() {
        uiGroup.getChildren().clear();

        shopButton = new Button("SHOP"); shopButton.setTranslateX(getAppWidth() - 85); shopButton.setTranslateY(10);
        shopButton.setStyle("-fx-background-color: #ffd700; -fx-font-weight: bold;");
        shopButton.setOnAction(e -> openShopUI());

        coinDisplay = new Text("0 C"); coinDisplay.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        coinDisplay.setFill(Color.GOLD); coinDisplay.setTranslateX(getAppWidth() - 110); coinDisplay.setTranslateY(65);

        feedbackText = new Text("BEREIT?"); feedbackText.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        feedbackText.setFill(Color.RED); feedbackText.setWrappingWidth(getAppWidth());
        feedbackText.setTextAlignment(TextAlignment.CENTER); feedbackText.setTranslateY(110);

        nextTryButton = new Button("NÄCHSTER SCHLAG"); nextTryButton.setTranslateX(getAppWidth()/2.0 - 70);
        nextTryButton.setTranslateY(20); nextTryButton.setVisible(false); nextTryButton.setOnAction(e -> resetGame());

        setupBars();
        uiGroup.getChildren().addAll(coinDisplay, feedbackText, nextTryButton, shopButton);
    }

    private void setupBars() {
        powerBarFill = new Rectangle(20, 0, Color.web("#FF3333"));
        Rectangle pBg = new Rectangle(20, 300, Color.web("#222222")); pBg.setStroke(Color.WHITE);
        Rectangle pPerf = new Rectangle(20, 50, Color.web("#33FF33", 0.4)); pPerf.setTranslateY(10);
        Group pBar = new Group(pBg, pPerf, powerBarFill); pBar.setClip(new Rectangle(20, 300));
        pBar.setTranslateX(getAppWidth() - 30); pBar.setTranslateY(getAppHeight() - 350);

        heightBarFill = new Rectangle(20, 0, Color.web("#33CCFF"));
        Rectangle hBg = new Rectangle(20, 300, Color.web("#222222")); hBg.setStroke(Color.WHITE);
        Rectangle hPerf = new Rectangle(20, 60, Color.web("#33FF33", 0.4)); hPerf.setTranslateY(120);
        Group hBar = new Group(hBg, hPerf, heightBarFill); hBar.setClip(new Rectangle(20, 300));
        hBar.setTranslateX(getAppWidth() - 60); hBar.setTranslateY(getAppHeight() - 350);

        directionIndicator = new Rectangle(4, 25, Color.WHITE);
        Group dBar = new Group(new Rectangle(200, 15, Color.web("#222222")), directionIndicator);
        dBar.setTranslateX(getAppWidth()/2.0 - 100); dBar.setTranslateY(getAppHeight() - 30);

        uiGroup.getChildren().addAll(pBar, hBar, dBar);
    }

    private void handleMouseClick() {
        if (nextTryButton.isVisible() || currentState == GameState.FINISHED) return;
        if (currentState == GameState.IDLE) { currentState = GameState.POWER_AIMING; barIncreasing = true; shopButton.setVisible(false); }
        else if (currentState == GameState.POWER_AIMING) { currentState = GameState.DIRECTION_AIMING; barIncreasing = true; }
        else if (currentState == GameState.DIRECTION_AIMING) { currentState = GameState.HEIGHT_AIMING; barIncreasing = true; }
        else if (currentState == GameState.HEIGHT_AIMING) executeSwing();
    }

    private void executeSwing() {
        currentState = GameState.SWINGING;
        double pPct = powerValue / 300.0;
        double hPct = heightValue / 300.0; // 0.0 = flach, 1.0 = senkrecht

        // Die totale Kraft basierend auf dem Power-Balken und Upgrades
        double totalForce = (1000 * pPct + 300) * (1 + (powerLevel - 1) * 0.25);

        // --- PHYSIK-KORREKTUR: ENERGIE-AUFTEILUNG ---
        // Wir nutzen Sinus/Cosinus (vereinfacht), um die Kraft zu verteilen:
        // Viel hPct -> Viel VerticalVelocity, wenig VelocityY (Vorwärts)

        ballVerticalVelocity = hPct * 800; // Kraft nach oben

        // Vorwärtsdrang wird reduziert, je höher man schlägt
        // hPct von 1.0 (ganz oben) reduziert die Vorwärtskraft um bis zu 80%
        double forwardEfficiency = 1.0 - (hPct * 0.8);
        ballVelocityY = -totalForce * forwardEfficiency;

        // Richtung
        ballVelocityX = (directionValue - 100) * (4.0 / (accuracyLevel + 1));

        // Feedback Texte
        if (hPct > 0.8) {
            feedbackText.setText("HOHER LOB (Wenig Weite)");
            feedbackText.setFill(Color.AQUA);
        } else if (hPct < 0.2) {
            feedbackText.setText("FLACHSCHUSS (Viel Roll)");
            feedbackText.setFill(Color.LIGHTGREEN);
        } else {
            feedbackText.setText("PERFEKTER PITCH");
            feedbackText.setFill(Color.LIME);
        }

        swingTimer = 0.1;
    }

    @Override
    public void onUpdate(double tpf) {
        if (currentState == GameState.POWER_AIMING) {
            powerValue += (barIncreasing ? 1300 : -1300) * tpf;
            if (powerValue >= 300 || powerValue <= 0) barIncreasing = !barIncreasing;
            powerBarFill.setHeight(powerValue); powerBarFill.setTranslateY(300 - powerValue);
        } else if (currentState == GameState.DIRECTION_AIMING) {
            directionValue += (barIncreasing ? 450 : -450) * tpf;
            if (directionValue >= 200 || directionValue <= 0) barIncreasing = !barIncreasing;
            directionIndicator.setTranslateX(directionValue);
        } else if (currentState == GameState.HEIGHT_AIMING) {
            heightValue += (barIncreasing ? 1100 : -1100) * tpf;
            if (heightValue >= 300 || heightValue <= 0) barIncreasing = !barIncreasing;
            heightBarFill.setHeight(heightValue); heightBarFill.setTranslateY(300 - heightValue);
        } else if (currentState == GameState.FLYING) updatePhysics(tpf);
        else if (currentState == GameState.SWINGING) { swingTimer -= tpf; if (swingTimer <= 0) currentState = GameState.FLYING; }
    }

    private void updatePhysics(double tpf) {
        ballHeight += ballVerticalVelocity * tpf;
        if (ballHeight > 0) {
            ballVerticalVelocity -= 750 * tpf;
            golfBall.setScaleX(1 + ballHeight / 60.0); golfBall.setScaleY(1 + ballHeight / 60.0);
            golfBall.setTranslateY(ballShadow.getTranslateY() - ballHeight);
        } else {
            ballHeight = 0; ballVerticalVelocity = 0;
            golfBall.setScaleX(1); golfBall.setTranslateY(ballShadow.getTranslateY());
        }

        ballShadow.setTranslateY(ballShadow.getTranslateY() + ballVelocityY * tpf);
        ballShadow.setTranslateX(ballShadow.getTranslateX() + ballVelocityX * tpf);
        golfBall.setTranslateX(ballShadow.getTranslateX());

        double friction = (ballHeight > 0) ? 0.998 : 0.965;
        String terrain = "Rough";

        if (ballHeight <= 0) {
            for (TerrainZone zone : terrainZones) {
                if (ballShadow.getTranslateX() >= zone.shape.getTranslateX() &&
                        ballShadow.getTranslateX() <= zone.shape.getTranslateX() + zone.shape.getWidth() &&
                        ballShadow.getTranslateY() >= zone.shape.getTranslateY() &&
                        ballShadow.getTranslateY() <= zone.shape.getTranslateY() + zone.shape.getHeight()) {
                    friction *= zone.friction; terrain = zone.type; break;
                }
            }
        }

        ballVelocityY *= friction; ballVelocityX *= friction;
        worldGroup.setTranslateY(Math.max(0, -(ballShadow.getTranslateY() - 400)));

        if (Math.abs(ballVelocityY) < 5 && Math.abs(ballVelocityX) < 5 && ballHeight <= 0) {
            finishStroke(terrain);
        }
    }

    private void finishStroke(String terrain) {
        currentState = GameState.FINISHED;
        double dist = Math.max(0, (startBallY - ballShadow.getTranslateY()) / 10.0);
        int reward = (int)(dist * (0.2 * coinLevel));

        if (terrain.equals("Water")) {
            reward = -50; feedbackText.setText("WASSER! -50 C"); feedbackText.setFill(Color.AQUA);
        } else if (terrain.equals("Bunker")) {
            feedbackText.setText(String.format("%.1f m (BUNKER!)", dist)); feedbackText.setFill(Color.ORANGE);
        } else {
            feedbackText.setText(String.format("%.1f m (+%d C)", dist, reward)); feedbackText.setFill(Color.YELLOW);
        }

        coins += reward; coinDisplay.setText(coins + " C"); nextTryButton.setVisible(true);
    }

    private void openShopUI() {
        VBox layout = new VBox(10); layout.setStyle("-fx-background-color: rgba(0,0,0,0.95); -fx-padding: 20; -fx-alignment: center; -fx-border-color: gold;");
        layout.setTranslateX(getAppWidth()/2.0 - 110); layout.setTranslateY(120);

        Button p = new Button("POWER (Lvl " + powerLevel + "): " + (powerLevel * 100));
        p.setOnAction(e -> { if(coins >= powerLevel*100){ coins-=powerLevel*100; powerLevel++; closeShop(layout); }});

        Button a = new Button("GENAUIGKEIT (Lvl " + accuracyLevel + "): " + (accuracyLevel * 80));
        a.setOnAction(e -> { if(coins >= accuracyLevel*80){ coins-=accuracyLevel*80; accuracyLevel++; closeShop(layout); }});

        Button c = new Button("GELD+ (Lvl " + coinLevel + "): " + (coinLevel * 150));
        c.setOnAction(e -> { if(coins >= coinLevel*150){ coins-=coinLevel*150; coinLevel++; closeShop(layout); }});

        Button close = new Button("X"); close.setOnAction(e -> closeShop(layout));
        layout.getChildren().addAll(new Text("PRO-SHOP"), p, a, c, close);
        uiGroup.getChildren().add(layout); shopButton.setDisable(true);
    }

    private void closeShop(VBox layout) { uiGroup.getChildren().remove(layout); shopButton.setDisable(false); coinDisplay.setText(coins + " C"); }
    private void resetBallPosition() { ballShadow.setTranslateX(getAppWidth()/2.0); ballShadow.setTranslateY(startBallY); golfBall.setTranslateX(ballShadow.getTranslateX()); golfBall.setTranslateY(ballShadow.getTranslateY()); }

    private void resetGame() {
        currentState = GameState.IDLE;
        ballVelocityX = 0; ballVelocityY = 0; ballHeight = 0; ballVerticalVelocity = 0;
        powerValue = 0; directionValue = 0; heightValue = 0;

        buildWorld(); // Hindernisse neu generieren
        resetBallPosition();

        nextTryButton.setVisible(false); shopButton.setVisible(true);
        feedbackText.setText("BEREIT?"); feedbackText.setFill(Color.RED);
    }
}