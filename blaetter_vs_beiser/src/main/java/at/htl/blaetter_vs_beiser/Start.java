package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

        System.out.println("#########################################");
        System.out.println("DEBUG: initGame() wurde GESTARTET!");
        System.out.println("#########################################");
        // 1. Hintergrund & Factories
        entityBuilder().view(texture("Background.png", getAppWidth(), getAppHeight())).zIndex(-100).buildAndAttach();

        getGameWorld().addEntityFactory(new Zombie());
        getGameWorld().addEntityFactory(new GameFactory());
        gridService.drawGrid();

        // 2. Level laden mit Diagnose
        try {
            var loader = getAssetLoader().loadJSON("Level/level1.json", LevelData.class);
            if (loader.isPresent()) {
                LevelData level = loader.get();
                System.out.println("--- START ---");
                System.out.println("Datei gefunden! Levelname: " + level.levelName);

                if (level.waves == null || level.waves.isEmpty()) {
                    System.err.println("FEHLER: Die Liste 'waves' ist leer oder null! Prüfe die Namen im JSON.");
                } else {
                    System.out.println("Anzahl geplanter Zombies: " + level.waves.size());

                    int[] lanesY = {70, 170, 280, 360, 460};

                    for (LevelData.ZombieSpawn s : level.waves) {
                        System.out.println("Plane Zombie: " + s.type + " bei Sekunde " + s.time);

                        getGameTimer().runOnceAfter(() -> {
                            SpawnData data = new SpawnData(getAppWidth(), lanesY[s.lane]);
                            data.put("mitHut", "HUT".equalsIgnoreCase(s.type));
                            spawn("zombie", data);
                            System.out.println("!!! SPAWN JETZT: " + s.type);
                        }, Duration.seconds(s.time));
                    }
                }
                System.out.println("--- ENDE ---");
            } else {
                System.err.println("FEHLER: Datei 'Level/level1.json' existiert nicht im Ordner assets!");
            }
        } catch (Exception e) {
            System.err.println("FEHLER beim JSON-Parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}