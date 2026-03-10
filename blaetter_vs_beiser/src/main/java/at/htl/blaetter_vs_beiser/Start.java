package at.htl.blaetter_vs_beiser; // Muss in Zeile 1 stehen

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

// Das Wort "public class Start" ist zwingend erforderlich!
public class Start extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BvB");
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new Zombie());

        spawn("enemy", getAppWidth(), 100);
        spawn("enemy", getAppWidth(), 200);
        spawn("enemy", getAppWidth(), 0);





    }
}