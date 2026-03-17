package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Zombie implements EntityFactory {




    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        return FXGL.entityBuilder(data)
                .at(data.getX(), data.getY())
                // Hier definierst du die Hitbox:
                // BoundingShape.box(Breite, Höhe)
                .bbox(new HitBox(BoundingShape.box(64, 102)))
                .with(new ZombieComponent())
                .zIndex(100)
                .build();
    }
}
