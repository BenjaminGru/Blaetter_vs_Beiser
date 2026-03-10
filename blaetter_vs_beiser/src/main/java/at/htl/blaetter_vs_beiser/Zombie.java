package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Zombie implements EntityFactory {




    @Spawns("enemy") // 2. The tag goes right here
    public Entity newEnemy(SpawnData data) { // 3. Method signature must look like this
        return entityBuilder(data)

                .view("img.png")
                .with(new ZombieComponent())
                .build();
    }
}
