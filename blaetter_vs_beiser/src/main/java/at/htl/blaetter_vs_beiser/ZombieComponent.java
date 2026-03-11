package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.util.Duration;

public class ZombieComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animWalk;

    public ZombieComponent() {
        animWalk = new AnimationChannel(
                FXGL.image("Zombie3.png"),
                4, 64, 102,
                Duration.seconds(2.85), 0, 3

        );

        texture = new AnimatedTexture(animWalk);
        texture.loop(); // Startet das Beinezappeln
    }

    @Override
    public void onAdded() {
        // Die Textur der Entity hinzufügen
        entity.getViewComponent().addChild(texture);

        // Da das Originalbild nach links schaut, ist scale(1) korrekt
        entity.setScaleX(1);
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