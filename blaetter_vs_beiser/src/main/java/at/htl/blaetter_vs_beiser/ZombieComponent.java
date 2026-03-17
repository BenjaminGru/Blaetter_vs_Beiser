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

    public ZombieComponent() {
        animWalk = new AnimationChannel(
                FXGL.image("Zombies/Zombie3.png"),
                4, 64, 102,
                Duration.seconds(2.85), 0, 3

        );

        texture = new AnimatedTexture(animWalk);
        texture.loop(); // Startet das Beinezappeln
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loop();

            if(false) {
                Texture hut = FXGL.texture("Zombies/Hut2.png");
                hut.setTranslateX(-7);
                hut.setTranslateY(-8);
                entity.getViewComponent().addChild(hut);

                // Hut-Zombies haben z.B. 10 HP
                entity.setProperty("hp", 10);
                entity.setProperty("typ", "Hut_ZOMBIE");
            } else if (true) {
                Texture hut = FXGL.texture("Zombies/Goldegger_Hut4.png");
                hut.setTranslateX(-12);
                hut.setTranslateY(-58);
                entity.getViewComponent().addChild(hut);

                // Hut-Zombies haben z.B. 10 HP
                entity.setProperty("hp", 20);
                entity.setProperty("typ", "Goldegger_Hut_ZOMBIE");

            }else {
            // Normale Zombies haben nur 5 HP
            entity.setProperty("hp", 5);
            entity.setProperty("typ", "NORMALER_ZOMBIE");
        }
    }

    private double timer = 0;

    @Override
    public void onUpdate(double tpf) {
        timer += tpf;

        // Er läuft 2 Sekunden, dann macht er 1 Sekunde Pause
        if (timer % 3.0 < 2.0) {
            entity.translateX(-20 * tpf);
            //texture.play(); // Optional: Animation anmachen
        } else {
            // Hier steht er still
            //texture.stop(); // Optional: Animation pausieren
        }
    }
}