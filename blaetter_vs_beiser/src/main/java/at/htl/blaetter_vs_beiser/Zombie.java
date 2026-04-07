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
        // Hol den Typ-String aus dem JSON (z.B. "HUT_15")
        String type = data.get("type");

        return entityBuilder(data)
                .type(EntityType.ZOMBIE)
                .with(new ZombieComponent(type))
                .collidable()
                .build();
    }
}