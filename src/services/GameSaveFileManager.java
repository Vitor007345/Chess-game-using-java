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

public class GameSaveFileManager {
	
	private static final String SAVE_FOLDER = "saves";
    private static final String SAVE_FILE = "saves/savegame.txt";
    
    public static boolean hasSavedGame() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }
    
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
                board = BoardFactory.standartChessBoard();
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
            throw new LoadingException(e.getMessage());
        } catch (InvalidFENexception | MoveNotationException e) {
        	throw new LoadingException("Corrupted file");
        }
    }
    
    

}
