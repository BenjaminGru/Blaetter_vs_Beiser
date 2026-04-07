package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Cursor;
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
        vars.put("sun", 10000);
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



        //GOLF UI


        Rectangle secretbutton = new Rectangle(50,50, Color.TRANSPARENT);

        secretbutton.setTranslateX(getAppWidth() - 50);
        secretbutton.setTranslateY(0);



        secretbutton.setOnMouseClicked(event -> {
            getSceneService().pushSubScene(new GolfRangeScene());
        });

        getGameScene().addUINode(secretbutton);

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


        spawn("zombie", 100, 60);
        spawn("zombie", getAppWidth(), 150);
        spawn("zombie", getAppWidth(), 240);
        spawn("zombie", getAppWidth(), 330);
        spawn("zombie", getAppWidth(), 420);


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



    @Override
    protected void initPhysics() {

        // Kollision: ERBSE trifft ZOMBIE
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PEA, EntityType.ZOMBIE) {

            @Override
            protected void onCollisionBegin(Entity pea, Entity zombie) {
                // DEIN PART: Die Erbse hat ihr Ziel erreicht und wird zerstört
                pea.removeFromWorld();

                // PART VON DEINEM FREUND:
                // Hier kann er später seinen Code einfügen, der dem Zombie Leben abzieht!
                System.out.println("PENG! Erbse hat Zombie getroffen!");
                // z.B. zombie.getComponent(ZombieComponent.class).takeDamage(20);
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        // Hole alle Pflanzen auf dem Feld
        var plants = getGameWorld().getEntitiesByType(EntityType.PLANT);

        for (var plant : plants) {
            // Da jetzt jede Pflanze 100%ig "hp" hat, reicht dieser simple Check:
            if (plant.getInt("hp") <= 0) {
                plant.removeFromWorld(); // Pflanze stirbt und verschwindet!
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}