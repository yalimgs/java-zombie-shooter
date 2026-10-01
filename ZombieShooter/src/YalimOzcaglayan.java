/**
 * Javadoc description part:
 * Entry point and central game controller for Zombie Shooter.
 *
 * Responsibilities:
 *   - Initialise StdDraw canvas
 *   - Run the main game loop (input → update → draw)
 *   - Spawn zombies (with increasing birth rate over time)
 *   - Spawn heart pickups
 *   - Coordinate collision detection between entities
 *   - Render the stats bar, grid, overlays (start screen / game over)
 *   - Handle restart / play-again
 *
 * Javadoc tags part:
 * @author Yalim Ozcaglayan, Student ID: 2025719117
 * @since Date: 27.04.2026
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class YalimOzcaglayan {

    // Canvas

    private static final int    CANVAS_W = 700;
    private static final int    CANVAS_H = 750;

    // Layout

    // Y coordinate of the bottom edge of the stats bar.

    public static final double STATS_Y = 0.88;
    private static final double STATS_MID = (STATS_Y + 1.0) / 2.0;

    // Timing

    private static final int    FPS = 60;
    private static final double DT  = 1.0 / FPS;

    // Zombie spawning

    private static final double BIRTH_INTERVAL_START = 3.0;  // seconds
    private static final double BIRTH_INTERVAL_MIN   = 0.55; // seconds (fastest)
    private static final double BIRTH_RAMP            = 0.04; // interval reduction per second

    // Heart spawning

    private static final double HEART_INTERVAL = 9.0; // seconds

    // Scoring

    private static final int SCORE_KILL   = 10;
    private static final int SCORE_ESCAPE = -5;

    // Colors

    private static final Color COL_PLAY_BG   = new Color(18, 28, 12);
    private static final Color COL_STATS_BG  = new Color(10, 14, 7);
    private static final Color COL_GRID      = new Color(28, 42, 18);
    private static final Color COL_DIVIDER   = new Color(65, 95, 38);
    private static final Color COL_TEXT      = new Color(200, 230, 175);
    private static final Color COL_TEXT_ACC  = new Color(255, 215, 50);
    private static final Color COL_SCORE_POS = new Color(90, 255, 110);
    private static final Color COL_SCORE_NEG = new Color(255, 75, 75);
    private static final Color COL_AMMO_BG   = new Color(35, 55, 30);
    private static final Color COL_AMMO_FG   = new Color(55, 155, 255);
    private static final Color COL_OVERLAY   = new Color(0, 0, 0, 185);
    private static final Color COL_PANEL_BG  = new Color(8, 18, 5, 235);
    private static final Color COL_START_BG  = new Color(12, 18, 8);

    // Game objects

    private Player         player;
    private ArrayList<Zombie> zombies;
    private ArrayList<Heart>  hearts;

    // Game state

    private int     score;
    private boolean gameOver;
    private boolean started;  // false = show start screen

    // Timers

    private double timeElapsed;
    private double birthTimer;
    private double birthInterval;
    private double heartTimer;

    private final Random rng = new Random();

    // Constructor

    public YalimOzcaglayan() {
        StdDraw.setCanvasSize(CANVAS_W, CANVAS_H);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();
        StdDraw.setTitle("Zombie Shooter — SWE 501");
        player  = new Player();
        zombies = new ArrayList<>();
        hearts  = new ArrayList<>();
        started = false; // show start screen first
    }

    // State management

    // Fully reset all game state to begin a new game.

    private void resetGame() {
        player.reset();
        zombies.clear();
        hearts.clear();
        score         = 0;
        gameOver      = false;
        timeElapsed   = 0;
        birthTimer    = 0;
        birthInterval = BIRTH_INTERVAL_START;
        heartTimer    = 0;
        started       = true;
    }

    // Main loop

    public void run() {
        while (true) {
            if (!started) {
                drawStartScreen();
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) ||
                        StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                    resetGame();
                }
                StdDraw.show();
                StdDraw.pause(1000 / FPS);
                continue;
            }

            if (!gameOver) {
                handleInput();
                update();
            }

            draw();

            if (gameOver) {
                drawGameOverOverlay();
                if (StdDraw.isKeyPressed(KeyEvent.VK_R) ||
                        StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                    resetGame();
                }
            }

            StdDraw.show();
            StdDraw.pause(1000 / FPS);
        }
    }

    // Input

    private void handleInput() {
        boolean left  = StdDraw.isKeyPressed(KeyEvent.VK_LEFT);
        boolean right = StdDraw.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean up    = StdDraw.isKeyPressed(KeyEvent.VK_UP);
        boolean down  = StdDraw.isKeyPressed(KeyEvent.VK_DOWN);

        player.move(left, right, up, down, STATS_Y);

        if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
            player.shoot();
        }
    }

    // Update

    private void update() {
        timeElapsed += DT;

        // Update player (bullets + ammo regen)
        player.updateBullets(DT, STATS_Y);

        // Zombie spawning with increasing birth rate
        birthInterval = Math.max(BIRTH_INTERVAL_MIN,
                BIRTH_INTERVAL_START - timeElapsed * BIRTH_RAMP);
        birthTimer += DT;
        if (birthTimer >= birthInterval) {
            birthTimer -= birthInterval;
            spawnZombie();
        }

        // Heart spawning
        heartTimer += DT;
        if (heartTimer >= HEART_INTERVAL) {
            heartTimer -= HEART_INTERVAL;
            spawnHeart();
        }

        // Update zombies
        updateZombies();

        // Update hearts
        updateHearts();

        // Collision: bullets vs zombies
        checkBulletZombieCollisions();

        // Collision: bullets vs hearts
        checkBulletHeartCollisions();

        // Collision: player vs zombies
        checkPlayerZombieCollisions();

        // Collision: player vs hearts
        checkPlayerHeartCollisions();
    }

    private void spawnZombie() {
        double sx = 0.05 + rng.nextDouble() * 0.90;
        // Speed multiplier increases mildly over time
        double speedMult = 1.0 + timeElapsed * 0.008;
        zombies.add(new Zombie(sx, STATS_Y - Zombie.SIZE - 0.005, speedMult));
    }

    private void spawnHeart() {
        double sx = 0.05 + rng.nextDouble() * 0.90;
        hearts.add(new Heart(sx, STATS_Y - Heart.SIZE - 0.005));
    }

    private void updateZombies() {
        Iterator<Zombie> it = zombies.iterator();
        while (it.hasNext()) {
            Zombie z = it.next();
            z.update(DT);
            if (z.escapedBottom()) {
                score += SCORE_ESCAPE;
                it.remove();
            }
        }
    }

    private void updateHearts() {
        Iterator<Heart> it = hearts.iterator();
        while (it.hasNext()) {
            Heart h = it.next();
            h.update(DT);
            if (h.escapedBottom()) it.remove();
        }
    }

    // Collision detection

    // One bullet kills one zombie; both are removed. Score += 10.

    private void checkBulletZombieCollisions() {
        ArrayList<Bullet> bullets = player.getBullets();
        Iterator<Bullet> bi = bullets.iterator();
        while (bi.hasNext()) {
            Bullet b = bi.next();
            boolean hit = false;
            Iterator<Zombie> zi = zombies.iterator();
            while (zi.hasNext()) {
                Zombie z = zi.next();
                if (z.collidesWithBullet(b)) {
                    zi.remove();
                    score += SCORE_KILL;
                    hit = true;
                    break;
                }
            }
            if (hit) bi.remove();
        }
    }

    // A bullet hitting a heart destroys the heart (no life bonus).

    private void checkBulletHeartCollisions() {
        ArrayList<Bullet> bullets = player.getBullets();
        Iterator<Bullet> bi = bullets.iterator();
        while (bi.hasNext()) {
            Bullet b = bi.next();
            boolean hit = false;
            Iterator<Heart> hi = hearts.iterator();
            while (hi.hasNext()) {
                Heart h = hi.next();
                if (h.collidesWithBullet(b)) {
                    hi.remove();
                    hit = true;
                    break;
                }
            }
            if (hit) bi.remove();
        }
    }

    // Touching a zombie costs 1 life and removes that zombie.

    private void checkPlayerZombieCollisions() {
        Iterator<Zombie> it = zombies.iterator();
        while (it.hasNext()) {
            Zombie z = it.next();
            if (z.collidesWithPlayer(player)) {
                it.remove();
                player.loseLife();
                if (!player.isAlive()) {
                    gameOver = true;
                }
            }
        }
    }

    // Walking into a heart grants +1 life.

    private void checkPlayerHeartCollisions() {
        Iterator<Heart> it = hearts.iterator();
        while (it.hasNext()) {
            Heart h = it.next();
            if (h.collidesWithPlayer(player)) {
                it.remove();
                player.gainLife();
            }
        }
    }

    // Drawing

    private void draw() {
        // Play area background
        StdDraw.setPenColor(COL_PLAY_BG);
        StdDraw.filledRectangle(0.5, STATS_Y / 2.0, 0.5, STATS_Y / 2.0);

        drawGrid();
        drawStatsBar();

        // Divider
        StdDraw.setPenColor(COL_DIVIDER);
        StdDraw.setPenRadius(0.004);
        StdDraw.line(0, STATS_Y, 1, STATS_Y);
        StdDraw.setPenRadius(0.001);

        // Entities (back to front)
        for (Heart  h : hearts)  h.draw();
        for (Zombie z : zombies) z.draw();
        player.drawBullets();
        player.draw();
        player.drawLivesIcons();
    }

    // Faint grid lines in the play area for atmosphere.

    private void drawGrid() {
        StdDraw.setPenColor(COL_GRID);
        StdDraw.setPenRadius(0.0008);
        for (int i = 1; i < 10; i++) {
            double xg = i / 10.0;
            StdDraw.line(xg, 0, xg, STATS_Y);
        }
        for (int i = 1; i < 10; i++) {
            double yg = i / 10.0 * STATS_Y;
            StdDraw.line(0, yg, 1, yg);
        }
        StdDraw.setPenRadius(0.001);
    }

    // Stats bar: lives + ammo (left), score + birth rate (right).

    private void drawStatsBar() {
        // Background
        StdDraw.setPenColor(COL_STATS_BG);
        StdDraw.filledRectangle(0.5, (STATS_Y + 1.0) / 2.0,
                0.5, (1.0 - STATS_Y) / 2.0);

        Font bold  = new Font("Monospaced", Font.BOLD,  16);
        Font plain = new Font("Monospaced", Font.PLAIN, 13);

        // LEFT: Lives and Ammo
        StdDraw.setFont(bold);
        StdDraw.setPenColor(COL_TEXT);
        StdDraw.textLeft(0.02, STATS_MID + 0.026, "Lives: " + player.getLives());

        StdDraw.setFont(plain);
        StdDraw.setPenColor(COL_TEXT_ACC);
        StdDraw.textLeft(0.02, STATS_MID - 0.012, "Ammo:  " + player.getAmmo());

        // Ammo progress bar
        double bx   = 0.115;
        double bY   = STATS_MID - 0.037;
        double bW   = 0.19;
        double bH   = 0.011;
        double fill = (double) player.getAmmo() / player.getMaxAmmo();
        StdDraw.setPenColor(COL_AMMO_BG);
        StdDraw.filledRectangle(bx + bW / 2.0, bY, bW / 2.0, bH / 2.0);
        if (fill > 0) {
            StdDraw.setPenColor(COL_AMMO_FG);
            StdDraw.filledRectangle(bx + fill * bW / 2.0, bY, fill * bW / 2.0, bH / 2.0);
        }

        // RIGHT: Score and Birth rate
        StdDraw.setFont(bold);
        Color sc = score >= 0 ? COL_SCORE_POS : COL_SCORE_NEG;
        StdDraw.setPenColor(sc);
        StdDraw.textRight(0.98, STATS_MID + 0.026, "Score: " + score);

        StdDraw.setFont(plain);
        StdDraw.setPenColor(COL_TEXT);
        double bps = 1.0 / Math.max(birthInterval, 0.01);
        StdDraw.textRight(0.98, STATS_MID - 0.012,
                String.format("Birth: %.1f/s", bps));
    }

    // Overlay screens

    //  Full-screen start / instructions screen.

    private void drawStartScreen() {
        StdDraw.setPenColor(COL_START_BG);
        StdDraw.filledRectangle(0.5, 0.5, 0.5, 0.5);

        // Title
        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 36));
        StdDraw.setPenColor(new Color(100, 225, 75));
        StdDraw.text(0.5, 0.72, "ZOMBIE SHOOTER");

        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 14));
        StdDraw.setPenColor(COL_TEXT);

        String[] lines = {
                "Arrow Keys       Move (4 directions)",
                "Space            Shoot upward",
                "",
                "Kill zombie      +10 points",
                "Miss zombie       -5 points",
                "Collect heart    +1 life",
                "Shoot heart      Destroys heart",
                "",
                "Touching a zombie costs 1 life.",
                "Difficulty increases over time.",
        };
        double lineY = 0.60;
        for (String line : lines) {
            StdDraw.text(0.5, lineY, line);
            lineY -= 0.045;
        }

        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 17));
        StdDraw.setPenColor(COL_TEXT_ACC);
        StdDraw.text(0.5, 0.15, "Press SPACE or ENTER to Start");

        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 11));
        StdDraw.setPenColor(new Color(80, 120, 55));
        StdDraw.text(0.5, 0.10, "SWE 501  —  Assignment 2");
    }

    // Semi-transparent Game Over overlay drawn on top of the paused game.

    private void drawGameOverOverlay() {
        // Dim background
        StdDraw.setPenColor(COL_OVERLAY);
        StdDraw.filledRectangle(0.5, 0.5, 0.5, 0.5);

        // Panel
        StdDraw.setPenColor(COL_PANEL_BG);
        StdDraw.filledRectangle(0.5, 0.50, 0.30, 0.21);
        StdDraw.setPenColor(COL_DIVIDER);
        StdDraw.setPenRadius(0.003);
        StdDraw.rectangle(0.5, 0.50, 0.30, 0.21);
        StdDraw.setPenRadius(0.001);

        // Title
        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 30));
        StdDraw.setPenColor(new Color(255, 55, 55));
        StdDraw.text(0.5, 0.638, "GAME OVER");

        // Score
        StdDraw.setFont(new Font("Monospaced", Font.BOLD, 20));
        Color sc = score >= 0 ? COL_SCORE_POS : COL_SCORE_NEG;
        StdDraw.setPenColor(sc);
        StdDraw.text(0.5, 0.545, "Final Score: " + score);

        // Instruction
        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 14));
        StdDraw.setPenColor(COL_TEXT);
        StdDraw.text(0.5, 0.43, "Press ENTER or R to Play Again");

        // Decorative line
        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 10));
        StdDraw.setPenColor(new Color(80, 140, 45));
        StdDraw.text(0.5, 0.395, "~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
    }

    // Entry point

    public static void main(String[] args) {
        new YalimOzcaglayan().run();
    }
}