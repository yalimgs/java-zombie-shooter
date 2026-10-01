/**
 * Heart.java
 *
 * Represents a heart.
 *
 * Responsibilities:
 *   - A falling heart pickup.
 *   - Walking into it grants the player +1 life.
 *   - A bullet hitting it destroys it (no life bonus).
 */

import java.awt.Color;

public class Heart {

    // Constants

    public static final double SIZE  = 0.026; // collision radius
    private static final double SPEED = 0.0016;

    // State

    public double x;
    public double y;
    private double bobTimer; // for gentle up-down bobbing effect

    // Constructor

    public Heart(double x, double y) {
        this.x         = x;
        this.y         = y;
        this.bobTimer  = Math.random() * Math.PI * 2; // random phase
    }

    // Update
    // Move the heart downward. Call once per frame.

    public void update(double dt) {
        y -= SPEED;
        bobTimer += dt * 3.0;
    }

    // Collision helpers
    // True if this heart overlaps the given bullet.

    public boolean collidesWithBullet(Bullet b) {
        double dx = b.x - x;
        double dy = b.y - y;
        return Math.sqrt(dx * dx + dy * dy) < SIZE + Bullet.RADIUS;
    }

    // True if this heart overlaps the player.

    public boolean collidesWithPlayer(Player p) {
        double dx = p.x - x;
        double dy = p.y - y;
        return Math.sqrt(dx * dx + dy * dy) < SIZE + Player.SIZE;
    }

    // True if the heart has fallen below the visible play area.

    public boolean escapedBottom() {
        return y < -SIZE;
    }

    // Drawing
    // Draw a glowing pink heart with a gentle bob effect.

    public void draw() {
        double s   = SIZE;
        double bob = Math.sin(bobTimer) * s * 0.12; // subtle bob
        double dy  = y + bob;

        // Outer glow
        StdDraw.setPenColor(new Color(255, 80, 120, 55));
        StdDraw.filledCircle(x, dy, s * 1.55);

        // Heart body: two circles + downward triangle
        StdDraw.setPenColor(new Color(255, 90, 120));
        StdDraw.filledCircle(x - s * 0.44, dy + s * 0.30, s * 0.54);
        StdDraw.filledCircle(x + s * 0.44, dy + s * 0.30, s * 0.54);
        double[] hx = {x - s * 0.92, x + s * 0.92, x};
        double[] hy = {dy + s * 0.30, dy + s * 0.30, dy - s * 0.72};
        StdDraw.filledPolygon(hx, hy);

        // Highlight
        StdDraw.setPenColor(new Color(255, 200, 215, 190));
        StdDraw.filledCircle(x - s * 0.28, dy + s * 0.38, s * 0.22);

        // Sparkle dots
        StdDraw.setPenColor(new Color(255, 230, 240, 180));
        StdDraw.filledCircle(x + s * 0.70, dy + s * 0.55, s * 0.09);
        StdDraw.filledCircle(x + s * 0.90, dy + s * 0.20, s * 0.06);
    }
}
