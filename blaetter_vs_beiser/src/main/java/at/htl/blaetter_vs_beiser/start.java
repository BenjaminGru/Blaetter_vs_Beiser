package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.application.Application;

public class start extends GameApplication {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    protected void initSettings(GameSettings gameSettings) {

        gameSettings.setTitle("bvb");
    }
    @Override
    protected void initInput() {}

    @Override
    protected void initGame() {}




}
