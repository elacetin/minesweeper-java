import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * TutorialScreen.java
 * A scrollable how-to-play guide with live mini-illustrations.
 * Uses JFrame, JPanel, JScrollPane, Graphics, Color, Font, FontMetrics.
 * Grade 12 Computer Science
 */
public class TutorialScreen extends JFrame {

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color BG         = new Color(20, 20, 35);
    private static final Color CARD_BG    = new Color(32, 32, 50);
    private static final Color HEADER_FG  = new Color(230, 230, 245);
    private static final Color BODY_FG    = new Color(170, 170, 195);
    private static final Color ACCENT     = new Color(80, 200, 120);
    private static final Color ACCENT2    = new Color(255, 190, 50);
    private static final Color DIVIDER    = new Color(50, 50, 75);

    public TutorialScreen(JFrame parent) {
        setTitle("How to Play — Minesweeper");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ── Title ──────────────────────────────────────────────────────────
        content.add(makeTitle("📖  HOW TO PLAY"));
        content.add(Box.createVerticalStrut(6));
        content.add(makeSubtitle("Master Minesweeper in five easy steps"));
        content.add(Box.createVerticalStrut(24));

        // ── Step cards ────────────────────────────────────────────────────────
        content.add(makeCard("1 — The Goal",
            "Clear the entire board without detonating any mines.\n"
            + "Numbers tell you how many mines touch that cell.\n"
            + "Use logic to deduce which cells are safe!",
            new GoalIllustration()));

        content.add(Box.createVerticalStrut(16));

        content.add(makeCard("2 — Left Click to Reveal",
            "Left-click any hidden cell to uncover it.\n"
            + "If it shows a blank (no number), all its neighbours\n"
            + "are automatically revealed in a chain reaction.",
            new RevealIllustration()));

        content.add(Box.createVerticalStrut(16));

        content.add(makeCard("3 — Right Click to Flag",
            "Right-click a hidden cell to plant a 🚩 flag.\n"
            + "Flags mark cells you believe contain mines.\n"
            + "Right-click again to remove the flag.",
            new FlagIllustration()));

        content.add(Box.createVerticalStrut(16));

        content.add(makeCard("4 — Reading the Numbers",
            "Each number shows how many of its 8 neighbours are mines.\n"
            + "Combine overlapping clues to narrow down mine locations.\n"
            + "A blank cell means zero adjacent mines — safe to expand!",
            new NumberIllustration()));

        content.add(Box.createVerticalStrut(16));

        content.add(makeCard("5 — Chording",
            "On a revealed number, left-click when you have flagged\n"
            + "exactly that many neighbours. All other neighbours\n"
            + "reveal automatically — great for speed-solving!",
            new ChordIllustration()));

        content.add(Box.createVerticalStrut(24));

        // ── Difficulty guide ─────────────────────────────────────────────────
        content.add(makeDifficultyGuide());
        content.add(Box.createVerticalStrut(24));

        // ── Close button ──────────────────────────────────────────────────────
        JButton close = new JButton("✔  Got it!");
        close.setFont(new Font("Courier New", Font.BOLD, 15));
        close.setForeground(new Color(20, 20, 35));
        close.setBackground(ACCENT);
        close.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        close.setFocusPainted(false);
        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> dispose());
        content.add(close);

        // ── Scroll wrapper ────────────────────────────────────────────────────
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);

        setContentPane(scroll);
        setSize(620, 700);
        setLocationRelativeTo(parent);
    }

    // ── Factory helpers ───────────────────────────────────────────────────────
    private JLabel makeTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Courier New", Font.BOLD, 26));
        lbl.setForeground(HEADER_FG);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel makeSubtitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Courier New", Font.PLAIN, 14));
        lbl.setForeground(new Color(130, 130, 155));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Builds a "card" with a heading, multi-line body text, and an illustration.
     */
    private JPanel makeCard(String heading, String body, JPanel illustration) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Text block
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(CARD_BG);

        JLabel headLbl = new JLabel(heading);
        headLbl.setFont(new Font("Courier New", Font.BOLD, 15));
        headLbl.setForeground(ACCENT);

        JTextArea bodyArea = new JTextArea(body);
        bodyArea.setEditable(false);
        bodyArea.setFocusable(false);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBackground(CARD_BG);
        bodyArea.setForeground(BODY_FG);
        bodyArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        bodyArea.setBorder(null);

        text.add(headLbl);
        text.add(Box.createVerticalStrut(6));
        text.add(bodyArea);

        card.add(text, BorderLayout.CENTER);

        if (illustration != null) {
            illustration.setBackground(CARD_BG);
            illustration.setPreferredSize(new Dimension(90, 88));
            card.add(illustration, BorderLayout.EAST);
        }

        return card;
    }

    /** Builds a colour-coded difficulty reference table. */
    private JPanel makeDifficultyGuide() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel title = new JLabel("Difficulty Reference");
        title.setFont(new Font("Courier New", Font.BOLD, 15));
        title.setForeground(ACCENT2);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        String[][] rows = {
            {"🟢  Easy",   "9 × 9",   "10 mines",  "~12% density"},
            {"🟡  Medium", "16 × 16", "40 mines",  "~16% density"},
            {"🔴  Hard",   "16 × 30", "99 mines",  "~21% density"},
        };
        Color[] rowColors = {
            new Color(80, 200, 120),
            new Color(255, 190, 50),
            new Color(220, 70, 70)
        };

        for (int i = 0; i < rows.length; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
            row.setBackground(CARD_BG);
            for (String cell : rows[i]) {
                JLabel lbl = new JLabel(cell);
                lbl.setFont(new Font("Courier New", Font.PLAIN, 13));
                lbl.setForeground(rowColors[i]);
                lbl.setPreferredSize(new Dimension(130, 22));
                row.add(lbl);
            }
            panel.add(row);
        }
        return panel;
    }

    // =========================================================================
    // ── Inline illustration panels ───────────────────────────────────────────
    // =========================================================================

    /** Shows a 3×3 mini-grid with numbers and a bomb. */
    private static class GoalIllustration extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cs = 24;
            int ox = (getWidth()  - cs * 3) / 2;
            int oy = (getHeight() - cs * 3) / 2;
            int[][] hints = {{1, 1, 0},{1, -1, 1},{1, 1, 0}};
            Color[] numColors = {null, new Color(70,130,255), null};
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int x = ox + c * cs, y = oy + r * cs;
                    g.setColor(new Color(22, 22, 34));
                    g.fillRect(x, y, cs, cs);
                    g.setColor(new Color(50, 50, 75));
                    g.drawRect(x, y, cs, cs);
                    int h = hints[r][c];
                    if (h == -1) {
                        CellGraphics.drawBomb(g, x, y, cs, false);
                    } else if (h > 0) {
                        g.setFont(new Font("Courier New", Font.BOLD, 13));
                        g.setColor(new Color(70, 130, 255));
                        g.drawString(String.valueOf(h), x + 7, y + 17);
                    }
                }
            }
        }
    }

    /** Shows cells expanding from a blank centre. */
    private static class RevealIllustration extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cs = 24, ox = (getWidth()-cs*3)/2, oy = (getHeight()-cs*3)/2;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int x = ox + c*cs, y = oy + r*cs;
                    boolean centre = (r==1&&c==1);
                    g.setColor(centre ? new Color(22,22,34) : new Color(58,58,80));
                    g.fillRect(x, y, cs, cs);
                    g.setColor(new Color(50,50,75));
                    g.drawRect(x, y, cs, cs);
                    if (centre) {
                        g.setFont(new Font("Courier New",Font.BOLD,11));
                        g.setColor(new Color(100,100,130));
                        g.drawString("·", x+9, y+16);
                    }
                }
            }
            // Arrow
            g.setColor(new Color(80,200,120));
            g.drawString("→", ox + cs*3 + 2, oy + cs + 17);
        }
    }

    /** Shows a flagged cell next to a hidden cell. */
    private static class FlagIllustration extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cs = 36;
            int x1 = (getWidth()/2) - cs - 4, y1 = (getHeight()-cs)/2;
            int x2 = (getWidth()/2) + 4,       y2 = y1;

            // Hidden cell
            g.setColor(new Color(58,58,80));
            g.fillRect(x1, y1, cs, cs);
            g.setColor(new Color(50,50,75));
            g.drawRect(x1, y1, cs, cs);

            // Flagged cell
            g.setColor(new Color(58,58,80));
            g.fillRect(x2, y2, cs, cs);
            g.setColor(new Color(50,50,75));
            g.drawRect(x2, y2, cs, cs);
            CellGraphics.drawFlag(g, x2, y2, cs);
        }
    }

    /** Shows revealed cells with numbers 1–4. */
    private static class NumberIllustration extends JPanel {
        private static final Color[] COLS = {
            new Color(70,130,255), new Color(50,180,80),
            new Color(220,60,60),  new Color(20,20,180)
        };
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cs = 22;
            int ox = (getWidth()-cs*2)/2, oy = (getHeight()-cs*2)/2;
            for (int i = 0; i < 4; i++) {
                int x = ox + (i%2)*cs, y = oy + (i/2)*cs;
                g.setColor(new Color(22,22,34));
                g.fillRect(x, y, cs, cs);
                g.setColor(new Color(50,50,75));
                g.drawRect(x, y, cs, cs);
                g.setFont(new Font("Courier New",Font.BOLD,13));
                g.setColor(COLS[i]);
                g.drawString(String.valueOf(i+1), x+5, y+16);
            }
        }
    }

    /** Shows a revealed number cell with flagged neighbours. */
    private static class ChordIllustration extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cs = 28;
            int ox = (getWidth()-cs*3)/2, oy = (getHeight()-cs*2)/2;
            // Top row: flag, revealed "1", flag
            drawRevCell(g, ox,       oy, "🏳", false);
            drawRevCell(g, ox+cs,    oy, "1",  true);
            drawRevCell(g, ox+cs*2,  oy, "🏳", false);
            // Bottom row: hidden
            for (int c = 0; c < 3; c++) {
                g.setColor(new Color(58,58,80));
                g.fillRect(ox+c*cs, oy+cs, cs, cs);
                g.setColor(new Color(50,50,75));
                g.drawRect(ox+c*cs, oy+cs, cs, cs);
            }
        }
        private void drawRevCell(Graphics g, int x, int y, String txt, boolean reveal) {
            g.setColor(reveal ? new Color(22,22,34) : new Color(58,58,80));
            g.fillRect(x, y, 28, 28);
            g.setColor(new Color(50,50,75));
            g.drawRect(x, y, 28, 28);
            if (reveal) {
                g.setFont(new Font("Courier New",Font.BOLD,14));
                g.setColor(new Color(70,130,255));
                g.drawString(txt, x+8, y+19);
            } else {
                CellGraphics.drawFlag(g, x, y, 28);
            }
        }
    }
}
