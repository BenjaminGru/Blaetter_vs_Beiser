package at.htl.blaetter_vs_beiser.Components_Plants;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class Snowpeashooter extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle; // Idle = "Stehen/Wackeln"

    public Snowpeashooter() {

        animIdle = new AnimationChannel(
                image("Plants/" + "Snowpeashooter_spreadsheet.png"),
                4, 70, 105,
                Duration.seconds(1.5), 0, 3
        );

        texture = new AnimatedTexture(animIdle);
        texture.loop(); // Lässt die Animation in Endlosschleife laufen
    }

    @Override
    public void onAdded() {
        // Wenn die Pflanze auf dem Feld spawnt, hängen wir die Animation dran!
        texture.setTranslateY(-20);
        texture.setTranslateX(-10);
        entity.getViewComponent().addChild(texture);
    }
}
