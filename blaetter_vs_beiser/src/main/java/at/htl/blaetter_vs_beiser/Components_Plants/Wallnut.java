package at.htl.blaetter_vs_beiser.Components_Plants;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

public class Wallnut extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle;  // Die Wackel-Animation
    private AnimationChannel animStill; // Das Standbild (Nur Bild 0)

    public Wallnut() {

        // Die Wackel-Animation (Frames 0 bis 3)
        animIdle = new AnimationChannel(
                image("Plants/" + "Wallnut_spreadsheet.png"),
                4, 70, 105,
                Duration.seconds(1.5), 0, 3
        );

        // NEU: Das Standbild (Nur Frame 0)
        animStill = new AnimationChannel(
                image("Plants/" + "Wallnut_spreadsheet.png"),
                4, 70, 105,
                Duration.seconds(1.0), 0, 0 // Startet bei 0 und endet bei 0
        );

        // Wir erstellen die Textur anfangs mit der Wackel-Animation
        texture = new AnimatedTexture(animIdle);

        texture.setOnCycleFinished(() -> {

            if (texture.getAnimationChannel() == animIdle) {


                texture.loopAnimationChannel(animStill);


                getGameTimer().runOnceAfter(() -> {
                    if (entity != null && entity.isActive()) {

                        texture.playAnimationChannel(animIdle);
                    }
                }, Duration.seconds(7.5));
            }
        });
    }

    @Override
    public void onAdded() {
        texture.setTranslateY(-20);
        texture.setTranslateX(-10);
        entity.getViewComponent().addChild(texture);

        // Spielt die Wackel-Animation direkt beim Spawnen 1x ab
        texture.playAnimationChannel(animIdle);
    }
}