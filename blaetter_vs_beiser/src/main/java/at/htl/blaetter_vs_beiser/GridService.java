package at.htl.blaetter_vs_beiser;

import javafx.geometry.Point2D;
import static com.almasb.fxgl.dsl.FXGL.*;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GridService {

    public static final int COLS = 9;
    public static final int ROWS = 5;

    public static final int TILE_SIZE_WIDTH = 65;
    public static final int TILE_SIZE_HEIGHT = 100;

    private double offsetX;
    private double offsetY;

    // NEU: Unser "Gedächtnis". Merkt sich für jedes Feld, ob es belegt ist (true/false)
    private boolean[][] occupiedCells = new boolean[COLS][ROWS];

    public void drawGrid() {
        offsetX = getAppWidth() / 4.1;
        offsetY = getAppHeight() / 8.0;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {

                double posX = offsetX + (x * TILE_SIZE_WIDTH);
                double posY = offsetY + (y * TILE_SIZE_HEIGHT);

                Rectangle rect = new Rectangle(TILE_SIZE_WIDTH - 2, TILE_SIZE_HEIGHT - 2);
                rect.setFill(Color.GREEN.deriveColor(0, 1, 1, 0.3));
                rect.setStroke(Color.DARKGREEN);

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

    // --- NEUE METHODEN FÜR DEN PLANTMANAGER ---

    // 1. Prüft, ob der Klick überhaupt auf dem Feld war UND ob das Feld leer ist
    public boolean isFree(int x, int y) {
        // Zuerst checken, ob x und y überhaupt innerhalb von 0-8 und 0-4 liegen
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            return !occupiedCells[x][y]; // true, wenn NICHT belegt
        }
        return false; // Klick war außerhalb des Rasens
    }

    // 2. Blockiert ein Feld (wird aufgerufen, wenn Pflanze gebaut wurde)
    public void setOccupied(int x, int y, boolean occupied) {
        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            occupiedCells[x][y] = occupied;
        }
    }

    // 3. Rechnet das Grid (z.B. Feld 3|2) zurück in Pixel um, damit FXGL weiß, WO gespawnt wird
    public Point2D getPixelCoordinates(int x, int y) {
        double posX = offsetX + (x * TILE_SIZE_WIDTH);
        double posY = offsetY + (y * TILE_SIZE_HEIGHT);
        return new Point2D(posX, posY);
    }
}

