package chessgame;

import chessgame.pieces.*;

import chessgame.moves.Move;

import java.util.ArrayList;

public class BoardFactory {
	
	
	public static ChessBoard standartChessBoard() {
		Piece[][] matrix = new Piece[8][8];
		return new ChessBoard(
	            matrix,
	            generateInitialRooks(matrix, true),    // whiteRooks
	            generateInitialRooks(matrix, false),   // blackRooks
	            generateInitialPawns(matrix, true),    // whitePawns
	            generateInitialPawns(matrix, false),   // blackPawns
	            generateInitialKnights(matrix, true),  // whiteKnights
	            generateInitialKnights(matrix, false), // blackKnights
	            generateInitialBishops(matrix, true),  // whiteBishops
	            generateInitialBishops(matrix, false), // blackBishops
	            generateInitialQueen(matrix, true),    // whiteQueens
	            generateInitialQueen(matrix, false),   // blackQueens
	            generateInitialKing(matrix, true),     // whiteKing
	            generateInitialKing(matrix, false),    // blackKing
	            new ArrayList<Move>()
	        );
		
	}
	
	
	
	
	private static ArrayList<Rook> generateInitialRooks(Piece[][] matrix, boolean isWhite) {
	    int row = isWhite ? 0 : 7;
	    
	    ArrayList<Rook> rooks = new ArrayList<>();
	    rooks.add(new Rook(row, 0, isWhite));
	    rooks.add(new Rook(row, 7, isWhite));
	    
	    matrix[row][0] = rooks.get(0);
	    matrix[row][7] = rooks.get(1);
	    
	    return rooks;
	}

	private static ArrayList<Pawn> generateInitialPawns(Piece[][] matrix, boolean isWhite) {
	    int row = isWhite ? 1 : 6;
	    
	    ArrayList<Pawn> pawns = new ArrayList<>();
	    for(int i = 0; i < 8; i++) {
	        Pawn p = new Pawn(row, i, isWhite);
	        pawns.add(p);
	        matrix[row][i] = p;
	    }
	    return pawns;
	}

	private static ArrayList<Knight> generateInitialKnights(Piece[][] matrix, boolean isWhite) {
	    int row = isWhite ? 0 : 7;
	    
	    ArrayList<Knight> knights = new ArrayList<>();
	    knights.add(new Knight(row, 1, isWhite));
	    knights.add(new Knight(row, 6, isWhite));
	    
	    matrix[row][1] = knights.get(0);
	    matrix[row][6] = knights.get(1);
	    
	    return knights;
	}

	private static ArrayList<Bishop> generateInitialBishops(Piece[][] matrix, boolean isWhite) {
	    int row = isWhite ? 0 : 7;
	    
	    ArrayList<Bishop> bishops = new ArrayList<>();
	    bishops.add(new Bishop(row, 2, isWhite));
	    bishops.add(new Bishop(row, 5, isWhite));
	    
	    matrix[row][2] = bishops.get(0);
	    matrix[row][5] = bishops.get(1);
	    
	    return bishops;
	}

	private static ArrayList<Queen> generateInitialQueen(Piece[][] matrix, boolean isWhite) {
	    int row = isWhite ? 0 : 7;
	    
	    ArrayList<Queen> queens = new ArrayList<>();
	    queens.add(new Queen(row, 3, isWhite));
	    
	    matrix[row][3] = queens.get(0);
	    
	    return queens;
	}

	private static King generateInitialKing(Piece[][] matrix, boolean isWhite) {
	    int row = isWhite ? 0 : 7;
	    
	    King king = new King(row, 4, isWhite);
	    matrix[row][4] = king;
	    
	    return king;
	}

	
}
