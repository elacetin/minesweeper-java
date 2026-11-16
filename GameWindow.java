import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GameWindow.java
 * The main game frame: a HUD bar (mine counter, smiley reset, timer)
 * above the BoardPanel.  Uses JFrame, JPanel, JButton, Timer, Font,
 * Color, FlowLayout, Dimension, BorderLayout.
 * Grade 12 Computer Science
 */
public class GameWindow extends JFrame {

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color HUD_BG     = new Color(20,  20,  35);
    private static final Color TEXT_COLOR = new Color(230, 230, 245);
    private static final Color DIGIT_BG   = new Color(10,  10,  18);
    private static final Color DIGIT_RED  = new Color(220,  60,  60);

    // ── State ─────────────────────────────────────────────────────────────────
    private final GameConfig config;
    private Board      board;
    private BoardPanel boardPanel;

    private int     secondsElapsed = 0;
    private boolean timerRunning   = false;
    private javax.swing.Timer swingTimer;

    // HUD components
    private JLabel mineCountLabel;
    private JLabel timerLabel;
    private JButton faceButton;

    // ── Constructor ───────────────────────────────────────────────────────────
    public GameWindow(GameConfig config) {
        this.config = config;
        setTitle("Minesweeper — " + config.displayName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        buildUI();
        resetGame();

        pack();
        setLocationRelativeTo(null);
    }

    // ── UI Construction ───────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 35));

        // ── HUD ───────────────────────────────────────────────────────────────
        JPanel hud = buildHUD();
        add(hud, BorderLayout.NORTH);
    }

    private JPanel buildHUD() {
        JPanel hud = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        hud.setBackground(HUD_BG);
        hud.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                       new Color(50, 50, 75)));

        // Mine counter
        mineCountLabel = makeDigitLabel("000");
        JLabel mineIcon = new JLabel("💣");
        mineIcon.setFont(new Font("Dialog", Font.PLAIN, 20));

        // Smiley / reset button
        faceButton = makeFaceButton();

        // Timer
        timerLabel = makeDigitLabel("000");
        JLabel timerIcon = new JLabel("⏱");
        timerIcon.setFont(new Font("Dialog", Font.PLAIN, 20));

        // Back to menu
        JButton menuBtn = makeSmallButton("☰ Menu");
        menuBtn.addActionListener(e -> {
            swingTimer.stop();
            dispose();
            new TitleScreen().setVisible(true);
        });

        hud.add(mineIcon);
        hud.add(mineCountLabel);
        hud.add(Box.createHorizontalStrut(20));
        hud.add(faceButton);
        hud.add(Box.createHorizontalStrut(20));
        hud.add(timerLabel);
        hud.add(timerIcon);
        hud.add(Box.createHorizontalStrut(10));
        hud.add(menuBtn);

        return hud;
    }

    /** Creates a 7-segment-style digit display label. */
    private JLabel makeDigitLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(DIGIT_BG);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g.setColor(DIGIT_RED);
                g.setFont(new Font("Courier New", Font.BOLD, 20));
                FontMetrics fm = g.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(getText(), tx, ty);
            }
        };
        lbl.setPreferredSize(new Dimension(64, 34));
        lbl.setOpaque(false);
        return lbl;
    }

    /** Smiley button that resets the game. */
    private JButton makeFaceButton() {
        JButton btn = new JButton("🙂") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                            ? new Color(80, 80, 110)
                            : new Color(50, 50, 75));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(200, 200, 220));
                g2.setFont(new Font("Dialog", Font.PLAIN, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                              (getWidth()  - fm.stringWidth(getText())) / 2,
                              (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(44, 34));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> resetGame());
        return btn;
    }

    private JButton makeSmallButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Courier New", Font.PLAIN, 12));
        btn.setForeground(new Color(180, 180, 200));
        btn.setBackground(new Color(40, 40, 60));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Game reset ────────────────────────────────────────────────────────────
    private void resetGame() {
        // Stop existing timer
        if (swingTimer != null) swingTimer.stop();
        secondsElapsed = 0;
        timerRunning   = false;

        // Remove old board panel if present
        if (boardPanel != null) {
            getContentPane().remove(boardPanel);
        }

        // Fresh board
        board = new Board(config);
        boardPanel = new BoardPanel(board);
        boardPanel.setOnStateChange(this::onBoardChanged);
        getContentPane().add(boardPanel, BorderLayout.CENTER);

        // Reset HUD
        updateHUD();
        faceButton.setText("🙂");

        // Swing timer fires every second
        swingTimer = new javax.swing.Timer(1000, e -> {
            if (timerRunning && board.getGameResult() == Board.ONGOING) {
                secondsElapsed = Math.min(secondsElapsed + 1, 999);
                updateTimerLabel();
            }
        });
        swingTimer.start();

        revalidate();
        repaint();
        pack();
    }

    // ── Called by BoardPanel after every click ────────────────────────────────
    private void onBoardChanged() {
        // Start timer on first action
        if (!timerRunning && !board.isFirstClick()) {
            timerRunning = true;
        }

        updateHUD();

        int result = board.getGameResult();
        if (result == Board.WIN) {
            timerRunning = false;
            faceButton.setText("😎");
            boardPanel.repaint();
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this,
                    "🎉  You cleared the board!\n"
                    + "Time: " + secondsElapsed + " seconds",
                    "You Win!", JOptionPane.INFORMATION_MESSAGE));
        } else if (result == Board.LOSS) {
            timerRunning = false;
            faceButton.setText("😵");
            boardPanel.repaint();
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this,
                    "💥  You hit a mine!\n"
                    + "Time: " + secondsElapsed + " seconds",
                    "Game Over", JOptionPane.ERROR_MESSAGE));
        }
    }

    // ── HUD updaters ──────────────────────────────────────────────────────────
    private void updateHUD() {
        updateMineLabel();
        updateTimerLabel();
    }

    private void updateMineLabel() {
        int remaining = board.getMinesRemaining();
        // Clamp to -99..999
        remaining = Math.max(-99, Math.min(999, remaining));
        String text = remaining < 0
                      ? String.format("-%02d", -remaining)
                      : String.format("%03d", remaining);
        mineCountLabel.setText(text);
        mineCountLabel.repaint();
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("%03d", secondsElapsed));
        timerLabel.repaint();
    }
}
