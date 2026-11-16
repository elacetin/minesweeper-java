import javax.swing.*;
import java.awt.*;

/**
 * MineArtPanel.java
 * Displays a decorative row of flag and bomb icons on the title screen.
 * Demonstrates CellGraphics as a standalone rendering class.
 * Grade 12 Computer Science
 */
public class MineArtPanel extends JPanel {

    private static final int ICON_SIZE = 44;
    private static final int ICONS     = 5;
    private static final int GAP       = 8;

    public MineArtPanel() {
        int w = ICONS * ICON_SIZE + (ICONS - 1) * GAP;
        setPreferredSize(new Dimension(w, ICON_SIZE));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Alternating bomb / flag pattern
        for (int i = 0; i < ICONS; i++) {
            int x = i * (ICON_SIZE + GAP);
            if (i % 2 == 0) {
                CellGraphics.drawBomb(g, x, 0, ICON_SIZE, i == 2); // centre one sparkles
            } else {
                CellGraphics.drawFlag(g, x, 0, ICON_SIZE);
            }
        }
    }
}
