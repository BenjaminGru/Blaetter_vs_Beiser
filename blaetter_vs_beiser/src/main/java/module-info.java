module at.htl.blaetter_vs_beiser {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens at.htl.blaetter_vs_beiser to javafx.fxml;
    exports at.htl.blaetter_vs_beiser;
}