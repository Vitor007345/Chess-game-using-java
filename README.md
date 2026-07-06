# Java Chess Engine

A fully-featured, lightweight chess engine and graphical user interface (GUI) built from scratch using Java Swing. This application combines an object-oriented approach with low-level bitfield optimizations to implement complete chess logic, match history persistence, and user preferences.

## 🚀 Features

- **Complete Chess Rules Implementation**: Handles complex mechanics such as castling, *en passant* captures, and pawn promotion.
- **Draw Detection**: Full enforcement of the 50-move rule (halfmove clock) and Threefold Repetition tracking using reduced FEN strings.
- **Dual Input Modes**: Move pieces intuitively by clicking squares on the grid or by typing standard algebraic notation (e.g., `e4`, `Nf3`, `exd5`, `O-O`) into the move input field.
- **Match Persistence (Save/Load)**: Automatically saves game state (initial position plus the full sequence of moves) to local files, allowing players to safely close the app and resume their match later.
- **Memory Optimization**: Utilizes an ultra-efficient 8-bit bitfield representation per piece to encode coordinates, color, and movement history in a single `byte`.
- **Customizable UI Settings**: 
  - Dynamic board coordinate rendering.
  - Optional automatic board rotation/reversal based on active player perspective.
  - Optional auto-promotion to Queen.

---

## 📂 Project Architecture

The project is modularly structured into specific packages to segregate engine logic from UI rendering and file storage:

- **`main`**: Bootstraps the application and safely runs the GUI on the Event Dispatch Thread (EDT).
- **`chessgame`**: Contains the main `ChessBoard` state machine, `BoardFactory` for generating positions (including standard setups or custom FEN strings), and game configuration models.
- **`chessgame.pieces`**: Contains the polymorphic piece models (`Piece`, `King`, `Queen`, `Rook`, `Bishop`, `Knight`, `Pawn`) utilizing bitfield storage.
- **`chessgame.moves`**: Manages the `Move` class to record historical metrics needed for full match undoing.
- **`chessgame.errors`**: Custom domain exceptions like `MoveNotationException` and `InvalidFENexception`.
- **`services`**: Manages file I/O operations (`GameSaveFileManager` and `SettingsFileManager`) using structured local text files.
- **`vision`**: Built with Java Swing; coordinates window management for the Main Menu, active Matchboard GUI, and Settings configurations.

---

## 🛠️ Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher (JDK 21 recommended).

---

## 🏃 How to Run

### Via IDE (Eclipse / IntelliJ IDEA)
1. Clone this repository to your local workspace:
   ```bash
   git clone https://github.com/Vitor007345/Chess-game-using-java.git
   ```
2. Open the project folder inside your preferred IDE.
3. Locate the `Main.java` file inside the `src/main/` directory.
4. Run the file as a standard Java Application.

### Via Command Line
1. Open your terminal and navigate to the project's root folder.
2. Compile all source files into a build target directory:
   ```bash
   javac -d bin src/main/Main.java src/chessgame/*.java src/chessgame/pieces/*.java src/chessgame/moves/*.java src/chessgame/errors/*.java src/services/*.java src/services/errors/*.java src/vision/*.java
   ```
3. Run the compiled application:
   ```bash
   java -cp bin main.Main
   ```

---

## 🎮 How to Play

1. **Main Menu**: Start a fresh "New Game", "Continue" your last match, or paste a raw FEN string into "Import Position" to study custom puzzles or endgames.
2. **Playing Moves**: 
   - **Click Interface**: Click the piece you want to move (it highlights in yellow) and then click its target destination square. If promoting a pawn, an interactive selection popup will appear.
   - **Text Interface**: Type the move in the text entry bar at the bottom using standard algebraic notation rules and press `Enter` or click "Send Move".
3. **Controls**:
   - **Undo Move**: Roll back the last played ply instantly.
   - **Reverse Board**: Manually flip the visual map layout perspective.
   - **Go to Menu**: Exit the active match. The engine automatically saves your progress safely upon exiting or closing the window.
