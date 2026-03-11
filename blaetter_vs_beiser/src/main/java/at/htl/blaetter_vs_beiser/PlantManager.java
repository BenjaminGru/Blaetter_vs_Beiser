package at.htl.blaetter_vs_beiser;

import javafx.geometry.Point2D;
import static com.almasb.fxgl.dsl.FXGL.*;

public class PlantManager {

    private static GridService gridService = null;
    private String selectedPlant = null;

    public PlantManager(GridService gridService) {
        this.gridService = gridService;
    }

    public void setSelectedPlant(String plantName) {
        this.selectedPlant = plantName;
        System.out.println("🌱 Ausgewählt: " + plantName);
    }

    public static void handleGridClick(double mouseX, double mouseY) {

        Point2D gridPos = gridService.getGridCoordinates(mouseX, mouseY);
        int x = (int) gridPos.getX();
        int y = (int) gridPos.getY();


        if (x >= 0 && x < GridService.COLS && y >= 0 && y < GridService.ROWS) {
            System.out.println("✅ Erfolgreicher Klick auf Feld -> X: " + x + " | Y: " + y);

        }
    }


}
