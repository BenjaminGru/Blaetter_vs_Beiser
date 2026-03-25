package at.htl.blaetter_vs_beiser.Components_Plants;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class Cherrybomb extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle; // Idle = "Stehen/Wackeln"

    public Cherrybomb() {

        animIdle = new AnimationChannel(
                image("Plants/" + "Kirschgranate_spreadsheet.png"),
                3, 97, 105,
                Duration.seconds(1.5), 0, 2
        );

        texture = new AnimatedTexture(animIdle);
        texture.loop(); // Lässt die Animation in Endlosschleife laufen
    }

    @Override
    public void onAdded() {
        // Wenn die Pflanze auf dem Feld spawnt, hängen wir die Animation dran!
        texture.setTranslateY(-20);
        texture.setTranslateX(-15);
        entity.getViewComponent().addChild(texture);
    }
}
