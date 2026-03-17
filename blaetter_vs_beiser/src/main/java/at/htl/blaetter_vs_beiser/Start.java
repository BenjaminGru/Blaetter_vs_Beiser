package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Start extends GameApplication {

    // Nur deklarieren, noch nicht erschaffen!
    private GridService gridService;
    private PlantManager plantManager;



    @Override
    protected void onPreInit() {
        // Diese Methode wird von FXGL aufgerufen, sobald die Engine wach ist,
        // aber BEVOR initInput oder initUI aufgerufen werden. Perfekt für Manager!
        gridService = new GridService();
        plantManager = new PlantManager(gridService);
    }


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BvB");
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    @Override
    protected void initInput() {

        }


    @Override
    protected void initGameVars(java.util.Map<String, Object> vars) {
        vars.put("sun", 1000);
    }

    @Override
    protected void initUI() {
        Image cursorImg = FXGL.image("Mousecursor.png");
        getGameScene().getRoot().setCursor(new javafx.scene.ImageCursor(cursorImg, 0, 0));

        // FEHLER 2 BEHOBEN: Wir übergeben den plantManager an die UI
        PlantSelectionUI ui = new PlantSelectionUI(plantManager);

        ui.setTranslateX(0);
        ui.setTranslateY(0);

        getGameScene().addUINode(ui);
    }

    @Override
    protected void initGame() {

        Texture bgTexture = FXGL.texture("Background.png");
        bgTexture.setFitWidth(getAppWidth());
        bgTexture.setFitHeight(getAppHeight());
        bgTexture.setPreserveRatio(false);


        entityBuilder()
                .at(0, 0)
                .view(bgTexture) // Nutze die skalierte bgTexture
                .zIndex(-100)      // Ganz nach hinten
                .buildAndAttach();
        getGameWorld().addEntityFactory(new GameFactory());
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