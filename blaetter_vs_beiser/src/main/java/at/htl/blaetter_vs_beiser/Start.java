package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import static javafx.application.Application.launch;

public class Start extends GameApplication {

    @Override
    protected void initSettings(GameSettings gameSettings) {

        gameSettings.setTitle("bvb");
    }


}
public static void main(String[] args) {
    launch(args);
}