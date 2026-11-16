import java.awt.*;
import java.awt.geom.*;

/**
 * CellGraphics.java
 * Dedicated class for drawing custom cell icons (flag, bomb, etc.)
 * using Java2D Graphics, Color, Shape, and geometric primitives.
 *
 * All methods are static so they can be called from any renderer
 * without instantiation.
 *
 * Grade 12 Computer Science
 */
public class CellGraphics {

    // ── Prevent instantiation ─────────────────────────────────────────────────
    private CellGraphics() {}

    // ── Colour constants ──────────────────────────────────────────────────────
    public static final Color FLAG_POLE   = new Color(200, 200, 200);
    public static final Color FLAG_RED    = new Color(220,  50,  50);
    public static final Color FLAG_SHADOW = new Color(140,  30,  30);
    public static final Color BOMB_BODY   = new Color(25,   25,  25);
    public static final Color BOMB_SHINE  = new Color(90,   90,  90);
    public static final Color BOMB_FUSE   = new Color(160, 120,  40);
    public static final Color BOMB_SPARK  = new Color(255, 210,  50);
    public static final Color WRONG_RED   = new Color(200,  30,  30);

    /**
     * Draws a triangular flag on a pole, centred inside the given rectangle.
     *
     * @param g    Graphics context
     * @param x    top-left x of the cell
     * @param y    top-left y of the cell
     * @param size width/height of the cell in pixels
     */
    public static void drawFlag(Graphics g, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        double s = size;

        // ── Pole ──────────────────────────────────────────────────────────────
        int poleX  = (int)(x + s * 0.50);
        int poleY1 = (int)(y + s * 0.18);
        int poleY2 = (int)(y + s * 0.82);

        g2.setStroke(new BasicStroke(Math.max(1.5f, size / 14f)));
        g2.setColor(FLAG_POLE);
        g2.drawLine(poleX, poleY1, poleX, poleY2);

        // ── Flag triangle ─────────────────────────────────────────────────────
        int[] px = {
            poleX,
            (int)(x + s * 0.82),
            poleX
        };
        int[] py = {
            poleY1,
            (int)(y + s * 0.38),
            (int)(y + s * 0.56)
        };

        // Shadow/base
        g2.setColor(FLAG_SHADOW);
        g2.fillPolygon(new int[]{px[0]+1, px[1]+1, px[2]+1},
                       new int[]{py[0]+1, py[1]+1, py[2]+1}, 3);

        // Main flag
        g2.setColor(FLAG_RED);
        g2.fillPolygon(px, py, 3);

        // ── Base of pole ─────────────────────────────────────────────────────
        int baseW = (int)(s * 0.36);
        int baseH = (int)(s * 0.08);
        int baseX = poleX - baseW / 2;
        int baseY = poleY2 - baseH;

        g2.setColor(FLAG_POLE);
        g2.fillRoundRect(baseX, baseY, baseW, baseH, 4, 4);

        g2.dispose();
    }

    /**
     * Draws a cartoon-style bomb with a fuse, centred inside the given rectangle.
     *
     * @param g      Graphics context
     * @param x      top-left x of the cell
     * @param y      top-left y of the cell
     * @param size   width/height of the cell in pixels
     * @param spark  if true, draw a yellow spark at the fuse tip
     */
    public static void drawBomb(Graphics g, int x, int y, int size, boolean spark) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        double s = size;

        // ── Body ──────────────────────────────────────────────────────────────
        int r    = (int)(s * 0.30);          // radius
        int cx   = (int)(x + s * 0.50);      // centre x
        int cy   = (int)(y + s * 0.58);      // centre y

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(cx - r + 2, cy - r + 2, r * 2, r * 2);

        // Body fill
        g2.setColor(BOMB_BODY);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Shine highlight
        int hr = Math.max(2, r / 3);
        g2.setColor(BOMB_SHINE);
        g2.fillOval(cx - r + r / 4, cy - r + r / 5, hr, hr);

        // ── Spikes (8-direction cross) ────────────────────────────────────────
        g2.setColor(BOMB_BODY);
        g2.setStroke(new BasicStroke(Math.max(1.5f, size / 12f),
                                      BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_ROUND));
        double spikeLen = s * 0.14;
        int[][] spikeDirs = {{0,-1},{0,1},{-1,0},{1,0},
                              {1,-1},{-1,1},{1,1},{-1,-1}};
        for (int[] d : spikeDirs) {
            double normLen = Math.sqrt(d[0]*d[0] + d[1]*d[1]);
            int ex = (int)(cx + (r + spikeLen) * d[0] / normLen);
            int ey = (int)(cy + (r + spikeLen) * d[1] / normLen);
            int sx2 = (int)(cx + r * d[0] / normLen);
            int sy2 = (int)(cy + r * d[1] / normLen);
            g2.drawLine(sx2, sy2, ex, ey);
        }

        // ── Fuse ─────────────────────────────────────────────────────────────
        int fuseStartX = cx;
        int fuseStartY = cy - r;
        int fuseMidX   = (int)(cx + s * 0.08);
        int fuseMidY   = (int)(cy - r - s * 0.10);
        int fuseEndX   = (int)(cx + s * 0.14);
        int fuseEndY   = (int)(cy - r - s * 0.22);

        g2.setColor(BOMB_FUSE);
        g2.setStroke(new BasicStroke(Math.max(1.5f, size / 14f),
                                      BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_ROUND));
        // Curved fuse via quadratic bezier using GeneralPath
        GeneralPath fuse = new GeneralPath();
        fuse.moveTo(fuseStartX, fuseStartY);
        fuse.quadTo(fuseMidX, fuseMidY, fuseEndX, fuseEndY);
        g2.draw(fuse);

        // ── Spark at fuse tip ────────────────────────────────────────────────
        if (spark) {
            drawSpark(g2, fuseEndX, fuseEndY, (int)(s * 0.12));
        }

        g2.dispose();
    }

    /**
     * Draws a small starburst spark.
     */
    private static void drawSpark(Graphics2D g2, int cx, int cy, int radius) {
        g2.setColor(BOMB_SPARK);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int rays = 6;
        for (int i = 0; i < rays; i++) {
            double angle = Math.PI * 2 * i / rays;
            int x1 = (int)(cx + (radius * 0.3) * Math.cos(angle));
            int y1 = (int)(cy + (radius * 0.3) * Math.sin(angle));
            int x2 = (int)(cx + radius * Math.cos(angle));
            int y2 = (int)(cy + radius * Math.sin(angle));
            g2.drawLine(x1, y1, x2, y2);
        }
        // Hot centre
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - 2, cy - 2, 4, 4);
    }

    /**
     * Draws a red X over a cell to indicate a wrongly-placed flag.
     */
    public static void drawWrongFlag(Graphics g, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        drawFlag(g2, x, y, size);   // draw the flag underneath

        int pad = size / 5;
        g2.setColor(WRONG_RED);
        g2.setStroke(new BasicStroke(Math.max(2f, size / 8f),
                                      BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_ROUND));
        g2.drawLine(x + pad, y + pad, x + size - pad, y + size - pad);
        g2.drawLine(x + size - pad, y + pad, x + pad, y + size - pad);

        g2.dispose();
    }

    /**
     * Draws the exploded bomb cell (red background + bomb).
     */
    public static void drawExplodedBomb(Graphics g, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Red background for the cell that was clicked
        g2.setColor(new Color(200, 50, 50));
        g2.fillRect(x, y, size, size);
        drawBomb(g2, x, y, size, true);
        g2.dispose();
    }
}
