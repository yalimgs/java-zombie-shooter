/**
 * Zombie.java
 *
 * Represents a single zombie enemy.
 *
 * Responsibilities:
 *   - Move downward with sinusoidal horizontal wobble
 *   - Detect collision with a bullet (one bullet kills one zombie)
 *   - Detect collision with the player (costs player one life)
 *   - Detect when it walks off the bottom (costs points)
 *   - Draw itself as a creepy green zombie face
 */

import java.awt.Color;

public class Zombie {

    // Constants

    public static final double SIZE        = 0.040; // collision radius
    private static final double BASE_SPEED  = 0.0018;
    private static final double WOBBLE_AMP  = 0.009;
    private static final double WOBBLE_FREQ = 3.5;  // full cycles per second

    // State

    public double x;
    public double y;
    private double startX; // wobble pivot
    private double age;    // seconds alive (used for wobble phase)
    private double speed;

    // Constructor

    /**
     * @param x       spawn x position
     * @param y       spawn y position
     * @param speedMult  speed multiplier (>1 = faster, used for difficulty ramp)
     */

    public Zombie(double x, double y, double speedMult) {
        this.x      = x;
        this.y      = y;
        this.startX = x;
        this.age    = 0;
        this.speed  = BASE_SPEED * speedMult;
    }

    // Update

    /**
     * Move the zombie downward and apply sinusoidal horizontal wobble.
     * @param dt seconds elapsed since last frame
     */

    public void update(double dt) {
        age += dt;
        y   -= speed;
        x    = startX + Math.sin(age * WOBBLE_FREQ * Math.PI * 2.0) * WOBBLE_AMP;
        // Keep inside horizontal bounds
        x = Math.max(SIZE, Math.min(1.0 - SIZE, x));
    }

    // Collision helpers
    // Returns true if this zombie overlaps the given bullet.
    // Removes the zombie from the game (caller handles bullet removal).

    public boolean collidesWithBullet(Bullet b) {
        double dx   = b.x - x;
        double dy   = b.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < SIZE + Bullet.RADIUS;
    }

     // Returns true if this zombie overlaps the player.
     // When true, the caller should: remove zombie, call player.loseLife().

    public boolean collidesWithPlayer(Player p) {
        double dx   = p.x - x;
        double dy   = p.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < SIZE + Player.SIZE;
    }

    // Returns true if this zombie has walked completely off the bottom of the play area

        public boolean escapedBottom() {
        return y < -SIZE;
    }

    // Drawing
    // Draw the zombie as a green zombie face with red eyes and a jagged mouth
    // Visually distinct from the yellow player face.

    public void draw() {
        double s = SIZE;

        // Outer glow (sickly green)
        StdDraw.setPenColor(new Color(80, 160, 20, 55));
        StdDraw.filledCircle(x, y, s * 1.30);

        // Face
        StdDraw.setPenColor(new Color(100, 175, 55));  // zombie green
        StdDraw.filledCircle(x, y, s);

        // Face outline
        StdDraw.setPenColor(new Color(30, 80, 10));
        StdDraw.setPenRadius(0.003);
        StdDraw.circle(x, y, s);
        StdDraw.setPenRadius(0.001);

        // Eyes (hollow, red-glowing, sunken)
        double eyeY = y + s * 0.22;
        double eyeR = s * 0.21;

        // Dark sockets
        StdDraw.setPenColor(new Color(20, 50, 5));
        StdDraw.filledCircle(x - s * 0.35, eyeY, eyeR * 1.15);
        StdDraw.filledCircle(x + s * 0.35, eyeY, eyeR * 1.15);

        // Red irises
        StdDraw.setPenColor(new Color(210, 30, 30));
        StdDraw.filledCircle(x - s * 0.35, eyeY, eyeR);
        StdDraw.filledCircle(x + s * 0.35, eyeY, eyeR);

        // Yellow slit pupils
        StdDraw.setPenColor(new Color(255, 200, 0));
        StdDraw.filledRectangle(x - s * 0.35, eyeY, eyeR * 0.15, eyeR * 0.65);
        StdDraw.filledRectangle(x + s * 0.35, eyeY, eyeR * 0.15, eyeR * 0.65);

        // Nose (two dark holes)
        StdDraw.setPenColor(new Color(40, 90, 15));
        StdDraw.filledCircle(x - s * 0.12, y, s * 0.08);
        StdDraw.filledCircle(x + s * 0.12, y, s * 0.08);

        // Mouth (stitched, jagged)
        // Dark mouth area
        StdDraw.setPenColor(new Color(15, 45, 5));
        StdDraw.filledRectangle(x, y - s * 0.28, s * 0.42, s * 0.12);

        // Crooked teeth
        StdDraw.setPenColor(new Color(210, 205, 180));
        double[] teethX = {-0.30, -0.15, 0.02, 0.18, 0.32};
        double[] teethH = {0.14,  0.09,  0.13, 0.07, 0.11};
        for (int i = 0; i < teethX.length; i++) {
            StdDraw.filledRectangle(x + teethX[i] * s,
                                    y - s * 0.22,
                                    s * 0.065,
                                    s * teethH[i]);
        }

        // Stitches on forehead
        StdDraw.setPenColor(new Color(20, 60, 5));
        StdDraw.setPenRadius(0.002);
        StdDraw.line(x - s * 0.25, y + s * 0.55, x + s * 0.10, y + s * 0.65);
        StdDraw.setPenRadius(0.001);

        // Small stitch marks
        for (double t = 0.0; t <= 1.0; t += 0.33) {
            double sx = (x - s * 0.25) + t * (s * 0.35);
            double sy = (y + s * 0.55) + t * (s * 0.10);
            StdDraw.line(sx - s * 0.03, sy - s * 0.04, sx + s * 0.03, sy + s * 0.04);
        }
    }
}
