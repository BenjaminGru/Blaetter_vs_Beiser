package at.htl.blaetter_vs_beiser;

import at.htl.blaetter_vs_beiser.Components_Plants.Peashooter;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture; // WICHTIG: Texture Import

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {


    @Spawns("peashooter")
    public Entity newPeashooter(SpawnData data) {
        return entityBuilder(data)
                // .view(...)
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Peashooter())
                .build();
    }


    @Spawns("sunflower")
    public Entity newSunflower(SpawnData data) {
        Texture tex = texture("Plants/Sunflower.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }


    @Spawns("wallnut")
    public Entity newWallnut(SpawnData data) {
        Texture tex = texture("Plants/Wallnut.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }


    @Spawns("potatomine")
    public Entity newPotatoMine(SpawnData data) {
        Texture tex = texture("Plants/Potatomine.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }

    // --- 5. KIRSCHGRANATE ---
    @Spawns("cherrybomb")
    public Entity newCherryBomb(SpawnData data) {
        Texture tex = texture("Plants/Cherrybomb.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }


    @Spawns("snowpeashooter")
    public Entity newSnowPeashooter(SpawnData data) {
        Texture tex = texture("Plants/Snowpeashooter.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }
}