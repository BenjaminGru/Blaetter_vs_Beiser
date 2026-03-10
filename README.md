
---

# 👤 PERSON A – Grid, Pflanzen, Projectiles, Ressourcen

## 🔹 Darf bearbeiten

* `grid/*`
* `plants/*`
* `projectiles/*`
* Sonnen-/Kosten-System
* Plant-bezogene Collision-Reaction

## 🔹 Darf NICHT bearbeiten

* `zombies/*`
* `waves/*`
* Zombie-Bewegungslogik
* Zombie-Spawning

---

## 📁 PERSON A – Dateien

```text
grid/
    GridService.java
    GridCell.java

plants/
    PlantComponent.java
    PeashooterComponent.java
    SunflowerComponent.java

projectiles/
    ProjectileComponent.java

ui/
    PlantSelectionUI.java
```

---

## 🎯 PERSON A – Verantwortlichkeiten

### 1️⃣ GridSystem

* Raster berechnen (Rows, Columns)
* Snap-to-grid Logik
* Prüfen ob Feld frei ist
* Pflanze dort registrieren

---

### 2️⃣ PlantComponent (Base)

```java
public abstract class PlantComponent extends Component {
    protected int health;
    protected int cost;
}
```

---

### 3️⃣ Peashooter

* Schießt alle X Sekunden
* Spawnt Projectile
* Sucht Zombie in derselben Reihe

---

### 4️⃣ Projectile

* Bewegt sich nach rechts
* Prüft Collision mit EntityType.ZOMBIE
* Macht Damage
* Entfernt sich selbst

---

### 5️⃣ Sonnen-System

* Spieler startet mit z.B. 100 Sun
* Abziehen bei Platzierung
* Sunflower generiert Sun

---

# 👤 PERSON B – Zombies, Waves, AI, Game Over

## 🔹 Darf bearbeiten

* `zombies/*`
* `waves/*`
* Zombie-KI
* Spawn-System
* GameOver-Check

## 🔹 Darf NICHT bearbeiten

* `plants/*`
* `grid/*`
* Projectile-Logik
* Kosten-System

---

## 📁 PERSON B – Dateien

```text
zombies/
    ZombieComponent.java
    BasicZombieComponent.java
    FastZombieComponent.java

waves/
    WaveService.java
    SpawnManager.java

core/
    GameOverService.java
```

---

## 🎯 PERSON B – Verantwortlichkeiten

### 1️⃣ ZombieComponent (Base)

```java
public abstract class ZombieComponent extends Component {
    protected int health;
    protected double speed;
}
```

---

### 2️⃣ Movement

* Bewegt sich konstant nach links
* Wenn Kollision mit PLANT:

    * Stoppen
    * Schaden pro Sekunde machen

---

### 3️⃣ WaveSystem

* Timer-basiertes Spawning
* Erhöht Schwierigkeit
* Verschiedene Zombie-Typen

---

### 4️⃣ Game Over

* Wenn Zombie X <= 0
* Spiel stoppen
* UI anzeigen

---

# 🧠 GEMEINSAME REGELN (EXTREM WICHTIG)

## 1️⃣ Gemeinsames Enum (nur EINER erstellt es!)

```java
public enum EntityType {
    PLANT,
    ZOMBIE,
    PROJECTILE
}
```

⚠️ Diese Datei wird nur einmal erstellt und danach nicht mehr geändert.

---

## 2️⃣ GameFactory – Aufteilung

```java
public class GameFactory implements EntityFactory {

    // PERSON A darf nur diese Methoden ändern
    @Spawns("peashooter")
    public Entity spawnPeashooter(SpawnData data) { }

    @Spawns("projectile")
    public Entity spawnProjectile(SpawnData data) { }

    // PERSON B darf nur diese Methoden ändern
    @Spawns("basicZombie")
    public Entity spawnBasicZombie(SpawnData data) { }
}
```

NIEMALS gegenseitig die Methoden anfassen.

---

# 🔥 MAINAPP – HARTE REGEL

## Nur gemeinsam ändern!

Nur diese Methoden:

```java
initSettings()
initGameVars()
initGame()
initInput()
initUI()
```

👉 Vor jeder Änderung:

* kurz sagen: „Ich ändere jetzt initGame()“
* danach committen

---

# 🧨 Git-Regeln

Person A:

```
feature/grid-plants
```

Person B:

```
feature/zombies-waves
```

Merge nur wenn:

* Spiel startet
* Keine Errors
* Kurz getestet

---

# 🏗 Entwicklungs-Reihenfolge (wichtig!)

### PHASE 1

A → Grid anzeigen
B → Dummy-Zombie läuft

### PHASE 2

A → Pflanze platzierbar
B → Zombie stoppt bei Pflanze

### PHASE 3

A → Peashooter schießt
B → Zombie verliert Leben

### PHASE 4

B → Waves
A → UI verbessern

---

# 🏆 Wenn ihr es richtig professionell wollt

Noch sauberer wäre:

A baut nur:

* Defensive Systeme

B baut nur:

* Offensive Systeme

Beide greifen nur über:

* EntityType
* Services
* Events

---
