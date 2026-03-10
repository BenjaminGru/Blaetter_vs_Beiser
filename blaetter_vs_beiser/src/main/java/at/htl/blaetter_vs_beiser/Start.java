package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// Wichtig: Der Standard-Import für alle FXGL-Methoden (entityBuilder, spawn, etc.)
import static com.almasb.fxgl.dsl.FXGL.*;

public class Start extends GameApplication {

    private GridService gridService = new GridService();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BvB");
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);

    }

    @Override
    protected void initGame() {
        // Falls du schon eine Factory hast:
        // getGameWorld().addEntityFactory(new GameFactory());

        drawGrid();
    }

    private void drawGrid() {
        for (int y = 0; y < GridService.ROWS; y++) {
            for (int x = 0; x < GridService.COLS; x++) {

                // Berechne die Pixel-Position für die Optik
                double posX = x * GridService.TILE_SIZE_HEIGHT;
                double posY = y * GridService.TILE_SIZE_WIDTH;

                // Erstelle das optische Feld (Rechteck)
                Rectangle rect = new Rectangle(GridService.TILE_SIZE_HEIGHT - 2, GridService.TILE_SIZE_WIDTH - 2);
                rect.setFill(Color.GREEN.deriveColor(0, 1, 1, 0.3)); // Grün mit 30% Deckkraft (transparent)
                rect.setStroke(Color.DARKGREEN); // Dunkelgrüner Rand für das Raster

                // Zeichne ein Rechteck für jede Zelle
                entityBuilder()
                        .at(posX, posY)
                        .view(rect)
                        .buildAndAttach();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}