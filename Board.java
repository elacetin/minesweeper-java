import java.util.Random;

/**
 * Board.java
 * Holds all game state: mine positions, cell states, flag positions.
 * The flood-fill reveal uses RECURSION to uncover safe empty regions.
 * Grade 12 Computer Science
 */
public class Board {

    // ── Cell state constants ──────────────────────────────────────────────────
    public static final int HIDDEN    = 0;  // covered, untouched
    public static final int REVEALED  = 1;  // uncovered by player
    public static final int FLAGGED   = 2;  // player placed flag
    public static final int QUESTION  = 3;  // question mark (optional)

    // ── Game-result constants ─────────────────────────────────────────────────
    public static final int ONGOING = 0;
    public static final int WIN     = 1;
    public static final int LOSS    = 2;

    // ── Board data ────────────────────────────────────────────────────────────
    private final int rows, cols, totalMines;
    private final boolean[][] mines;      // true if this cell contains a mine
    private final int[][]    adjacent;   // number of adjacent mines (0-8)
    private final int[][]    cellState;  // HIDDEN / REVEALED / FLAGGED / QUESTION

    private int   flagCount;
    private int   revealedCount;
    private int   gameResult;
    private boolean firstClick; // first click never hits a mine

    // ── Constructor ───────────────────────────────────────────────────────────
    public Board(GameConfig cfg) {
        this.rows       = cfg.rows;
        this.cols       = cfg.cols;
        this.totalMines = cfg.mines;

        mines      = new boolean[rows][cols];
        adjacent   = new int[rows][cols];
        cellState  = new int[rows][cols];  // all default to HIDDEN (0)

        flagCount     = 0;
        revealedCount = 0;
        gameResult    = ONGOING;
        firstClick    = true;
    }

    // ── Mine placement (deferred until first click) ───────────────────────────
    /**
     * Places mines randomly, guaranteeing the first clicked cell is safe.
     */
    private void placeMines(int safeRow, int safeCol) {
        Random rng = new Random();
        int placed = 0;
        while (placed < totalMines) {
            int r = rng.nextInt(rows);
            int c = rng.nextInt(cols);
            // Skip the safe cell and its immediate neighbours
            if (!mines[r][c] && !isNeighbour(r, c, safeRow, safeCol)) {
                mines[r][c] = true;
                placed++;
            }
        }
        computeAdjacency();
    }

    private boolean isNeighbour(int r, int c, int sr, int sc) {
        return Math.abs(r - sr) <= 1 && Math.abs(c - sc) <= 1;
    }

    /** Fills the adjacent[][] array for every cell. */
    private void computeAdjacency() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mines[r][c]) {
                    adjacent[r][c] = -1; // sentinel: this IS a mine
                    continue;
                }
                int count = 0;
                for (int[] d : directions()) {
                    int nr = r + d[0];
                    int nc = c + d[1];
                    if (inBounds(nr, nc) && mines[nr][nc]) count++;
                }
                adjacent[r][c] = count;
            }
        }
    }

    // ── Primary action: reveal a cell ─────────────────────────────────────────
    /**
     * Called when the player left-clicks a cell.
     * Triggers recursive flood-fill if the cell has no adjacent mines.
     */
    public void reveal(int row, int col) {
        if (gameResult != ONGOING) return;
        if (!inBounds(row, col))   return;
        if (cellState[row][col] != HIDDEN) return;

        // First click: place mines now
        if (firstClick) {
            placeMines(row, col);
            firstClick = false;
        }

        if (mines[row][col]) {
            // Hit a mine → game over
            cellState[row][col] = REVEALED;
            gameResult = LOSS;
            revealAllMines();
            return;
        }

        // Recursive reveal
        revealRecursive(row, col);
        checkWin();
    }

    /**
     * RECURSIVE flood-fill reveal.
     * Base cases:
     *   - cell out of bounds
     *   - cell already revealed or flagged
     *   - cell is a mine
     * Recursive case:
     *   - reveal this cell; if adjacent == 0, recurse into all 8 neighbours.
     */
    private void revealRecursive(int row, int col) {
        // ── Base cases ────────────────────────────────────────────────────────
        if (!inBounds(row, col))              return;
        if (cellState[row][col] != HIDDEN)   return;
        if (mines[row][col])                  return;

        // ── Reveal this cell ──────────────────────────────────────────────────
        cellState[row][col] = REVEALED;
        revealedCount++;

        // ── Recursive case: expand if blank (no adjacent mines) ───────────────
        if (adjacent[row][col] == 0) {
            for (int[] d : directions()) {
                revealRecursive(row + d[0], col + d[1]); // ← RECURSION
            }
        }
    }

    // ── Flag / unflag ─────────────────────────────────────────────────────────
    public void toggleFlag(int row, int col) {
        if (gameResult != ONGOING)          return;
        if (!inBounds(row, col))             return;
        if (cellState[row][col] == REVEALED) return;

        if (cellState[row][col] == HIDDEN) {
            cellState[row][col] = FLAGGED;
            flagCount++;
        } else if (cellState[row][col] == FLAGGED) {
            cellState[row][col] = HIDDEN;
            flagCount--;
        }
    }

    // ── Chord: reveal neighbours when flagged count matches hint ──────────────
    /**
     * "Chording" – if a revealed cell's adjacent-mine count equals the number
     * of flagged neighbours, reveal all un-flagged neighbours.
     */
    public void chord(int row, int col) {
        if (gameResult != ONGOING)                return;
        if (cellState[row][col] != REVEALED)      return;
        if (adjacent[row][col] <= 0)              return;

        int flaggedNeighbours = 0;
        for (int[] d : directions()) {
            int nr = row + d[0], nc = col + d[1];
            if (inBounds(nr, nc) && cellState[nr][nc] == FLAGGED)
                flaggedNeighbours++;
        }

        if (flaggedNeighbours == adjacent[row][col]) {
            for (int[] d : directions()) {
                int nr = row + d[0], nc = col + d[1];
                if (inBounds(nr, nc) && cellState[nr][nc] == HIDDEN)
                    revealRecursive(nr, nc);
            }
            checkWin();
        }
    }

    // ── Win detection ─────────────────────────────────────────────────────────
    private void checkWin() {
        // Win condition: every non-mine cell has been revealed
        if (revealedCount == rows * cols - totalMines) {
            gameResult = WIN;
            // Auto-flag remaining mines
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (mines[r][c] && cellState[r][c] == HIDDEN) {
                        cellState[r][c] = FLAGGED;
                        flagCount++;
                    }
        }
    }

    /** Reveals all mine positions (used after a loss). */
    private void revealAllMines() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (mines[r][c] && cellState[r][c] != FLAGGED)
                    cellState[r][c] = REVEALED;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /** All 8 compass directions as row/col deltas. */
    private int[][] directions() {
        return new int[][] {
            {-1,-1},{-1, 0},{-1, 1},
            { 0,-1},        { 0, 1},
            { 1,-1},{ 1, 0},{ 1, 1}
        };
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int  getRows()           { return rows; }
    public int  getCols()           { return cols; }
    public int  getTotalMines()     { return totalMines; }
    public int  getFlagCount()      { return flagCount; }
    public int  getMinesRemaining() { return totalMines - flagCount; }
    public int  getGameResult()     { return gameResult; }
    public boolean isMine(int r, int c)      { return mines[r][c]; }
    public int  getAdjacent(int r, int c)    { return adjacent[r][c]; }
    public int  getCellState(int r, int c)   { return cellState[r][c]; }
    public boolean isFirstClick()            { return firstClick; }
}
