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
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Sunflower())
                .build();
    }


    @Spawns("wallnut")
    public Entity newWallnut(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Wallnut())
                .build();
    }


    @Spawns("potatomine")
    public Entity newPotatomine(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Potatomine())
                .build();
    }

    // --- 5. KIRSCHGRANATE ---
    @Spawns("cherrybomb")
    public Entity newCherrybomb(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Cherrybomb())
                .build();
    }


    @Spawns("snowpeashooter")
    public Entity newSnowpeashooter(SpawnData data) {
        return entityBuilder(data)
                // Wir fügen unsere brandneue Animations-Komponente hinzu:
                .with(new at.htl.blaetter_vs_beiser.Components_Plants.Snowpeashooter())
                .build();
    }

}