package at.htl.blaetter_vs_beiser;

import java.util.List;

public class LevelData {
    public String levelName;
    public List<ZombieSpawn> waves; // Muss exakt so im JSON stehen!

    public static class ZombieSpawn {
        public double time;
        public String type;
        public int lane;
    }
}