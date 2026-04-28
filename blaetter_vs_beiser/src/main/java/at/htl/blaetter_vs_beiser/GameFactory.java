package at.htl.blaetter_vs_beiser;


import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.BoundingShape;
import at.htl.blaetter_vs_beiser.Components_Plants.Peashooter;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture; // WICHTIG: Texture Import
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {


    // --- 1. DAS PROJEKTIL (Die fliegende Erbse) ---
    @Spawns("pea") // WICHTIG: Das muss "pea" heißen!
    public Entity newPea(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PEA) // Gib ihr das Namensschild "PEA"
                .viewWithBBox(new Circle(8, Color.LIGHTGREEN))
                .with(new ProjectileComponent(new Point2D(1, 0), 300))
                .collidable()
                .build();
    }

    @Spawns("icepea")
    public Entity newIcePea(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PEA) // Bleibt PEA, damit die Kollision greift
                .viewWithBBox(new javafx.scene.shape.Circle(8, javafx.scene.paint.Color.LIGHTBLUE))
                .with(new com.almasb.fxgl.dsl.components.ProjectileComponent(new javafx.geometry.Point2D(1, 0), 300))
                .collidable()
                .with("isIce", true) // NEU: Der Eis-Stempel!
                .build();
    }

    // --- 2. DIE PFLANZE (Der Bohnenschießer) ---
    @Spawns("peashooter") // Das ruft dein Spiel auf, wenn du die Karte kaufst
    public Entity newPeashooterPlant(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PLANT) // Gib ihr das Namensschild "PLANT"
                .with(new Peashooter()) // <--- HIER wird endlich dein wackelnder Spritesheet geladen!
                .bbox(new HitBox(BoundingShape.box(50, 70)))
                .collidable()
                .with("hp", 100)
                .build();

    }


    @Spawns("sunflower")
    public Entity newSunflower(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Sunflower())
                .bbox(new HitBox(BoundingShape.box(50, 70)))
                .collidable()
                .type(EntityType.PLANT)
                .with("hp", 75)
                .build();
    }


    @Spawns("wallnut")
    public Entity newWallnut(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Wallnut())
                .bbox(new HitBox(BoundingShape.box(50, 70)))
                .collidable()
                .type(EntityType.PLANT)
                .with("hp", 200)
                .build();
    }


    @Spawns("potatomine")
    public Entity newPotatomine(SpawnData data) {
        return entityBuilder(data)
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Potatomine())
                .bbox(new HitBox(BoundingShape.box(50, 70)))
                .collidable()
                .type(EntityType.PLANT)
                .with("hp", 100)
                .build();
    }

    // --- 5. KIRSCHGRANATE ---
    @Spawns("cherrybomb")
    public Entity newCherrybomb(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Cherrybomb())
                .bbox(new HitBox(BoundingShape.box(50, 70)))
                .collidable()
                .type(EntityType.PLANT)
                .with("hp", 125)
                .build();
    }


    @Spawns("snowpeashooter")
    public Entity newSnowpeashooter(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Snowpeashooter())
                .bbox(new HitBox(BoundingShape.box(50, 70)))
                .collidable()
                .type(EntityType.PLANT)
                .with("hp", 100)
                .build();
    }

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {

        String type = data.hasKey("zombieType") ? data.get("zombieType") : "NORMAL";

        // NEU: Wir legen die Lebenspunkte in den "Rucksack" (SpawnData) des Zombies!
        // Das wird beim build() automatisch zu properties.getInt("hp").
        data.put("hp", 100);

        return entityBuilder(data)
                .type(EntityType.ZOMBIE)
                .bbox(new HitBox(BoundingShape.box(50, 90))) // Unsichtbare Hitbox!
                .collidable()
                .with(new ZombieComponent(type))
                .build();
    }
}