package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Start extends GameApplication {

    private GridService gridService = new GridService();
    // Hier ist unser Manager-Objekt (kleines p)
    private PlantManager plantManager = new PlantManager(gridService);

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BvB");
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    @Override
    protected void initInput() {
        onBtnDown(javafx.scene.input.MouseButton.PRIMARY, () -> {
            // FEHLER 1 BEHOBEN: kleines 'p', damit wir das Objekt von oben nutzen!
            plantManager.handleGridClick(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        });
    }

    @Override
    protected void initGameVars(java.util.Map<String, Object> vars) {
        vars.put("sun", 50);
    }

    @Override
    protected void initUI() {
        // FEHLER 2 BEHOBEN: Wir übergeben den plantManager an die UI
        PlantSelectionUI ui = new PlantSelectionUI(plantManager);

        ui.setTranslateX(0);
        ui.setTranslateY(0);

        getGameScene().addUINode(ui);
    }

    @Override
    protected void initGame() {

        getGameWorld().addEntityFactory(new Zombie());

        /*
        spawn("zombie", 100, 60);
        spawn("zombie", getAppWidth(), 150);
        spawn("zombie", getAppWidth(), 240);
        spawn("zombie", getAppWidth(), 330);
        spawn("zombie", getAppWidth(), 420);
        */
        gridService.drawGrid();
    }

    public static void main(String[] args) {
        launch(args);
    }
}