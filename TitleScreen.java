import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * TitleScreen.java
 * Displays the main menu with difficulty options and a tutorial button.
 * Uses JFrame, JPanel, JButton, Font, Color, FlowLayout.
 * Grade 12 Computer Science
 */
public class TitleScreen extends JFrame {

    // ── Colour palette ──────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(20,  20,  35);
    private static final Color ACCENT_GREEN = new Color(80, 200, 120);
    private static final Color ACCENT_AMBER = new Color(255, 190,  50);
    private static final Color ACCENT_RED   = new Color(220,  70,  70);
    private static final Color ACCENT_BLUE  = new Color(70, 140, 220);
    private static final Color TEXT_LIGHT   = new Color(230, 230, 245);
    private static final Color TEXT_DIM     = new Color(140, 140, 160);
    private static final Color BTN_BG       = new Color(38,  38,  58);
    private static final Color BTN_HOVER    = new Color(55,  55,  80);

    public TitleScreen() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ── Root panel with dark background ─────────────────────────────────
        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Subtle grid pattern in background
                g.setColor(new Color(30, 30, 48));
                for (int x = 0; x < getWidth(); x += 40)
                    g.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40)
                    g.drawLine(0, y, getWidth(), y);
            }
        };
        root.setBackground(BG_DARK);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        // ── Title label ──────────────────────────────────────────────────────
        JLabel title = new JLabel("💣 MINESWEEPER", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 42));
        title.setForeground(TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select a Difficulty", SwingConstants.CENTER);
        subtitle.setFont(new Font("Courier New", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Difficulty buttons ───────────────────────────────────────────────
        JButton easyBtn   = makeMenuButton("🟢  EASY",   "9×9  —  10 mines",  ACCENT_GREEN);
        JButton mediumBtn = makeMenuButton("🟡  MEDIUM", "16×16 —  40 mines", ACCENT_AMBER);
        JButton hardBtn   = makeMenuButton("🔴  HARD",   "16×30 —  99 mines", ACCENT_RED);
        JButton tutBtn    = makeMenuButton("📖  TUTORIAL", "How to play",     ACCENT_BLUE);

        // ── Wire up actions ──────────────────────────────────────────────────
        easyBtn.addActionListener(e   -> startGame(GameConfig.EASY));
        mediumBtn.addActionListener(e -> startGame(GameConfig.MEDIUM));
        hardBtn.addActionListener(e   -> startGame(GameConfig.HARD));
        tutBtn.addActionListener(e    -> new TutorialScreen(this).setVisible(true));

        // ── Assemble ─────────────────────────────────────────────────────────
        root.add(title);
        root.add(Box.createVerticalStrut(8));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(40));
        root.add(easyBtn);
        root.add(Box.createVerticalStrut(14));
        root.add(mediumBtn);
        root.add(Box.createVerticalStrut(14));
        root.add(hardBtn);
        root.add(Box.createVerticalStrut(30));
        root.add(tutBtn);

        // ── Mini mine-art ─────────────────────────────────────────────────────
        MineArtPanel art = new MineArtPanel();
        art.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(Box.createVerticalStrut(30));
        root.add(art);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null); // centre on screen
    }

    // ── Helper: styled button with a two-line label ──────────────────────────
    private JButton makeMenuButton(String line1, String line2, Color accent) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(getModel().isRollover() ? BTN_HOVER : BTN_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Accent left bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight(), 4, 4);

                // Primary label
                g2.setFont(new Font("Courier New", Font.BOLD, 18));
                g2.setColor(TEXT_LIGHT);
                FontMetrics fm1 = g2.getFontMetrics();
                int y1 = getHeight() / 2 - 4;
                g2.drawString(line1, 22, y1);

                // Secondary label
                g2.setFont(new Font("Courier New", Font.PLAIN, 12));
                g2.setColor(TEXT_DIM);
                g2.drawString(line2, 24, y1 + 18);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(320, 62));
        btn.setMaximumSize(new Dimension(320, 62));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Launch the game window ────────────────────────────────────────────────
    private void startGame(GameConfig cfg) {
        dispose(); // close title screen
        GameWindow game = new GameWindow(cfg);
        game.setVisible(true);
    }
}
