/**
 * Bullet.java
 *
 * Represents a bullet.
 *
 * Responsibilities:
 *   - A single bullet fired by the player, traveling straight upward.
 *   - Destroys zombies or hearts.
 */

import java.awt.Color;

public class Bullet {

    // Constants

    public static final double SPEED  = 0.022;
    public static final double RADIUS = 0.008;

    // State

    public double x;
    public double y;

    // Constructor

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Update
    // Move the bullet upward by one frame.

    public void update() {
        y += SPEED;
    }

    // Drawing
    // Draw the bullet as a glowing yellow projectile.

    public void draw() {
        // Outer glow
        StdDraw.setPenColor(new Color(255, 255, 100, 110));
        StdDraw.filledCircle(x, y, RADIUS * 2.4);
        // Inner bright core
        StdDraw.setPenColor(new Color(255, 230, 50));
        StdDraw.filledCircle(x, y, RADIUS);
        // White hot center
        StdDraw.setPenColor(new Color(255, 255, 230));
        StdDraw.filledCircle(x, y, RADIUS * 0.45);
    }
}
