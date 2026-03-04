package at.htl.blaetter_vs_beiser; // Muss in Zeile 1 stehen

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

// Das Wort "public class Start" ist zwingend erforderlich!
public class Start extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BvB");
    }

    public static void main(String[] args) {
        launch(args);
    }
}