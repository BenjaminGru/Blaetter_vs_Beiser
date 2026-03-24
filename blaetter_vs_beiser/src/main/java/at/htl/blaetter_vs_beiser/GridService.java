package at.htl.blaetter_vs_beiser;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.entity.Entity; // NEUER IMPORT FÜR DIE SCHAUFEL

import static com.almasb.fxgl.dsl.FXGL.*;

public class GridService {

    public static final int COLS = 9;
    public static final int ROWS = 5;

    public static final int TILE_SIZE_WIDTH = 65;
    public static final int TILE_SIZE_HEIGHT = 100;

    private double offsetX;
    private double offsetY;

    // NEU: Wir speichern jetzt die ECHTEN Pflanzen (Entities), nicht mehr nur true/false
    private Entity[][] gridEntities = new Entity[COLS][ROWS];

    public void drawGrid() {
        offsetX = getAppWidth() / 4.1;
        offsetY = getAppHeight() / 8.0;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {

                double posX = offsetX + (x * TILE_SIZE_WIDTH);
                double posY = offsetY + (y * TILE_SIZE_HEIGHT);

                Rectangle rect = new Rectangle(TILE_SIZE_WIDTH - 2, TILE_SIZE_HEIGHT - 2);
                rect.setFill(Color.TRANSPARENT.deriveColor(0, 1, 1, 0.3));
                rect.setStroke(Color.TRANSPARENT);

                entityBuilder()
                        .at(posX, posY)
                        .view(rect)
                        .buildAndAttach();

            }
        }
    }

    public Point2D getGridCoordinates(double mouseX, double mouseY) {
        int gridX = (int) ((mouseX - offsetX) / TILE_SIZE_WIDTH);
        int gridY = (int) ((mouseY - offsetY) / TILE_SIZE_HEIGHT);
        return new Point2D(gridX, gridY);
    }

    // --- METHODEN FÜR DEN PLANTMANAGER ---

    // 1. Prüft, ob das Feld frei ist (also ob da "nichts" bzw. null drin steht)
    public boolean isFree(int x, int y) {
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            return gridEntities[x][y] == null; // true, wenn NICHT belegt
        }
        return false; // Klick war außerhalb des Rasens
    }

    // 2. Speichert die echte Pflanze im Feld ab
    public void setOccupied(int x, int y, Entity plant) {
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            gridEntities[x][y] = plant;
        }
    }

    // 3. NEU FÜR DIE SCHAUFEL: Entfernt die Pflanze vom Spielfeld und aus dem Raster
    public void removePlant(int x, int y) {
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            Entity plant = gridEntities[x][y];
            if (plant != null) {
                plant.removeFromWorld();   // Löscht die Grafik aus dem Spiel
                gridEntities[x][y] = null; // Macht das Feld wieder frei für neue Pflanzen
            }
        }
    }

    // 4. Rechnet das Grid (z.B. Feld 3|2) zurück in Pixel um
    public Point2D getPixelCoordinates(int x, int y) {
        double posX = offsetX + (x * TILE_SIZE_WIDTH);
        double posY = offsetY + (y * TILE_SIZE_HEIGHT);
        return new Point2D(posX, posY);
    }
}