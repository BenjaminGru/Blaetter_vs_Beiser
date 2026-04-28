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

    // Physik & Ball
    private double ballVelocityY = 0, ballVelocityX = 0, ballHeight = 0, ballVerticalVelocity = 0;
    private double powerValue = 0, directionValue = 0, heightValue = 0;
    private boolean barIncreasing = true;
    private double swingTimer = 0;

    // Progression & Stats
    private int coins = 0;
    private int powerLevel = 1, accuracyLevel = 1, coinLevel = 1;

    // UI Komponenten
    private Group worldGroup, uiGroup;
    private Rectangle powerBarFill, heightBarFill, directionIndicator;
    private Text feedbackText, coinDisplay, clubDisplay;
    private Button nextTryButton, shopButton;
    private Circle golfBall, ballShadow;
    private double startBallY;

    // Terrain & Zufall
    private List<TerrainZone> terrainZones = new ArrayList<>();
    private record TerrainZone(Rectangle shape, double friction, String type) {}
    private Random random = new Random();

    public GolfRangeScene() {
        worldGroup = new Group();
        uiGroup = new Group();
        getContentRoot().getChildren().addAll(worldGroup, uiGroup);

        startBallY = getAppHeight() - 150;
        initStaticBall();
        buildWorld();
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

        Rectangle roughBg = new Rectangle(getAppWidth(), 40000, Color.web("#0e3011"));
        roughBg.setTranslateY(-39000);
        worldGroup.getChildren().add(roughBg);

        double fairwayWidth = 600;
        addTerrain(getAppWidth() / 2.0 - (fairwayWidth / 2.0), -39000, fairwayWidth, 40000, Color.web("#388e3c"), 0.97, "Fairway");

        for (int i = 150; i <= 15000; i += 120) {
            if (random.nextDouble() > 0.4) continue;
            double yPos = getAppHeight() - 200 - (i * 10);
            double w = 60 + random.nextDouble() * 140;
            double h = 40 + random.nextDouble() * 80;
            double xPos = random.nextDouble() * (getAppWidth() - w);

            if (random.nextBoolean()) {
                addTerrain(xPos, yPos, w, h, Color.web("#1e88e5"), 0.0, "Water");
            } else {
                addTerrain(xPos, yPos, w, h, Color.web("#ffecb3"), 0.6, "Bunker");
            }
        }

        for (int i = 50; i <= 15000; i += 50) {
            double yPos = getAppHeight() - 200 - (i * 10);
            Rectangle line = new Rectangle(getAppWidth(), 1, Color.color(1, 1, 1, 0.15));
            line.setTranslateY(yPos);
            Text marker = new Text(i + "m"); marker.setFill(Color.LIGHTGRAY);
            marker.setTranslateX(10); marker.setTranslateY(yPos - 2);
            worldGroup.getChildren().addAll(line, marker);
        }

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

        shopButton = new Button("SHOP");
        shopButton.setTranslateX(getAppWidth() - 85); shopButton.setTranslateY(10);
        shopButton.setStyle("-fx-background-color: #ffd700; -fx-font-weight: bold;");
        shopButton.setOnAction(e -> openShopUI());

        coinDisplay = new Text("0 C"); coinDisplay.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        coinDisplay.setFill(Color.GOLD); coinDisplay.setTranslateX(getAppWidth() - 110); coinDisplay.setTranslateY(65);

        clubDisplay = new Text("CLUB: DRIVER"); clubDisplay.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        clubDisplay.setFill(Color.LIGHTBLUE); clubDisplay.setTranslateX(20); clubDisplay.setTranslateY(30);

        feedbackText = new Text("BEREIT?"); feedbackText.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        feedbackText.setFill(Color.WHITE); feedbackText.setWrappingWidth(getAppWidth());
        feedbackText.setTextAlignment(TextAlignment.CENTER); feedbackText.setTranslateY(110);

        nextTryButton = new Button("NÄCHSTER SCHLAG"); nextTryButton.setTranslateX(getAppWidth()/2.0 - 70);
        nextTryButton.setTranslateY(20); nextTryButton.setVisible(false); nextTryButton.setOnAction(e -> resetGame());

        setupBars();
        uiGroup.getChildren().addAll(coinDisplay, clubDisplay, feedbackText, nextTryButton, shopButton);
    }

    private void setupBars() {
        // Power Bar Fix: Clip sorgt dafür, dass nichts übersteht
        Rectangle pBg = new Rectangle(20, 300, Color.web("#222222"));
        pBg.setStroke(Color.WHITE); pBg.setStrokeWidth(2);

        powerBarFill = new Rectangle(20, 0, Color.web("#FF3333"));
        Group pBar = new Group(pBg, powerBarFill);
        pBar.setTranslateX(getAppWidth() - 35); pBar.setTranslateY(getAppHeight() - 350);
        // Clip anwenden
        Rectangle clipP = new Rectangle(20, 300);
        pBar.setClip(clipP);

        // Height Bar
        Rectangle hBg = new Rectangle(20, 300, Color.web("#222222"));
        hBg.setStroke(Color.WHITE); hBg.setStrokeWidth(2);
        heightBarFill = new Rectangle(20, 0, Color.web("#33CCFF"));
        Group hBar = new Group(hBg, heightBarFill);
        hBar.setTranslateX(getAppWidth() - 65); hBar.setTranslateY(getAppHeight() - 350);
        hBar.setClip(new Rectangle(20, 300));

        // Direction Indicator
        directionIndicator = new Rectangle(4, 25, Color.WHITE);
        Group dBar = new Group(new Rectangle(200, 15, Color.web("#222222")), directionIndicator);
        dBar.setTranslateX(getAppWidth()/2.0 - 100); dBar.setTranslateY(getAppHeight() - 35);

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
        double hPct = heightValue / 300.0;

        // DRIVER PHYSIK: Sehr viel Power, flacherer Winkel
        // Basis-Kraft erhöht, aber Skalierung durch Upgrades generft
        double basePower = 1300;
        double totalForce = (basePower * pPct + 300) * (1 + (powerLevel - 1) * 0.06);

        // Der Driver hat einen Loft von ca. 9° bis 15°. hPct steuert hier die Flugkurve.
        double angleDeg = 9 + (hPct * 20);
        double angleRad = Math.toRadians(angleDeg);

        ballVerticalVelocity = Math.sin(angleRad) * totalForce;
        ballVelocityY = -Math.cos(angleRad) * totalForce;

        double error = (directionValue - 100);
        ballVelocityX = error * (3.5 / (1 + (accuracyLevel * 0.4)));

        feedbackText.setText("DRIVER SHOT!"); feedbackText.setFill(Color.LIME);
        swingTimer = 0.15;
    }

    @Override
    public void onUpdate(double tpf) {
        if (currentState == GameState.POWER_AIMING) {
            powerValue += (barIncreasing ? 1300 : -1300) * tpf;
            if (powerValue >= 300) { powerValue = 300; barIncreasing = false; }
            if (powerValue <= 0) { powerValue = 0; barIncreasing = true; }
            powerBarFill.setHeight(powerValue);
            powerBarFill.setTranslateY(300 - powerValue);
        } else if (currentState == GameState.DIRECTION_AIMING) {
            directionValue += (barIncreasing ? 450 : -450) * tpf;
            if (directionValue >= 196) { directionValue = 196; barIncreasing = false; }
            if (directionValue <= 0) { directionValue = 0; barIncreasing = true; }
            directionIndicator.setTranslateX(directionValue);
        } else if (currentState == GameState.HEIGHT_AIMING) {
            heightValue += (barIncreasing ? 1100 : -1100) * tpf;
            if (heightValue >= 300) { heightValue = 300; barIncreasing = false; }
            if (heightValue <= 0) { heightValue = 0; barIncreasing = true; }
            heightBarFill.setHeight(heightValue);
            heightBarFill.setTranslateY(300 - heightValue);
        } else if (currentState == GameState.FLYING) updatePhysics(tpf);
        else if (currentState == GameState.SWINGING) { swingTimer -= tpf; if (swingTimer <= 0) currentState = GameState.FLYING; }
    }

    private void updatePhysics(double tpf) {
        ballHeight += ballVerticalVelocity * tpf;

        if (ballHeight > 0) {
            ballVerticalVelocity -= 800 * tpf; // Schwerkraft
            ballVelocityY *= 0.998; // Luftwiderstand
            ballVelocityX *= 0.998;
            golfBall.setScaleX(1 + ballHeight / 80.0);
            golfBall.setScaleY(1 + ballHeight / 80.0);
            golfBall.setTranslateY(ballShadow.getTranslateY() - ballHeight);
        } else {
            ballHeight = 0;
            // Bouncen
            if (Math.abs(ballVerticalVelocity) > 60) ballVerticalVelocity = -ballVerticalVelocity * 0.25;
            else ballVerticalVelocity = 0;
            golfBall.setTranslateY(ballShadow.getTranslateY());
        }

        // Rand-Kollision (Bounce)
        if (ballShadow.getTranslateX() < 10 || ballShadow.getTranslateX() > getAppWidth() - 10) {
            ballVelocityX *= -0.5;
            ballShadow.setTranslateX(ballShadow.getTranslateX() < 10 ? 11 : getAppWidth() - 11);
        }

        ballShadow.setTranslateY(ballShadow.getTranslateY() + ballVelocityY * tpf);
        ballShadow.setTranslateX(ballShadow.getTranslateX() + ballVelocityX * tpf);
        golfBall.setTranslateX(ballShadow.getTranslateX());

        double friction = (ballHeight > 0) ? 1.0 : 0.975;
        String currentTerrain = "Rough";

        if (ballHeight <= 0) {
            for (TerrainZone zone : terrainZones) {
                if (ballShadow.getTranslateX() >= zone.shape.getTranslateX() &&
                        ballShadow.getTranslateX() <= zone.shape.getTranslateX() + zone.shape.getWidth() &&
                        ballShadow.getTranslateY() >= zone.shape.getTranslateY() &&
                        ballShadow.getTranslateY() <= zone.shape.getTranslateY() + zone.shape.getHeight()) {

                    if (zone.type.equals("Water")) {
                        golfBall.setVisible(false);
                        ballVelocityX = 0; ballVelocityY = 0;
                        currentTerrain = "Water";
                    } else {
                        friction = zone.friction;
                        currentTerrain = zone.type;
                    }
                    break;
                }
            }
        }

        ballVelocityY *= friction;
        ballVelocityX *= friction;
        worldGroup.setTranslateY(Math.max(0, -(ballShadow.getTranslateY() - 400)));

        if (Math.abs(ballVelocityY) < 5 && Math.abs(ballVelocityX) < 5 && ballHeight <= 0) {
            finishStroke(currentTerrain);
        }
    }

    private void finishStroke(String terrain) {
        currentState = GameState.FINISHED;
        double dist = Math.max(0, (startBallY - ballShadow.getTranslateY()) / 10.0);
        int reward = (int)(dist * (0.2 * coinLevel));

        if (terrain.equals("Water")) {
            reward = 0; feedbackText.setText("WASSER! (0 C)"); feedbackText.setFill(Color.AQUA);
        } else if (terrain.equals("Bunker")) {
            feedbackText.setText(String.format("%.1f m (BUNKER)", dist)); feedbackText.setFill(Color.ORANGE);
        } else {
            feedbackText.setText(String.format("%.1f Meter (+%d C)", dist, reward)); feedbackText.setFill(Color.YELLOW);
        }

        coins += reward; coinDisplay.setText(coins + " C"); nextTryButton.setVisible(true);
    }

    private void openShopUI() {
        VBox layout = new VBox(10); layout.setStyle("-fx-background-color: rgba(0,0,0,0.9); -fx-padding: 20; -fx-alignment: center; -fx-border-color: gold; -fx-border-width: 2;");
        layout.setTranslateX(getAppWidth()/2.0 - 120); layout.setTranslateY(150);

        Button p = new Button("POWER (Lvl " + powerLevel + "): " + (powerLevel * 200));
        p.setOnAction(e -> { if(coins >= powerLevel*200){ coins-=powerLevel*200; powerLevel++; closeShop(layout); }});

        Button a = new Button("ACCURACY (Lvl " + accuracyLevel + "): " + (accuracyLevel * 150));
        a.setOnAction(e -> { if(coins >= accuracyLevel*150){ coins-=accuracyLevel*150; accuracyLevel++; closeShop(layout); }});

        Button c = new Button("INCOME (Lvl " + coinLevel + "): " + (coinLevel * 250));
        c.setOnAction(e -> { if(coins >= coinLevel*250){ coins-=coinLevel*250; coinLevel++; closeShop(layout); }});

        Button close = new Button("X"); close.setOnAction(e -> closeShop(layout));

        Text t = new Text("PRO-SHOP"); t.setFill(Color.GOLD); t.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        layout.getChildren().addAll(t, p, a, c, close);
        uiGroup.getChildren().add(layout); shopButton.setDisable(true);
    }

    private void closeShop(VBox layout) { uiGroup.getChildren().remove(layout); shopButton.setDisable(false); coinDisplay.setText(coins + " C"); }
    private void resetBallPosition() { ballShadow.setTranslateX(getAppWidth()/2.0); ballShadow.setTranslateY(startBallY); golfBall.setTranslateX(ballShadow.getTranslateX()); golfBall.setTranslateY(ballShadow.getTranslateY()); golfBall.setVisible(true); }

    private void resetGame() {
        currentState = GameState.IDLE;
        ballVelocityX = 0; ballVelocityY = 0; ballHeight = 0; ballVerticalVelocity = 0;
        powerValue = 0; directionValue = 0; heightValue = 0;
        buildWorld(); resetBallPosition();
        nextTryButton.setVisible(false); shopButton.setVisible(true);
        feedbackText.setText("NEXT ROUND"); feedbackText.setFill(Color.WHITE);
    }
}