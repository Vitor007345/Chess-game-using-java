package services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import chessgame.BoardFactory;
import chessgame.ChessBoard;
import chessgame.errors.InvalidFENexception;
import chessgame.errors.MoveNotationException;
import services.errors.*;

/**
 * Utility class responsible for persisting and retrieving the state of a chess game
 * to and from the local file system.
 * It handles the creation of save directories and formats the save file with the 
 * initial board state (FEN or "STANDARD") followed by the sequence of moves.
 */
public class GameSaveFileManager {
    
    /** The relative path to the folder where game saves are stored. */
    private static final String SAVE_FOLDER = "saves";
    
    /** The specific file path used to store the current saved game. */
    private static final String SAVE_FILE = "saves/savegame.txt";
    
    /**
     * Checks whether a valid saved game file currently exists on the disk.
     * * @return True if the save file exists and is not empty, false otherwise.
     */
    public static boolean hasSavedGame() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }
    
    /**
     * Saves the current state of the provided chess board to a text file.
     * The first line of the file will contain the initial FEN string (or "STANDARD"),
     * and the second line will contain the sequence of played moves separated by spaces.
     * * @param board The {@link ChessBoard} instance representing the current game state to be saved.
     * @throws SavingException If an I/O error occurs while creating the folder or writing to the file.
     */
    public static void saveGame(ChessBoard board) throws SavingException{
        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) {
            folder.mkdir(); 
        }

        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            String fen = board.getInitialFEN();
            writer.write((fen == null ? "STANDARD" : fen) + "\n");
            writer.write(board.getMoveSequence() + "\n");
        } catch (IOException e) {
            throw new SavingException(e);
        }
    }
    
    /**
     * Loads a chess game from the saved text file.
     * It reconstructs the initial board state from the first line and then sequentially
     * replays all the stored moves to recreate the exact final position.
     * * @return A fully reconstructed {@link ChessBoard} instance representing the loaded game.
     * @throws LoadingException If the file is missing, empty, corrupted, or if an I/O error occurs during reading.
     */
    public static ChessBoard loadGame() throws LoadingException{
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            throw new LoadingException("File not found");
        }

        try (Scanner scanner = new Scanner(file)) {
            
            if (!scanner.hasNextLine()) {
                throw new LoadingException("File is empty");
            }
            String firstLine = scanner.nextLine().trim();
            ChessBoard board;
            
            if (firstLine.equals("STANDARD")) {
                board = BoardFactory.standardChessBoard();
            } else {
                board = BoardFactory.chessBoardFromFEN(firstLine);
            }
            
            if (scanner.hasNextLine()) {
                String movesLine = scanner.nextLine().trim();
                if (!movesLine.isEmpty()) {
                    String[] moves = movesLine.split("\\s+");
                    for (String moveStr : moves) {
                        board.move(moveStr);
                    }
                }
            }
            return board;
            
        } catch (IOException e) {
            throw new LoadingException(e);
        } catch (InvalidFENexception | MoveNotationException e) {
            throw new LoadingException("Corrupted file");
        }
    }
    
}