package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Zombie implements EntityFactory {

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        boolean hatHut = data.get("mitHut"); // Wert aus dem JSON holen

        return entityBuilder(data)
                .type(EntityType.ZOMBIE)
                // Hier geben wir den Wert direkt in die Klammern der Komponente!
                .with(new ZombieComponent(hatHut))
                .collidable()
                .build();
    }
}