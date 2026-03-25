package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

public class ZombieComponent extends Component {

    // --- HIER WERDEN DIE VARIABLEN DEKLARIERT ---
    private AnimatedTexture texture;
    private AnimationChannel animWalk;
    private boolean hatHut;
    // --------------------------------------------

    public ZombieComponent(boolean hatHut) {
        this.hatHut = hatHut;

        // Jetzt werden sie im Konstruktor initialisiert
        animWalk = new AnimationChannel(FXGL.image("Zombies/Zombie3.png"), 4, 64, 102, Duration.seconds(1), 0, 3);
        texture = new AnimatedTexture(animWalk);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loop();

        if (hatHut) {
            Texture hut = FXGL.texture("Zombies/Goldegger_Hut4.png");

            hut.setTranslateX(-13);
            hut.setTranslateY(-55);

            entity.getViewComponent().addChild(hut);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // Bewegung nach links
        entity.translateX(-15 * tpf);

        if (entity.getRightX() < 50) {
            entity.removeFromWorld();
        }
    }
}