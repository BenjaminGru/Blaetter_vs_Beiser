package at.htl.blaetter_vs_beiser;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static com.almasb.fxgl.dsl.FXGL.*;

public class PlantManager {

    private static GridService gridService = null;
    private String selectedPlant = null;
    private ImageView dragIcon = null;
    private String draggingPlant = null;
    private int draggingCost = 0;

    public PlantManager(GridService gridService) {
        this.gridService = gridService;
    }

    public void setSelectedPlant(String plantName) {
        this.selectedPlant = plantName;
        System.out.println("Ausgewählt: " + plantName);
    }

    public void startDrag(String plantName, int cost, double startX, double startY, Image plantImage) {
        this.draggingPlant = plantName;
        this.draggingCost = cost;


        dragIcon = new ImageView(plantImage);
        dragIcon.setFitWidth(GridService.TILE_SIZE_WIDTH);
        dragIcon.setFitHeight(GridService.TILE_SIZE_HEIGHT);
        dragIcon.setOpacity(1);

        // Zentrieren unter der Maus
        dragIcon.setTranslateX(startX - (GridService.TILE_SIZE_WIDTH / 2.0));
        dragIcon.setTranslateY(startY - (GridService.TILE_SIZE_HEIGHT / 2.0));

        // Bild zur Szene hinzufügen
        getGameScene().addUINode(dragIcon);
    }

    public void updateDrag(double mouseX, double mouseY) {
        if(dragIcon != null) {
            dragIcon.setTranslateX(mouseX - (GridService.TILE_SIZE_WIDTH / 2.0));
            dragIcon.setTranslateY(mouseY - (GridService.TILE_SIZE_HEIGHT / 2.0));
        }
    }

    public void endDrag(double mouseX, double mouseY) {

        if(dragIcon != null) {
            getGameScene().removeUINode(dragIcon);
            dragIcon = null;
        }

        if(draggingPlant != null) {

            Point2D gridPos = gridService.getGridCoordinates(mouseX, mouseY);
            int x = (int) gridPos.getX();
            int y = (int) gridPos.getY();

            if(gridService.isFree(x, y)) {

                int currentSun = getip("sun").get();

                if(currentSun >= draggingCost) {
                    inc("sun", -draggingCost);
                    gridService.setOccupied(x, y, true);

                    Point2D pixelPos = gridService.getPixelCoordinates(x, y);
                    String entityName = getEntityNameFromUI(draggingPlant);

                    spawn(entityName, pixelPos.getX(), pixelPos.getY());

                }
            }
        }
    }
    private String getEntityNameFromUI(String uiName) {
        switch (uiName) {
            case "Bohnenschießer": return "peashooter";
            case "Sonnenblume": return "sunflower";
            case "Walnuss": return "wallnut";
            case "Kartoffelmine": return "potatomine";
            case "Kirschgranate": return "cherrybomb";
            case "Eisbohnenschießer": return "snowpeashooter";
            default: return "peashooter"; // Fallback, falls was schiefgeht
        }
    }

    public static void handleGridClick(double mouseX, double mouseY) {

        Point2D gridPos = gridService.getGridCoordinates(mouseX, mouseY);
        int x = (int) gridPos.getX();
        int y = (int) gridPos.getY();


        if (x >= 0 && x < GridService.COLS && y >= 0 && y < GridService.ROWS) {
            System.out.println("YEAH BUDDY Feld -> X: " + x + " | Y: " + y);

        }
    }


}
