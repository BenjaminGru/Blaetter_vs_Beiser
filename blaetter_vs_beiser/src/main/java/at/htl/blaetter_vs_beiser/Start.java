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
import static javax.swing.UIManager.getInt;

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

        vars.put("currentRound", 1);      // Wir starten in Runde 1
        vars.put("zombiesToSpawn", 0);    // Wie viele Zombies kommen noch?
        vars.put("roundActive", false);   // Läuft die Runde gerade?
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
        // 1. Hintergrund & Factories (Dein alter Code)
        entityBuilder().view(texture("Background.png", getAppWidth(), getAppHeight())).zIndex(-100).buildAndAttach();
        getGameWorld().addEntityFactory(new GameFactory());
        gridService.drawGrid();


        startRound(getInt("currentRound"));
    }



    @Override
    protected void initPhysics() {

        // 1. Dein alter Handler: ERBSE trifft ZOMBIE
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PEA, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity pea, Entity zombie) {


                if (pea.getProperties().exists("isIce")) {
                    zombie.getComponent(ZombieComponent.class).applyFreeze();
                    System.out.println("BRRR! Zombie ist eingefroren!");
                }

                pea.removeFromWorld(); // Erbse platzt
                System.out.println("PENG! Erbse hat Zombie getroffen!");

                // ... (Hier bleibt dein alter Code für den hp-Schaden genau gleich!) ...
                if (zombie.getProperties().exists("hp")) {
                    int hp = zombie.getInt("hp");
                    hp -= 5;
                    if (hp <= 0) {
                        System.out.println("Zombie wurde besiegt!");
                        zombie.removeFromWorld();
                    } else {
                        zombie.setProperty("hp", hp);
                    }
                }
            }
        });



        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ZOMBIE, EntityType.PLANT) {
            @Override
            protected void onCollisionBegin(Entity zombie, Entity plant) {
                // Wir holen uns die Logik-Komponente des Zombies und setzen die Pflanze als Ziel
                ZombieComponent zombieLogic = zombie.getComponent(ZombieComponent.class);
                zombieLogic.setTarget(plant);
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        // ... (Dein bisheriger Code, der die Pflanzen mit 0 HP entfernt, bleibt hier oben stehen) ...

        // NEU: CHECK AUF RUNDEN-ENDE
        if (getb("roundActive")) {

            // Wie viele Zombies stehen noch auf dem Rasen?
            int aliveZombies = getGameWorld().getEntitiesByType(EntityType.ZOMBIE).size();

            // Wie viele Zombies warten noch unsichtbar auf ihren Spawn?
            int remainingSpawns = getInt("zombiesToSpawn");

            // Wenn keiner mehr wartet UND keiner mehr lebt -> GEWONNEN!
            if (aliveZombies == 0 && remainingSpawns == 0) {

                set("roundActive", false); // Runde beenden, damit das hier nicht 60x pro Sekunde feuert
                System.out.println("YAY! Runde " + getInt("currentRound") + " GESCHAFFT!");

                // Runden-Nummer um 1 erhöhen
                inc("currentRound", 1);

                // Wir gönnen dem Spieler 4 Sekunden Pause, dann startet die nächste Welle
                getGameTimer().runOnceAfter(() -> {
                    startRound(getInt("currentRound"));
                }, Duration.seconds(4));
            }
        }
    }

    // NEUE METHODE
    private void startRound(int round) {
        String fileName = "Level/level" + round + ".json";
        System.out.println("========== STARTE RUNDE " + round + " ==========");

        try {
            var loader = getAssetLoader().loadJSON(fileName, LevelData.class);
            if (loader.isPresent()) {
                LevelData level = loader.get();

                // 1. Wir merken uns, wie viele Zombies in dieser Runde kommen!
                set("zombiesToSpawn", level.waves.size());
                set("roundActive", true); // Runde geht offiziell los

                int[] lanesY = {70, 170, 280, 360, 460};

                for (LevelData.ZombieSpawn s : level.waves) {
                    getGameTimer().runOnceAfter(() -> {
                        SpawnData data = new SpawnData(getAppWidth(), lanesY[s.lane]);
                        data.put("mitHut", "HUT".equalsIgnoreCase(s.type));
                        spawn("zombie", data);

                        // 2. WICHTIG: Ein Zombie ist gespawnt, also ziehen wir einen von der Warteschlange ab!
                        inc("zombiesToSpawn", -1);

                    }, Duration.seconds(s.time));
                }
            } else {
                // Wenn es z.B. keine
                // level2
                //.json mehr gibt, hat man das Spiel durchgespielt!
                System.out.println("Keine weiteren Level gefunden. DU HAST GEWONNEN!");
                set("roundActive", false);
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden von " + fileName + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}