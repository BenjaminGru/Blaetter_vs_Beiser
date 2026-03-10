package at.htl.blaetter_vs_beiser;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class ZombieComponent extends Component {

    private double speed = -50;

    @Override
    public void onUpdate(double tpf) {
        // tpf (time per frame) sorgt für gleichmäßige Bewegung
        entity.translateX(speed * tpf);

    }}
