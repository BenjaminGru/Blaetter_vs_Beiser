package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.*;

public class Zombie implements EntityFactory {

    /*
    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {

        // 1. Typ aus den SpawnData auslesen.
        // Wenn kein Typ beim Spawnen übergeben wurde, wird "NORMAL" als Standard genommen.
        String type = data.hasKey("zombieType") ? data.get("zombieType") : "NORMAL";

        return FXGL.entityBuilder(data)
                .type(EntityType.ZOMBIE)
                .at(data.getX(), data.getY())
                .zIndex(100)
                // 2. Den String 'type' an den Konstruktor übergeben!
                .with(new ZombieComponent(type))
                .build();
    }


     */
}