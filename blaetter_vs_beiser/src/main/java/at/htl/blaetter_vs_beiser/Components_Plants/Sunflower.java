package at.htl.blaetter_vs_beiser.Components_Plants;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Sunflower extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle;
    private LocalTimer sunTimer;

    public Sunflower() {
        animIdle = new AnimationChannel(
                image("Plants/Sunflower_spreadsheet.png"),
                4, 70, 105,
                Duration.seconds(1.5), 0, 3
        );
        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        texture.setTranslateY(-20);
        entity.getViewComponent().addChild(texture);

        // Stoppuhr starten
        sunTimer = newLocalTimer();
        sunTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // Alle 15 Sekunden auslösen
        if (sunTimer.elapsed(Duration.seconds(15))) {

            // Geld gutschreiben!
            // (Das greift auf genau dieselbe Variable zu wie in deiner PlantSelectionUI)
            inc("sun", 25);

            // Stoppuhr wieder auf 0 setzen
            sunTimer.capture();
        }
    }
}