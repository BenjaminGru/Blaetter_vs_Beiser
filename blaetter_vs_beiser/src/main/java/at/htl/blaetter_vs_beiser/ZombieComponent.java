package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

public class ZombieComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animWalk;
    private String type;

    // Variablen für den Angriff auf Pflanzen
    private Entity targetPlant = null;
    private double attackTimer = 0.0;
    private double freezeTimer = 0.0;

    public ZombieComponent(String type) {
        this.type = type;

        // Alle Zombies nutzen dieselbe Lauf-Animation
        animWalk = new AnimationChannel(FXGL.image("Zombies/Zombie3.png"), 4, 64, 102, Duration.seconds(1), 0, 3);
        texture = new AnimatedTexture(animWalk);
    }

    @Override
    public void onAdded() {
        // 1. Den nackten Zombie hinzufügen
        entity.getViewComponent().addChild(texture);
        texture.loop();

        // 2. HP anpassen (Erbse macht 5 Schaden!)
        if ("HUT_15".equals(type)) {
            entity.setProperty("hp", 300); // Braucht 60 Treffer
            addHut("Zombies/Goldegger_Hut4.png", -15);
        }
        else if ("HUT_10".equals(type)) {
            entity.setProperty("hp", 200); // Braucht 40 Treffer
            addHut("Zombies/Hut2.png", -10);
        }
        else {
            entity.setProperty("hp", 35); // Normaler Zombie: Braucht 20 Treffer
        }
    }

    private void addHut(String pfad, double yOffset) {
        Texture hut = FXGL.texture(pfad);
        hut.setTranslateX(15);
        hut.setTranslateY(yOffset);

        entity.getViewComponent().addChild(hut);
        hut.toFront();
    }
    public void applyFreeze() {
        freezeTimer = 2.0; // Setzt den Timer auf 2 Sekunden!
    }

    // NEUE METHODE: Wird aufgerufen, wenn der Zombie eine Pflanze berührt
    public void setTarget(Entity plant) {
        this.targetPlant = plant;
    }

    @Override
    public void onUpdate(double tpf) {

        // Timer herunterzählen, falls der Zombie friert
        if (freezeTimer > 0) {
            freezeTimer -= tpf;
        }

        // Prüfen, ob der Zombie eine Pflanze vor sich hat
        if (targetPlant != null && targetPlant.isActive()) {

            // PRO-TIPP: Auch das Knabbern wird durch Eis um 50% verlangsamt!
            if (freezeTimer > 0) {
                attackTimer += (tpf * 0.5); // Kaut langsamer
            } else {
                attackTimer += tpf; // Kaut normal
            }

            if (attackTimer >= 1.0) {
                int currentHp = targetPlant.getInt("hp");
                targetPlant.setProperty("hp", currentHp - 10);
                System.out.println("Mampf! Pflanze hat noch " + (currentHp - 10) + " HP");
                attackTimer = 0.0;
            }

        } else {
            targetPlant = null;

            // Laufen berechnen
            double speed = 15.0; // Normales Tempo

            // Wenn er friert, Tempo halbieren!
            if (freezeTimer > 0) {
                speed = 7.5;
            }

            entity.translateX(-speed * tpf);

            if (entity.getRightX() < 0) {
                entity.removeFromWorld();
            }
        }
    }

    /*
    private void addHut(String pfad, double yOffset) {
        Texture hut = FXGL.texture(pfad);
        hut.setTranslateX(15);
        hut.setTranslateY(yOffset);

        // Anstatt nur addChild, füge ihn explizit ganz oben hinzu
        entity.getViewComponent().addChild(hut);
        hut.toFront(); // <--- Zwingt das JavaFX Node nach vorne
    }

     */


}

