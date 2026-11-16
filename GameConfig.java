/**
 * GameConfig.java
 * Stores the three difficulty configurations.
 * Grade 12 Computer Science
 */
public enum GameConfig {

    // rows, cols, mines
    EASY  ( 9,  9, 10),
    MEDIUM(16, 16, 40),
    HARD  (16, 30, 99);

    public final int rows;
    public final int cols;
    public final int mines;

    GameConfig(int rows, int cols, int mines) {
        this.rows  = rows;
        this.cols  = cols;
        this.mines = mines;
    }

    public String displayName() {
        switch (this) {
            case EASY:   return "Easy   (9×9, 10 mines)";
            case MEDIUM: return "Medium (16×16, 40 mines)";
            case HARD:   return "Hard   (16×30, 99 mines)";
            default:     return "";
        }
    }
}
