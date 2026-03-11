package at.htl.blaetter_vs_beiser;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
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

    // --- NEU: KONSTANTEN FÜR DEN L-ECKEN-LOOK ---
    private static final double L_THICKNESS = 6.0; // Dicke der L-Linien
    private static final double L_LENGTH = 20.0;    // Länge eines Schenkels des L
    private static final double L_ARC = 10.0;       // Abrundung der Ecken für den "weichen" Look
    private static final Color L_COLOR = Color.YELLOW;

    public PlantSelectionUI(PlantManager plantManager) {
        this.plantManager = plantManager;

        setSpacing(20);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);

        setStyle("-fx-background-color: #8B4513; -fx-border-color: #5C2E0B; -fx-border-width: 3;");

        getChildren().add(createSunCounter());

        HBox cardsBox = new HBox(5);
        cardsBox.getChildren().addAll(
                createPlantCard("Bohnenschießer", 100, Color.LIGHTGREEN),
                createPlantCard("Sonnenblume", 50, Color.YELLOW),
                createPlantCard("Walnuss", 50, Color.BEIGE),
                createPlantCard("Kartoffelmine", 50, Color.YELLOW),
                createPlantCard("Kirschgranate", 50, Color.DARKRED)
        );

        getChildren().add(cardsBox);
    }

    private VBox createSunCounter() {
        VBox sunBox = new VBox(5);
        sunBox.setAlignment(Pos.CENTER);
        Rectangle sunIcon = new Rectangle(40, 40, Color.GOLD);
        Text sunText = getUIFactoryService().newText("", Color.WHITE, 18);
        sunText.textProperty().bind(getip("sun").asString());
        sunBox.getChildren().addAll(sunIcon, sunText);
        return sunBox;
    }

    // --- GEÄNDERTE METHODE: createPlantCard ---
    // Sie gibt jetzt einen Node zurück, da wir einen StackPane-Container nutzen müssen.
    private Node createPlantCard(String plantName, int cost, Color colorHint) {

        // 1. Die eigentliche Pflanzenkarte (Inhalt)
        VBox cardContent = new VBox(2);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(5));
        // Wir nutzen den dunklen Standardrand wieder
        cardContent.setStyle("-fx-background-color: #DDDDDD;");

        Rectangle plantIcon = new Rectangle(40, 40, colorHint);
        Text costText = getUIFactoryService().newText(cost + " ☀", Color.BLACK, 14);
        cardContent.getChildren().addAll(plantIcon, costText);



        Pane highlightPane = new Pane();
        highlightPane.setMouseTransparent(true);
        highlightPane.setVisible(false);


        Rectangle tl_H = createLPart(L_LENGTH, L_THICKNESS); // Horizontale Linie
        tl_H.layoutXProperty().bind(cardContent.layoutXProperty());
        tl_H.layoutYProperty().bind(cardContent.layoutYProperty());

        Rectangle tl_V = createLPart(L_THICKNESS, L_LENGTH); // Vertikale Linie
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
        stackPane.getChildren().addAll(cardContent, highlightPane); // Erst Inhalt, dann Highlight oben drauf


        // 4. Klick-Logik auf den Container setzen
        stackPane.setOnMouseClicked(e -> {
            System.out.println(plantName + " ausgewählt! Kostet: " + cost);
            plantManager.setSelectedPlant(plantName);

            // SCHRITT 1: Altes Highlight unsichtbar machen
            if (currentSelectionHighlight != null) {
                currentSelectionHighlight.setVisible(false);
            }

            // SCHRITT 2: Gedächtnis aktualisieren
            currentSelectionHighlight = highlightPane;

            // SCHRITT 3: Neues gelbes Ecken-Highlight sichtbar machen
            currentSelectionHighlight.setVisible(true);
        });

        return stackPane;
    }

    // --- NEU: Hilfsmethode, um ein L-Teil-Rechteck zu erstellen ---
    private Rectangle createLPart(double width, double height) {
        Rectangle r = new Rectangle(width, height, L_COLOR);
        r.setArcWidth(L_ARC);  // Abrundung für den "weichen" Look
        r.setArcHeight(L_ARC);
        // Optional: Ein weicher Schatten für den "Glüh"-Effekt
        r.setEffect(new DropShadow(5, L_COLOR));
        return r;
    }
}