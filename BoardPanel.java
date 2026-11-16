import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * BoardPanel.java
 * Renders the Minesweeper grid and forwards mouse clicks to the Board model.
 * Uses Graphics, Color, Font, FontMetrics, Dimension.
 * Grade 12 Computer Science
 */
public class BoardPanel extends JPanel {

    // ── Cell geometry ─────────────────────────────────────────────────────────
    private static final int CELL_SIZE = 36;      // pixels per cell
    private static final int BORDER    = 2;       // inner border line width

    // ── Colour palette ────────────────────────────────────────────────────────
    private static final Color BG           = new Color(28,  28,  42);
    private static final Color CELL_HIDDEN  = new Color(58,  58,  80);
    private static final Color CELL_HOVER   = new Color(78,  78, 105);
    private static final Color CELL_REVEAL  = new Color(22,  22,  34);
    private static final Color GRID_LINE    = new Color(18,  18,  28);
    private static final Color BORDER_LIGHT = new Color(90,  90, 120);
    private static final Color BORDER_DARK  = new Color(15,  15,  25);

    /** Colours for hint numbers 1-8 (classic Minesweeper palette). */
    private static final Color[] NUMBER_COLORS = {
        null,                          // 0 → no number shown
        new Color( 70, 130, 255),      // 1 → blue
        new Color( 50, 180,  80),      // 2 → green
        new Color(220,  60,  60),      // 3 → red
        new Color( 20,  20, 180),      // 4 → dark blue
        new Color(160,  30,  30),      // 5 → dark red
        new Color( 40, 180, 180),      // 6 → teal
        new Color(200,  80, 200),      // 7 → purple
        new Color(130, 130, 130),      // 8 → grey
    };

    // ── State ──────────────────────────────────────────────────────────────────
    private final Board board;
    private int hoverRow = -1, hoverCol = -1;  // cell under the mouse

    // Callback to notify the parent window of state changes
    private Runnable onStateChange;

    // ── Constructor ───────────────────────────────────────────────────────────
    public BoardPanel(Board board) {
        this.board = board;

        int w = board.getCols() * CELL_SIZE;
        int h = board.getRows() * CELL_SIZE;
        setPreferredSize(new Dimension(w, h));
        setBackground(BG);

        // ── Mouse listener ────────────────────────────────────────────────────
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;
                if (!inBounds(row, col)) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Left click: reveal or chord
                    if (board.getCellState(row, col) == Board.REVEALED)
                        board.chord(row, col);
                    else
                        board.reveal(row, col);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Right click: toggle flag
                    board.toggleFlag(row, col);
                }

                repaint();
                if (onStateChange != null) onStateChange.run();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int newRow = e.getY() / CELL_SIZE;
                int newCol = e.getX() / CELL_SIZE;
                if (newRow != hoverRow || newCol != hoverCol) {
                    hoverRow = newRow;
                    hoverCol = newCol;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                repaint();
            }
        });
    }

    public void setOnStateChange(Runnable callback) {
        this.onStateChange = callback;
    }

    // ── Painting ──────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                drawCell(g2, r, c);
            }
        }
    }

    /** Draws a single cell at (row, col). */
    private void drawCell(Graphics2D g2, int row, int col) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        int state = board.getCellState(row, col);
        boolean isHover = (row == hoverRow && col == hoverCol);

        if (state == Board.REVEALED) {
            drawRevealedCell(g2, row, col, x, y);
        } else if (state == Board.FLAGGED) {
            drawHiddenCell(g2, x, y, false);
            CellGraphics.drawFlag(g2, x, y, CELL_SIZE);
        } else {
            // HIDDEN or QUESTION
            drawHiddenCell(g2, x, y, isHover && board.getGameResult() == Board.ONGOING);
        }

        // Grid line
        g2.setColor(GRID_LINE);
        g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
    }

    /** Draws the background of an unrevealed (covered) cell with 3-D bevel. */
    private void drawHiddenCell(Graphics2D g2, int x, int y, boolean hover) {
        // Fill
        g2.setColor(hover ? CELL_HOVER : CELL_HIDDEN);
        g2.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

        // Light top-left bevel
        g2.setColor(BORDER_LIGHT);
        g2.drawLine(x + 1, y + CELL_SIZE - 2, x + 1, y + 1);
        g2.drawLine(x + 1, y + 1, x + CELL_SIZE - 2, y + 1);

        // Dark bottom-right bevel
        g2.setColor(BORDER_DARK);
        g2.drawLine(x + CELL_SIZE - 2, y + 1, x + CELL_SIZE - 2, y + CELL_SIZE - 2);
        g2.drawLine(x + 1, y + CELL_SIZE - 2, x + CELL_SIZE - 2, y + CELL_SIZE - 2);
    }

    /** Draws a revealed cell: flat background, hint number, bomb, or X-flag. */
    private void drawRevealedCell(Graphics2D g2, int row, int col, int x, int y) {
        boolean isMine = board.isMine(row, col);

        if (isMine) {
            // Check if this was the cell clicked (it has REVEALED state + is mine → loss)
            // We highlight the detonation cell in red inside CellGraphics
            CellGraphics.drawExplodedBomb(g2, x, y, CELL_SIZE);
        } else {
            // Sunken flat background
            g2.setColor(CELL_REVEAL);
            g2.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

            int hint = board.getAdjacent(row, col);
            if (hint > 0) {
                drawHint(g2, x, y, hint);
            }
        }

        // After a loss: show flagged cells that were wrong
        if (board.getGameResult() == Board.LOSS) {
            // (handled by flag draw in drawCell)
        }
    }

    /** Draws the mine-count hint number centred in the cell. */
    private void drawHint(Graphics2D g2, int x, int y, int hint) {
        Font font = new Font("Courier New", Font.BOLD,
                             (int)(CELL_SIZE * 0.55));
        g2.setFont(font);
        g2.setColor(hint <= 8 ? NUMBER_COLORS[hint] : Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        String text = String.valueOf(hint);

        // Centre the text in the cell
        int tx = x + (CELL_SIZE - fm.stringWidth(text)) / 2;
        int ty = y + (CELL_SIZE + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, tx, ty);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < board.getRows() && c >= 0 && c < board.getCols();
    }
}
