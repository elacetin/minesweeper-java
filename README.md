# Minesweeper (Java Swing)

A desktop Minesweeper game written in Java with Swing and Java2D, built for Grade 12 Computer Science.

## Features

- Three difficulty levels: Easy (9x9, 10 mines), Medium (16x16, 40 mines), Hard (16x30, 99 mines)
- Title screen with difficulty buttons and a scrollable in-game tutorial with small illustrations
- Left click to reveal a cell, right click to place or remove a flag, and click a number to "chord" (reveal all neighbours once enough flags are placed)
- Recursive flood-fill that opens empty regions automatically
- First click is always safe: mines are placed after the first click and avoid that cell and its neighbours
- HUD with a mine counter, a timer, a reset button (the face changes on a win or loss) and a menu button
- Flag and bomb icons drawn in code with Java2D shapes (no image files)

## How to run

**In BlueJ:** open the project, right-click the `Minesweeper` class and choose `void main(String[] args)`.

**From the command line:**

```
javac *.java
java Minesweeper
```

## Project structure

| Class | Purpose |
| --- | --- |
| `Minesweeper` | Entry point; launches the title screen |
| `TitleScreen` | Main menu with difficulty and tutorial buttons |
| `TutorialScreen` | How-to-play guide with illustrations |
| `GameConfig` | Enum holding rows, columns and mines for each difficulty |
| `GameWindow` | Game frame, HUD, timer and reset logic |
| `Board` | Game state and rules: mine placement, reveal (recursive), flags, chording, win/loss |
| `BoardPanel` | Draws the grid and handles mouse input |
| `CellGraphics` | Static methods that draw the flag and bomb icons |
| `MineArtPanel` | Decorative row of icons on the title screen |

The game logic (`Board`) is kept separate from the drawing code (`BoardPanel`, `CellGraphics`).

## Known limitations / ideas for next steps

- Chording on a wrongly placed flag does not end the game; it should reveal the mine and count as a loss.
- The question-mark cell state and the "wrong flag" icon exist in the code but are not used yet.
- There is no best-time record or custom board size.

## Author

Ela Yagmur Cetin, University of Toronto, Electrical Engineering
