package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ZombieComponent extends Component {

    // --- ANIMATIONS-VARIABLEN ---
    private AnimatedTexture texture;
    private AnimationChannel animWalk;

    // --- LOGIK-VARIABLEN ---
    private int hp = 100;
    private Entity targetPlant = null;
    private LocalTimer eatTimer;

    public ZombieComponent() {
        // 1. Spritesheet laden (Achtung: Passe den Ordnernamen an, falls das Bild z.B. in "Zombies/" liegt!)
        // Und passe die Zahlen (4, 70, 105) an die echten Maße deines Zombie-Spritesheets an!
        animWalk = new AnimationChannel(
                image("zombie3.png"), // <-- Hier ist dein Bild!
                4, 64, 102,
                Duration.seconds(1.5), 0, 3
        );

        texture = new AnimatedTexture(animWalk);
        texture.loop(); // Zombie wackelt in Endlosschleife
    }

    @Override
    public void onAdded() {
        // 2. Den wackelnden Zombie sichtbar machen!
        texture.setTranslateY(-20); // (Falls er zu hoch/tief schwebt, hier anpassen)
        entity.getViewComponent().addChild(texture);

        // Timer starten
        eatTimer = newLocalTimer();
        eatTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // 3. LOGIK: Laufen oder Fressen?
        if (targetPlant == null) {
            // Laufen
            entity.translateX(-30 * tpf);
        } else {
            // Fressen
            if (targetPlant.isActive()) {
                if (eatTimer.elapsed(Duration.seconds(1.0))) {
                    int currentHp = targetPlant.getInt("hp");
                    targetPlant.setProperty("hp", currentHp - 20);
                    eatTimer.capture();
                }
            } else {
                targetPlant = null; // Pflanze tot -> weiterlaufen
            }
        }
    }

    // Wird aufgerufen, wenn die Erbse trifft
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            entity.removeFromWorld(); // Zombie stirbt
        }
    }

    // Wird aufgerufen, wenn er an eine Pflanze stößt
    public void startEating(Entity plant) {
        this.targetPlant = plant;
    }
}