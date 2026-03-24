package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture; // WICHTIG: Texture Import

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameFactory implements EntityFactory {

    // --- 1. BOHNENSCHIEßER ---
    @Spawns("peashooter")
    public Entity newPeashooter(SpawnData data) {
        // 1. Bild laden
        Texture tex = texture("Plants/Peashooter.png");
        // 2. Größe anpassen (gibt "void" zurück, deshalb in eigenen Zeilen)
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        // 3. Bild an die Entity übergeben
        return entityBuilder(data)
                .view(tex)
                // .with(new PeashooterComponent())
                .build();
    }

    // --- 2. SONNENBLUME ---
    @Spawns("sunflower")
    public Entity newSunflower(SpawnData data) {
        Texture tex = texture("Plants/Sunflower.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }

    // --- 3. WALNUSS ---
    @Spawns("wallnut")
    public Entity newWallnut(SpawnData data) {
        Texture tex = texture("Plants/Wallnut.png");
        tex.setFitWidth(60);
        tex.setFitHeight(60);

        return entityBuilder(data)
                .view(tex)
                .build();
    }

    // --- 4. KARTOFFELMINE ---
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

    // --- 6. EISBOHNENSCHIEßER ---
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