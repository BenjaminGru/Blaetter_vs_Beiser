package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
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

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlantSelectionUI extends HBox {

    private PlantManager plantManager;
    private Pane currentSelectionHighlight = null; // Speichert das Pane mit den L-Ecken

    // --- KONSTANTEN FÜR DEN L-ECKEN-LOOK ---
    private static final double L_THICKNESS = 6.0;
    private static final double L_LENGTH = 20.0;
    private static final double L_ARC = 10.0;
    private static final Color L_COLOR = Color.YELLOW;


    public PlantSelectionUI(PlantManager plantManager) {
        this.plantManager = plantManager;


        setSpacing(20);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);

        setStyle("-fx-background-color: #8B4513; -fx-border-color: #5C2E0B; -fx-border-width: 3;");

        getChildren().add(createSunCounter());

        HBox cardsBox = new HBox(5);

        // Alle Pflanzenkarten nutzen nun Bilder aus dem assets/textures/ Ordner von FXGL
        cardsBox.getChildren().addAll(
                createPlantCard("Bohnenschießer", 100, FXGL.image("Peashooter.png")),
                createPlantCard("Sonnenblume", 50, FXGL.image("Sunflower.png")),
                createPlantCard("Walnuss", 50, FXGL.image("Wallnut.png")),
                createPlantCard("Kartoffelmine", 50, FXGL.image("Potatomine.png")),
                createPlantCard("Kirschgranate", 150, FXGL.image("Cherrybomb.png")),
                createPlantCard("Eisbohnenschießer", 150, FXGL.image("Snowpeashooter.png"))
        );

        getChildren().add(cardsBox);
    }

    private VBox createSunCounter() {
        VBox sunBox = new VBox(5);
        sunBox.setAlignment(Pos.CENTER);


        ImageView sunIcon = new ImageView(FXGL.image("Sun.png"));
        sunIcon.setFitWidth(40);
        sunIcon.setFitHeight(40);

        sunIcon.setPreserveRatio(true);

        Text sunText = getUIFactoryService().newText("", Color.WHITE, 18);
        sunText.textProperty().bind(getip("sun").asString());

        sunBox.getChildren().addAll(sunIcon, sunText);
        return sunBox;
    }

    // Parameter von colorHint auf plantImage umbenannt für mehr Klarheit
    private Node createPlantCard(String plantName, int cost, Image plantImage) {

        // 1. Die eigentliche Pflanzenkarte (Inhalt)
        VBox cardContent = new VBox(2);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(5));
        cardContent.setStyle("-fx-background-color: #DDDDDD;");

        // --- GEÄNDERT: ImageView anstelle von Rectangle für die Pflanze ---
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

        return stackPane;
    }

    private Rectangle createLPart(double width, double height) {
        Rectangle r = new Rectangle(width, height, L_COLOR);
        r.setArcWidth(L_ARC);
        r.setArcHeight(L_ARC);
        r.setEffect(new DropShadow(5, L_COLOR));
        return r;
    }
}