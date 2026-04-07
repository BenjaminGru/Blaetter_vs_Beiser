package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Zombie implements EntityFactory {

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {

        String type = data.get("type");

        // 2. Jetzt geben wir diesen 'type' an die Component weiter
        return entityBuilder(data)
                .type(EntityType.ZOMBIE)
                .at(data.getX(), data.getY())
                .zIndex(100)
                .with(new ZombieComponent(type))
                .build();
    }
}