package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Zombie implements EntityFactory {




    @Spawns("enemy")
    public Entity newZombie(SpawnData data) {
        return FXGL.entityBuilder(data)
                .at(data.getX(), data.getY())
                .with(new ZombieComponent()) // Die Component regelt das Bild!
                .zIndex(100)
                .build();
    }
}
