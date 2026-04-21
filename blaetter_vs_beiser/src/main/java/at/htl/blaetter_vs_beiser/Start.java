package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
//import javafx.scene.Cursor;
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

        Texture bgTexture = FXGL.texture("Background.png");
        bgTexture.setFitWidth(getAppWidth());
        bgTexture.setFitHeight(getAppHeight());
        bgTexture.setPreserveRatio(false);

        entityBuilder()
                .at(0, 0)
                .view(bgTexture)
                .zIndex(-100)
                .buildAndAttach();

        getGameWorld().addEntityFactory(new GameFactory());
        //getGameWorld().addEntityFactory(new Zombie());


        spawn("zombie", getAppWidth(), 85);
        spawn("zombie", getAppWidth(), 185);
        spawn("zombie", getAppWidth(), 285);
        spawn("zombie", getAppWidth(), 385);
        spawn("zombie", getAppWidth(), 485);



        gridService.drawGrid();
    }

    @Override
    protected void initPhysics() {

        // --- REGEL 1: ERBSE TRIFFT ZOMBIE ---
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PEA, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity pea, Entity zombie) {
                // 1. Die Erbse platzt sofort!
                pea.removeFromWorld();

                // 2. Wir rufen die takeDamage-Methode auf unserem Zombie auf
                zombie.getComponent(ZombieComponent.class).takeDamage(20);
            }
        });

        // --- REGEL 2: ZOMBIE TRIFFT PFLANZE ---
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ZOMBIE, EntityType.PLANT) {
            @Override
            protected void onCollisionBegin(Entity zombie, Entity plant) {
                // Wir rufen die startEating-Methode auf unserem Zombie auf.
                // Dadurch wird seine "targetPlant" gesetzt, er stoppt und fängt an zu fressen!
                zombie.getComponent(ZombieComponent.class).startEating(plant);
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