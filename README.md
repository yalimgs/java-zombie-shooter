# java-zombie-shooter

A 2D top-down arcade survival game developed in Java using the `StdDraw` graphics library. Defend against incoming waves of zombies, manage your ammunition, and grab power-ups to survive as long as possible.

---

## 🎮 Gameplay & Mechanics

### Player & Controls
* **Movement:** Move freely across the play area using the **Arrow Keys** (cannot enter the top HUD bar).
* **Aiming:** The player constantly aims straight upward.
* **Shooting:** Press **Space** to fire a bullet.
  * Starting ammo: **20 bullets**.
  * Bullets regenerate automatically over time.
  * Firing is disabled when ammo reaches 0.
* **Health Display:** Lives are tracked in the top HUD and visually indicated by heart icons.

### Enemies & Dynamic Difficulty
* **Zombie Spawns:** Zombies spawn at random horizontal coordinates beneath the stats bar and advance downward with a wobbling pattern.
* **Dynamic Difficulty:** Spawn rates accelerate over time, progressively increasing the challenge.
* **Combat:** A single bullet hit eliminates a zombie.

### Pickups & Power-ups
* **Falling Hearts:** Hearts periodically drop from the top of the screen at random locations.
* **Collecting:** Walking over a falling heart restores **+1 life**.
* **Shooting:** Stray bullets destroy falling hearts on contact.

---

## 📊 Scoring & Rules

| Event | Effect |
| :--- | :--- |
| **Kill Zombie** | +10 Points |
| **Zombie Escapes (Bottom Screen)** | -5 Points |
| **Zombie Collision** | -1 Life (Removes Zombie) |
| **Pickup Heart** | +1 Life |

* **Starting Lives:** 3
* **Game Over:** Triggered when lives reach 0. A Game Over overlay displays the final score.
* **Score Dynamics:** Scores can drop into negative values.
* **Restart:** Fully resets the score, ammo, lives, and active entities.

---

## 🛠 Tech Stack

* **Language:** Java
* **Library:** Princeton `StdDraw`

---

## 👤 Author
* **GitHub:** [@yalimgs](https://github.com/yalimgs)
