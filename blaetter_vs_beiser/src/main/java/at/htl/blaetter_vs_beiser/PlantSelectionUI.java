package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlantSelectionUI extends HBox {

    private PlantManager plantManager;
    private Pane currentSelectionHighlight = null; // Speichert das Pane mit den L-Ecken

    // --- KONSTANTEN FÜR DEN L-ECKEN-LOOK ---
    private static final double L_THICKNESS = 6.0;
    private static final double L_LENGTH = 20.0;
    private static final double L_ARC = 10.0;
    private static final Color L_COLOR = Color.YELLOW;
    private static Text sunTextNode;


    public PlantSelectionUI(PlantManager plantManager) {
        this.plantManager = plantManager;


        setSpacing(10);
        setPadding(new Insets(0.1));
        setAlignment(Pos.TOP_LEFT);

        HBox brownBox = new HBox(20);
        brownBox.setAlignment(Pos.CENTER_LEFT);
        brownBox.setPadding(new Insets(10));
        brownBox.setStyle("-fx-background-color: #8B4513; -fx-border-color: #5C2E0B; -fx-border-width: 3;");

        // 3. Sonnen und Pflanzen kommen in den BRAUNEN Kasten
        brownBox.getChildren().add(createSunCounter());

        HBox cardsBox = new HBox(5);
        cardsBox.getChildren().addAll(
                createPlantCard("Bohnenschießer", 100, FXGL.image("Plants/Peashooter.png")),
                createPlantCard("Sonnenblume", 50, FXGL.image("Plants/Sunflower.png")),
                createPlantCard("Walnuss", 50, FXGL.image("Plants/Wallnut.png")),
                createPlantCard("Kartoffelmine", 25, FXGL.image("Plants/Potatomine.png")),
                createPlantCard("Kirschgranate", 150, FXGL.image("Plants/Cherrybomb.png")),
                createPlantCard("Eisbohnenschießer", 200, FXGL.image("Plants/Snowpeashooter.png"))
        );
        brownBox.getChildren().add(cardsBox);




        // Wir laden das Bild
        Image shovelImg = FXGL.image("Shovel.png");
        ImageView shovelView = new ImageView(shovelImg);


        shovelView.setFitWidth(40);
        shovelView.setFitHeight(60);
        shovelView.setScaleX(1.8);
        shovelView.setScaleY(1.8);
        shovelView.setTranslateY(18);
        shovelView.setPreserveRatio(true);



        final String sName = "Schaufel";
        final int sCost = 0;

        shovelView.setOnMousePressed(e -> {

            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;


            plantManager.startDrag(sName, sCost, getInput().getMouseXUI(), getInput().getMouseYUI(), shovelImg);
        });

        shovelView.setOnMouseDragged(e -> {
            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;
            plantManager.updateDrag(getInput().getMouseXUI(), getInput().getMouseYUI());
        });

        shovelView.setOnMouseReleased(e -> {
            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;
            plantManager.endDrag(getInput().getMouseXUI(), getInput().getMouseYUI());
        });

        getChildren().addAll(brownBox, shovelView);
    }

    private VBox createSunCounter() {
        VBox sunBox = new VBox(5);
        sunBox.setAlignment(Pos.CENTER);


        ImageView sunIcon = new ImageView(FXGL.image("Sun.png"));
        sunIcon.setFitWidth(40);
        sunIcon.setFitHeight(40);

        sunIcon.setPreserveRatio(true);

        sunTextNode = getUIFactoryService().newText("", Color.WHITE, 18);
        sunTextNode.textProperty().bind(getip("sun").asString());

        sunBox.getChildren().addAll(sunIcon, sunTextNode);
        return sunBox;
    }



    // Parameter von colorHint auf plantImage umbenannt für mehr Klarheit
    private Node createPlantCard(String plantName, int cost, Image plantImage) {

        // 1. Die eigentliche Pflanzenkarte (Inhalt)
        VBox cardContent = new VBox(2);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(5));
        cardContent.setStyle("-fx-background-color: #DDDDDD;");

        // ImageView anstelle von Rectangle für die Pflanze ---
        ImageView plantIcon = new ImageView(plantImage);
        plantIcon.setFitWidth(40);
        plantIcon.setFitHeight(40);
        plantIcon.setPreserveRatio(true);

        Text costText = getUIFactoryService().newText(cost + " ☀", Color.BLACK, 14);
        cardContent.getChildren().addAll(plantIcon, costText);

        // 2. Das Highlight-Pane für die Ecken
        Pane highlightPane = new Pane();
        highlightPane.setMouseTransparent(true);
        highlightPane.setVisible(false);

        // --- Top Left L-Ecke ---
        Rectangle tl_H = createLPart(L_LENGTH, L_THICKNESS);
        tl_H.layoutXProperty().bind(cardContent.layoutXProperty());
        tl_H.layoutYProperty().bind(cardContent.layoutYProperty());

        Rectangle tl_V = createLPart(L_THICKNESS, L_LENGTH);
        tl_V.layoutXProperty().bind(cardContent.layoutXProperty());
        tl_V.layoutYProperty().bind(cardContent.layoutYProperty());

        // --- Top Right L-Ecke ---
        Rectangle tr_H = createLPart(L_LENGTH, L_THICKNESS);
        tr_H.layoutXProperty().bind(cardContent.layoutXProperty().add(cardContent.widthProperty()).subtract(L_LENGTH));
        tr_H.layoutYProperty().bind(cardContent.layoutYProperty());

        Rectangle tr_V = createLPart(L_THICKNESS, L_LENGTH);
        tr_V.layoutXProperty().bind(cardContent.layoutXProperty().add(cardContent.widthProperty()).subtract(L_THICKNESS));
        tr_V.layoutYProperty().bind(cardContent.layoutYProperty());

        // --- Bottom Left L-Ecke ---
        Rectangle bl_H = createLPart(L_LENGTH, L_THICKNESS);
        bl_H.layoutXProperty().bind(cardContent.layoutXProperty());
        bl_H.layoutYProperty().bind(cardContent.layoutYProperty().add(cardContent.heightProperty()).subtract(L_THICKNESS));

        Rectangle bl_V = createLPart(L_THICKNESS, L_LENGTH);
        bl_V.layoutXProperty().bind(cardContent.layoutXProperty());
        bl_V.layoutYProperty().bind(cardContent.layoutYProperty().add(cardContent.heightProperty()).subtract(L_LENGTH));

        // --- Bottom Right L-Ecke ---
        Rectangle br_H = createLPart(L_LENGTH, L_THICKNESS);
        br_H.layoutXProperty().bind(cardContent.layoutXProperty().add(cardContent.widthProperty()).subtract(L_LENGTH));
        br_H.layoutYProperty().bind(cardContent.layoutYProperty().add(cardContent.heightProperty()).subtract(L_THICKNESS));

        Rectangle br_V = createLPart(L_THICKNESS, L_LENGTH);
        br_V.layoutXProperty().bind(cardContent.layoutXProperty().add(cardContent.widthProperty()).subtract(L_THICKNESS));
        br_V.layoutYProperty().bind(cardContent.layoutYProperty().add(cardContent.heightProperty()).subtract(L_LENGTH));

        // Alle 8 Teile dem highlightPane hinzufügen
        highlightPane.getChildren().addAll(tl_H, tl_V, tr_H, tr_V, bl_H, bl_V, br_H, br_V);

        // 3. Der Container (StackPane), der alles übereinander stapelt
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(cardContent, highlightPane);

        /*
        // 4. Klick-Logik
        stackPane.setOnMouseClicked(e -> {
            System.out.println(plantName + " ausgewählt! Kostet: " + cost);
            plantManager.setSelectedPlant(plantName);

            if (currentSelectionHighlight != null) {
                currentSelectionHighlight.setVisible(false);
            }

            currentSelectionHighlight = highlightPane;
            currentSelectionHighlight.setVisible(true);
        });
        */

        stackPane.setOnMousePressed(e -> {

            if(e.getButton() != javafx.scene.input.MouseButton.PRIMARY){
                return;
            }

            int currentSun = geti("sun"); // Aktuelles Geld abfragen

            // PRÜFEN: Hat der Spieler genug Geld für diese spezifische Pflanze?
            if (currentSun >= cost) {
                // --- GENUG GELD: Alles ganz normal machen ---
                if (currentSelectionHighlight != null) {
                    currentSelectionHighlight.setVisible(false);
                }
                currentSelectionHighlight = highlightPane;
                currentSelectionHighlight.setVisible(true);

                plantManager.startDrag(plantName, cost, getInput().getMouseXUI(), getInput().getMouseYUI(), plantImage);

            } else {
                // --- ZU WENIG GELD: Fehler-Sound und Rot blinken! ---
                play("audios/error.mp3");

                // cardContent (die VBox) rot färben
                cardContent.setStyle("-fx-background-color: red;");

                // Nach 0.2 Sekunden wieder auf dein Standard-Grau (#DDDDDD) setzen
                getGameTimer().runOnceAfter(() -> {
                    cardContent.setStyle("-fx-background-color: #DDDDDD;");
                }, Duration.seconds(0.2));

                // Extra-Tipp: Da du unten schon eine flashRed() Methode hast, rufen wir die gleich mit auf,
                // dann blinkt die Sonnen-Anzeige direkt mit!
                flashRed();
            }
        });

        stackPane.setOnMouseDragged(e -> {

            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) {
                return;
            }
            plantManager.updateDrag(getInput().getMouseXUI(), getInput().getMouseYUI());
        });

        stackPane.setOnMouseReleased(e -> {

            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) {
                return;
            }
            plantManager.endDrag(getInput().getMouseXUI(), getInput().getMouseYUI());

            // NEU: Hier ist der Null-Check! Wir prüfen erst, ob die Variable nicht leer ist.
            if (currentSelectionHighlight != null) {
                currentSelectionHighlight.setVisible(false);
            }
        });


        return stackPane;

    }


    private Rectangle createLPart(double width, double height) {
        Rectangle r = new Rectangle(width, height, L_COLOR);
        r.setArcWidth(L_ARC);
        r.setArcHeight(L_ARC);
        r.setEffect(new DropShadow(5, L_COLOR));
        return r;
    }

    public static void flashRed() {
        if (sunTextNode != null) {
            // 1. Text rot machen
            sunTextNode.setFill(Color.RED);

            // 2. Einen Timer stellen, der ihn nach 0.3 Sekunden wieder weiß macht
            getGameTimer().runOnceAfter(() -> {
                sunTextNode.setFill(Color.WHITE);
            }, javafx.util.Duration.seconds(0.3));
        }
    }
}