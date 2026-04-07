package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

public class ZombieComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animWalk;
    private String type;

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

        // 2. HP und Hut-Bild basierend auf dem Typ setzen
        if ("HUT_15".equals(type)) {
            entity.setProperty("hp", 15);
            addHut("Zombies/Goldegger_Hut4.png", -15);
        }
        else if ("HUT_10".equals(type)) {
            entity.setProperty("hp", 10);
            addHut("Zombies/Hut2.png", -10);
        }
        else {
            entity.setProperty("hp", 5); // Normaler Zombie
        }
    }

    private void addHut(String pfad, double yOffset) {
        Texture hut = FXGL.texture(pfad);
        hut.setTranslateX(15);
        hut.setTranslateY(yOffset);

        // Anstatt nur addChild, füge ihn explizit ganz oben hinzu
        entity.getViewComponent().addChild(hut);
        hut.toFront(); // <--- Zwingt das JavaFX Node nach vorne
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(-15 * tpf);

        if (entity.getRightX() < 0) {
            entity.removeFromWorld();
        }
    }
}