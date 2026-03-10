package at.htl.blaetter_vs_beiser;

import javafx.geometry.Point2D;

public class GridService {

    /*
    Berechnet die Koordinaten.
    Logik: Wandelt Maus-Klicks (Pixel) in Grid-Koordinaten (0-8 etc.) um.#
    Check: Ist das Feld schon belegt?
    */

    public static final Integer ROWS = 5;
    public static final Integer COLS = 9;
    public static final Integer TILE_SIZE_HEIGHT = 70;
    public static final Integer TILE_SIZE_WIDTH = 90;

    public Point2D getCoordinates(double x, double y) {
        int gridX = (int) (x / TILE_SIZE_HEIGHT);
        int gridY = (int) (y / TILE_SIZE_WIDTH);
        return new Point2D(gridX, gridY);
    }

    public Boolean isValid(int x, int y) {
        return x >= 0 && x < COLS && y>= 0 && y < ROWS;
    }



}
