package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimatedTexture; // Wichtig!
import javafx.util.Duration;

public class ZombieComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animWalk;

    public ZombieComponent() {
        animWalk = new AnimationChannel(FXGL.image("SpriteSheet_Zombie.png"), 4, 64, 64, Duration.seconds(1), 0, 3);

        texture = new AnimatedTexture(animWalk);
        texture.loop(); // Sorgt dafür, dass die Beine dauerhaft laufen
    }

    @Override
    public void onAdded() {
        // 3. WICHTIG: Die Textur der View hinzufügen
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        double speed = 100;
        entity.translateX(speed * tpf);
    }
}