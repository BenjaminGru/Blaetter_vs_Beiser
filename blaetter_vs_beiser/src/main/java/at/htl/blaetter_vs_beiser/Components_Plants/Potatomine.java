package at.htl.blaetter_vs_beiser.Components_Plants;

import at.htl.blaetter_vs_beiser.EntityType;
import at.htl.blaetter_vs_beiser.ZombieComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Potatomine extends Component {

    private LocalTimer activationTimer;
    private boolean isActive = false;

    // Variablen für unser Bild / den Spritesheet
    private AnimatedTexture texture;
    private AnimationChannel animBlinking; // Die blinkende Animation
    private AnimationChannel animStill;    // Das Standbild (wenn sie noch nicht scharf ist)

    public Potatomine() {
        // 1. Lade deinen Spritesheet (BITTE DATEINAMEN PRÜFEN!)
        // Ich gehe davon aus, dass er "Potatomine_spreadsheet.png" heißt.

        // Die blinkende Animation (spielt alle 4 Frames ab)
        animBlinking = new AnimationChannel(
                image("Plants/Potatomine_spreadsheet.png"),
                4, 70, 105, // Wie immer: Prüfe, ob die Maße 70x105 bei dir stimmen!
                Duration.seconds(1.0), 0, 3
        );

        // Das Standbild (zeigt einfach nur Frame 0 an, wo das Licht aus ist)
        animStill = new AnimationChannel(
                image("Plants/Potatomine_spreadsheet.png"),
                4, 70, 105,
                Duration.seconds(1.0), 0, 0
        );

        texture = new AnimatedTexture(animStill);
    }

    @Override
    public void onAdded() {
        // 2. Sichtbar machen! (Das hat vorher gefehlt)
        texture.setTranslateY(-20);
        entity.getViewComponent().addChild(texture);

        // Mine startet im Standbild (Licht aus)
        texture.playAnimationChannel(animStill);

        // 3. Stoppuhr starten
        activationTimer = newLocalTimer();
        activationTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // 4. Prüfen ob 5 Sekunden um sind und sie noch NICHT scharf ist
        if (!isActive && activationTimer.elapsed(Duration.seconds(5))) {
            isActive = true;

            // TADA! Jetzt ist sie scharf und fängt an rot zu blinken!
            texture.loopAnimationChannel(animBlinking);
            System.out.println("Kartoffelmine ist scharf!");
        }

        // 5. Wenn sie scharf ist, nach Zombies Ausschau halten
        if (isActive) {
            var zombies = getGameWorld().getEntitiesByType(EntityType.ZOMBIE);

            for (var zombie : zombies) {
                // Berühren sich die Hitboxen von Mine und Zombie?
                if (entity.isColliding(zombie)) {

                    // 1. Zombie wird sofort gelöscht
                    zombie.removeFromWorld();

                    // 2. Die Mine zerstört sich selbst
                    entity.removeFromWorld();

                    System.out.println("BOOOOM! Kartoffelmine hat den Zombie weggesprengt!");

                    // Optional: play("explosion.wav");
                }
            }
        }
    }
}