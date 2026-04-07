package at.htl.blaetter_vs_beiser.Components_Plants;

import at.htl.blaetter_vs_beiser.EntityType;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Peashooter extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle;
    private LocalTimer shootTimer;

    public Peashooter() {
        // 1. Animation initialisieren (Achtung: Prüfe, ob dein Dateiname exakt stimmt!)
        animIdle = new AnimationChannel(
                image("Plants/Peashooter_spreadsheet.png"),
                4, 70, 105,
                Duration.seconds(1.5), 0, 3
        );

        texture = new AnimatedTexture(animIdle);
        texture.loop(); // Endlosschleife für die Animation
    }

    @Override
    public void onAdded() {
        // 2. Das Bild wieder an die Pflanze heften (Macht den Spritesheet sichtbar!)
        texture.setTranslateY(-20);
        entity.getViewComponent().addChild(texture);

        // 3. Stoppuhr für das Schießen starten
        shootTimer = newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // 4. Sind 1.5 Sekunden vergangen?
        if (shootTimer.elapsed(Duration.seconds(1.5))) {

            // Wenn ja: Prüfen, ob ein Zombie da ist
            if (isZombieInLane()) {
                shoot(); // FEUER!
            }

            // WICHTIG: Die Stoppuhr MUSS zwingend hier zurückgesetzt werden,
            // damit er nach weiteren 1.5 Sekunden nochmal schießen kann!
            shootTimer.capture();
        }
    }

    private boolean isZombieInLane() {
        var zombies = getGameWorld().getEntitiesByType(EntityType.ZOMBIE);

        for (var zombie : zombies) {
            // Ich habe die Toleranz hier auf 80 erhöht. Wenn der Zombie beim
            // Laufen leicht hoch/runter wackelt, erkennt die Pflanze ihn jetzt trotzdem!
            boolean sameLane = Math.abs(zombie.getY() - entity.getY()) < 50;

            // Ist der Zombie rechts von der Pflanze?
            boolean isRight = zombie.getX() > entity.getX();

            if (sameLane && isRight) {
                return true;
            }
        }
        return false; // Kein Zombie in Sicht
    }

    private void shoot() {
        // Wir spawnen die Erbse ein Stückchen weiter rechts und etwas tiefer,
        // damit sie aus dem "Mund" der Pflanze kommt.
        spawn("pea", entity.getX() + 40, entity.getY() + 15);
    }
}