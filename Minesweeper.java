/**
 * Minesweeper.java
 * Main entry point - launches the title screen.
 * Grade 12 Computer Science
 */
public class Minesweeper {
    public static void main(String[] args) {
        // Launch on the Event Dispatch Thread for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            TitleScreen title = new TitleScreen();
            title.setVisible(true);
        });
    }
}
