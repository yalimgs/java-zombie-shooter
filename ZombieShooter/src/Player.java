/**
 * Player.java
 *
 * Represents the player character.
 *
 * Responsibilities:
 *   - Store position and movement
 *   - Handle shooting (owns the bullet list)
 *   - Track lives
 *   - Draw the character (smiley face) and life icons
 */

import java.awt.Color;
import java.util.ArrayList;

public class Player {

    // Constants

    public static final double SIZE         = 0.038; // collision radius
    public static final double SPEED        = 0.013;
    public static final double START_X      = 0.5;
    public static final double START_Y      = 0.10;

    private static final int    START_AMMO          = 20;
    private static final int    MAX_AMMO            = 60;
    private static final double AMMO_REGEN_INTERVAL = 0.5; // seconds per bullet
    private static final int    SHOT_COOLDOWN_FRAMES = 15;  // frames between shots

    // Play-area bounds (must stay inside these)
    public static final double BOUND_LEFT   = 0.0;
    public static final double BOUND_RIGHT  = 1.0;
    public static final double BOUND_BOTTOM = 0.0;
    // Top bound is supplied by GameMain (stats bar bottom)

    // State

    public double x;
    public double y;
    private int lives;
    private int ammo;
    private double ammoRegenTimer;
    private int shotCooldown;

    private ArrayList<Bullet> bullets;

    // Constructor

    public Player() {
        reset();
    }

    // Reset player to initial state for a new game.

    public void reset() {
        x              = START_X;
        y              = START_Y;
        lives          = 3;
        ammo           = START_AMMO;
        ammoRegenTimer = 0;
        shotCooldown   = 0;
        bullets        = new ArrayList<>();
    }

    // Movement
    // Move the player based on arrow key input.
    // topBound: y coordinate of the stats bar bottom (player cannot enter it).

    public void move(boolean left, boolean right, boolean up, boolean down, double topBound) {
        if (left)  x -= SPEED;
        if (right) x += SPEED;
        if (up)    y += SPEED;
        if (down)  y -= SPEED;

        // Clamp within play area
        x = Math.max(BOUND_LEFT + SIZE, Math.min(BOUND_RIGHT - SIZE, x));
        y = Math.max(BOUND_BOTTOM + SIZE, Math.min(topBound - SIZE, y));
    }

    // Shooting
    // Attempt to fire a bullet upward.
    // Does nothing if out of ammo or still in cooldown.

    public void shoot() {
        if (ammo > 0 && shotCooldown <= 0) {
            bullets.add(new Bullet(x, y + SIZE + 0.005));
            ammo--;
            shotCooldown = SHOT_COOLDOWN_FRAMES;
        }
    }

    // Update bullets and ammo regeneration. Call once per frame.

    public void updateBullets(double dt, double topBound) {
        // Cool down shot timer
        if (shotCooldown > 0) shotCooldown--;

        // Move bullets; remove those that exit the play area
        java.util.Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (b.y > topBound) it.remove();
        }

        // Regenerate ammo
        ammoRegenTimer += dt;
        if (ammoRegenTimer >= AMMO_REGEN_INTERVAL && ammo < MAX_AMMO) {
            ammo++;
            ammoRegenTimer -= AMMO_REGEN_INTERVAL;
        }
    }

    public ArrayList<Bullet> getBullets() { return bullets; }

    // Lives

    public int  getLives()            { return lives; }
    public void loseLife()            { if (lives > 0) lives--; }
    public void gainLife()            { lives++; }
    public boolean isAlive()          { return lives > 0; }

    // Ammo

    public int getAmmo() { return ammo; }
    public int getMaxAmmo() { return MAX_AMMO; }

    // Drawing
    // Draw the player as a smiley / hero face (distinct from zombies).
    // The character has a round yellow face, blue eyes, and a smile.

    public void draw() {
        double s = SIZE;

        // Outer glow
        StdDraw.setPenColor(new Color(255, 240, 100, 60));
        StdDraw.filledCircle(x, y, s * 1.35);

        // Face
        StdDraw.setPenColor(new Color(255, 220, 50));   // bright yellow
        StdDraw.filledCircle(x, y, s);

        // Face outline
        StdDraw.setPenColor(new Color(180, 140, 0));
        StdDraw.setPenRadius(0.003);
        StdDraw.circle(x, y, s);
        StdDraw.setPenRadius(0.001);

        // Eyes (blue, wide-open, heroic)
        double eyeY = y + s * 0.25;
        double eyeR = s * 0.20;

        // Eye whites
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(x - s * 0.35, eyeY, eyeR);
        StdDraw.filledCircle(x + s * 0.35, eyeY, eyeR);

        // Iris
        StdDraw.setPenColor(new Color(30, 130, 255));
        StdDraw.filledCircle(x - s * 0.35, eyeY, eyeR * 0.65);
        StdDraw.filledCircle(x + s * 0.35, eyeY, eyeR * 0.65);

        // Pupils
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledCircle(x - s * 0.35, eyeY, eyeR * 0.30);
        StdDraw.filledCircle(x + s * 0.35, eyeY, eyeR * 0.30);

        // Eye shine
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledCircle(x - s * 0.30, eyeY + eyeR * 0.30, eyeR * 0.18);
        StdDraw.filledCircle(x + s * 0.40, eyeY + eyeR * 0.30, eyeR * 0.18);

        // Smile
        StdDraw.setPenColor(new Color(160, 80, 0));
        StdDraw.setPenRadius(0.003);

        // Draw smile as a short arc approximated by line segments
        int segments = 10;
        double smileStartAngle = Math.toRadians(200);
        double smileEndAngle   = Math.toRadians(340);
        double smileR          = s * 0.55;
        double smileCY         = y - s * 0.05;
        for (int i = 0; i < segments; i++) {
            double a1 = smileStartAngle + (smileEndAngle - smileStartAngle) * i / segments;
            double a2 = smileStartAngle + (smileEndAngle - smileStartAngle) * (i + 1) / segments;
            StdDraw.line(x + smileR * Math.cos(a1), smileCY + smileR * Math.sin(a1),
                         x + smileR * Math.cos(a2), smileCY + smileR * Math.sin(a2));
        }
        StdDraw.setPenRadius(0.001);

        // Helmet / hat band on top
        StdDraw.setPenColor(new Color(60, 80, 200));
        StdDraw.filledRectangle(x, y + s * 0.78, s * 0.70, s * 0.14);
        StdDraw.setPenColor(new Color(100, 130, 255));
        StdDraw.filledRectangle(x, y + s * 0.78, s * 0.28, s * 0.10);

        // Bullets drawn separately via drawBullets()
    }

    // Draw all active bullets.
    public void drawBullets() {
        for (Bullet b : bullets) b.draw();
    }

    // Draw small heart icons under the player to show remaining lives.
    public void drawLivesIcons() {
        double iconS = 0.013;
        double totalW = (lives - 1) * iconS * 2.8;
        double startX = x - totalW / 2.0;
        double iconY  = y - SIZE - 0.030;

        for (int i = 0; i < lives; i++) {
            double ix = startX + i * iconS * 2.8;
            StdDraw.setPenColor(new Color(255, 80, 110));
            // Two circles + triangle = heart
            StdDraw.filledCircle(ix - iconS * 0.38, iconY + iconS * 0.28, iconS * 0.48);
            StdDraw.filledCircle(ix + iconS * 0.38, iconY + iconS * 0.28, iconS * 0.48);
            double[] hx = {ix - iconS * 0.85, ix + iconS * 0.85, ix};
            double[] hy = {iconY + iconS * 0.28, iconY + iconS * 0.28, iconY - iconS * 0.68};
            StdDraw.filledPolygon(hx, hy);
        }
    }
}
