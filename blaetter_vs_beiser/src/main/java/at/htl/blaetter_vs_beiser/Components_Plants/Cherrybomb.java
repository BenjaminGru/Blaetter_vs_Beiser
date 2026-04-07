package at.htl.blaetter_vs_beiser.Components_Plants;

import at.htl.blaetter_vs_beiser.EntityType;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.newLocalTimer;

public class Cherrybomb extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle;
    private LocalTimer explodeTimer;// Idle = "Stehen/Wackeln"

    public Cherrybomb() {

        animIdle = new AnimationChannel(
                image("Plants/" + "Kirschgranate_spreadsheet.png"),
                3, 97, 105,
                Duration.seconds(1.5), 0, 2
        );

        texture = new AnimatedTexture(animIdle);
        texture.loop(); // Lässt die Animation in Endlosschleife laufen
    }

    @Override
    public void onAdded() {
        // Wenn die Pflanze auf dem Feld spawnt, hängen wir die Animation dran!
        texture.setTranslateY(-20);
        texture.setTranslateX(-15);
        entity.getViewComponent().addChild(texture);

        explodeTimer = newLocalTimer();
        explodeTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // Nach 3 Sekunden explodieren
        if (explodeTimer.elapsed(Duration.seconds(3))) {
            explode();
        }
    }

    private void explode() {
        // Alle Zombies holen
        var zombies = getGameWorld().getEntitiesByType(EntityType.ZOMBIE);

        for (var zombie : zombies) {
            // Abstand zwischen Granate und Zombie berechnen
            double distance = entity.distance(zombie);

            // Wenn der Zombie nah genug ist (Radius 150 Pixel) -> BUMM!
            if (distance < 150) {
                zombie.removeFromWorld(); // Zombie löschen (stirbt)
            }
        }

        // (Optional: Hier einen Explosions-Sound abspielen)
        // play("explosion.wav");

        // Granate selbst löschen
        entity.removeFromWorld();
    }
}

