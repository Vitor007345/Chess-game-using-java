package chessgame;

import chessgame.pieces.*;
import chessgame.errors.InvalidFENexception;
import chessgame.moves.Move;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardFactory {
	
	
	public static ChessBoard standartChessBoard() {
		Piece[][] matrix = new Piece[8][8];
		
		HashMap<String, Integer> standardHistory = new HashMap<>();
        standardHistory.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -", 1);
		
		return new ChessBoard(
	            matrix,
	            generateInitialRooks(matrix, true),    // whiteRooks
	            generateInitialRooks(matrix, false),   // blackRooks
	            generateInitialPawns(matrix, true),   // whitePawns
	            generateInitialPawns(matrix, false),   // blackPawns
	            generateInitialKnights(matrix, true),  // whiteKnights
	            generateInitialKnights(matrix, false), // blackKnights
	            generateInitialBishops(matrix, true),  // whiteBishops
	            generateInitialBishops(matrix, false), // blackBishops
	            generateInitialQueen(matrix, true),    // whiteQueens
	            generateInitialQueen(matrix, false),   // blackQueens
	            generateInitialKing(matrix, true),     // whiteKing
	            generateInitialKing(matrix, false),    // blackKing
	            new ArrayList<Move>(),
	            true,
	            null,
	            0,
	            1, //In chess number of fullmoves starts as one
	            new ArrayList<Integer>(),
	            standardHistory
	        );
		
	}
	
	//FEN: Forsyth-Edwards Notation
	public static ChessBoard chessBoardFromFEN(String fen) throws InvalidFENexception {
		// Separates the 6 FEN blocks
		String[] parts = fen.trim().split("\\s+");
		if (parts.length != 6) {
			throw new InvalidFENexception("Invalid FEN string! Must contain exactly 6 blocks.");
		}

		String piecePlacement = parts[0];
		String activeColor = parts[1];
		String castlingRights = parts[2];
		String enPassantTarget = parts[3];
		
		int halfmoveClock;
		int fullmoveNumber;
		try {
			halfmoveClock = Integer.parseInt(parts[4]);
			fullmoveNumber = Integer.parseInt(parts[5]);
		} catch (NumberFormatException e) {
			throw new InvalidFENexception("Halfmove clock or Fullmove number are not valid numbers.");
		}

		//Initializes the empty structures
		Piece[][] matrix = new Piece[8][8];
		
		ArrayList<Rook> whiteRooks = new ArrayList<>();
		ArrayList<Rook> blackRooks = new ArrayList<>();
		ArrayList<Knight> whiteKnights = new ArrayList<>();
		ArrayList<Knight> blackKnights = new ArrayList<>();
		ArrayList<Bishop> whiteBishops = new ArrayList<>();
		ArrayList<Bishop> blackBishops = new ArrayList<>();
		ArrayList<Queen> whiteQueens = new ArrayList<>();
		ArrayList<Queen> blackQueens = new ArrayList<>();
		ArrayList<Pawn> whitePawns = new ArrayList<>();
		ArrayList<Pawn> blackPawns = new ArrayList<>();
		King whiteKing = null;
		King blackKing = null;

		// ==========================================
		// 1. POPULATING THE PIECE MATRIX
		// ==========================================
		// FEN starts at chess rank 8 (index 7) and goes down to rank 1 (index 0)
		String[] fenRows = piecePlacement.split("/");
		if (fenRows.length != 8) {
			throw new InvalidFENexception("The FEN board must have exactly 8 ranks.");
		}

		for (int i = 0; i < 8; i++) {
			byte row = (byte) (7 - i); // FEN row 0 = rank 7 (index) in our game
			String rowString = fenRows[i];
			byte col = 0;

			for (char c : rowString.toCharArray()) {
				if (Character.isDigit(c)) {
					col += Character.getNumericValue(c); // Skips empty squares
				} else {
					if (col >= 8) throw new InvalidFENexception("Rank " + (row + 1) + " exceeds 8 files.");
					
					boolean isWhite = Character.isUpperCase(c);
					char pieceType = Character.toUpperCase(c);
					Piece p = null;

					switch (pieceType) {
						case 'P' -> {
							p = new Pawn(row, col, isWhite);
							if (row != (isWhite ? 1 : 6)) {
								p.setMoved(true);
							}
							(isWhite ? whitePawns : blackPawns).add((Pawn) p);
						}
						case 'R' -> {
							p = new Rook(row, col, isWhite);
							(isWhite ? whiteRooks : blackRooks).add((Rook) p);
						}
						case 'N' -> {
							p = new Knight(row, col, isWhite);
							(isWhite ? whiteKnights : blackKnights).add((Knight) p);
						}
						case 'B' -> {
							p = new Bishop(row, col, isWhite);
							(isWhite ? whiteBishops : blackBishops).add((Bishop) p);
						}
						case 'Q' -> {
							p = new Queen(row, col, isWhite);
							(isWhite ? whiteQueens : blackQueens).add((Queen) p);
						}
						case 'K' -> {
							p = new King(row, col, isWhite);
							if (isWhite) whiteKing = (King) p;
							else blackKing = (King) p;
						}
						default -> throw new InvalidFENexception("Unknown piece in FEN: " + c);
					}
					
					matrix[row][col] = p;
					col++;
				}
			}
			if (col != 8) throw new InvalidFENexception("Rank " + (row + 1) + " does not have exactly 8 files.");
		}

		if (whiteKing == null || blackKing == null) {
			throw new InvalidFENexception("Invalid FEN: Missing kings on the board.");
		}

		// ==========================================
		// 2. ACTIVE COLOR
		// ==========================================
		boolean whiteToMove;
		if (activeColor.equals("w")) whiteToMove = true;
		else if (activeColor.equals("b")) whiteToMove = false;
		else throw new InvalidFENexception("Active color must be 'w' or 'b'.");

		// ==========================================
		// 3. CASTLING RIGHTS (Manipulating hasMoved)
		// ==========================================
		// If the King has no castling rights at all, it has already moved.
		whiteKing.setMoved(!castlingRights.contains("K") && !castlingRights.contains("Q"));
		blackKing.setMoved(!castlingRights.contains("k") && !castlingRights.contains("q"));

		// If the king hasn't moved, disable only the specific rooks that lost the right
		if (!whiteKing.hasMoved()) {
			if (!castlingRights.contains("K") && matrix[0][7] instanceof Rook r) r.setMoved(true);
			if (!castlingRights.contains("Q") && matrix[0][0] instanceof Rook r) r.setMoved(true);
		}
		if (!blackKing.hasMoved()) {
			if (!castlingRights.contains("k") && matrix[7][7] instanceof Rook r) r.setMoved(true);
			if (!castlingRights.contains("q") && matrix[7][0] instanceof Rook r) r.setMoved(true);
		}

		// ==========================================
		// 4. EN PASSANT (Creating a Dummy Move)
		// ==========================================
		ArrayList<Move> moves = new ArrayList<>();
		if (!enPassantTarget.equals("-")) {
			try {
				byte epCol = (byte) (enPassantTarget.charAt(0) - 'a');
				byte epRow = (byte) (enPassantTarget.charAt(1) - '1'); // e.g.: '3' becomes index 2

				// If the target is row 2 (e3), the pawn is at row 3 (e4). If target is row 5 (e6), the pawn is at 4 (e5).
				byte pawnRow = (byte) (epRow == 2 ? 3 : 4); 
				Piece epPawn = matrix[pawnRow][epCol];

				if (epPawn instanceof Pawn) {
					// Origin position of the dummy pawn (row 1 for white, row 6 for black)
					byte oldRow = (byte) (epPawn.isWhite() ? 1 : 6);
					Pawn dummyOldPawn = new Pawn(oldRow, epCol, epPawn.isWhite());
					
					// Adds the dummy move so the engine enables diagonal capture
					Move dummyMove = new Move(true, dummyOldPawn.getPieceInfo(), epPawn);
					moves.add(dummyMove);
				}
			} catch (Exception e) {
				throw new InvalidFENexception("Error reading En Passant target square: " + enPassantTarget);
			}
		}
		
		//gerenrate initial position history
		String initialCut = parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3];
		HashMap<String, Integer> positionHistory = new HashMap<String, Integer>();
		positionHistory.put(initialCut, 1);
		
		// All set! Initializes the board.
		return new ChessBoard(
			matrix, whiteRooks, blackRooks, whitePawns, blackPawns, 
			whiteKnights, blackKnights, whiteBishops, blackBishops, 
			whiteQueens, blackQueens, whiteKing, blackKing, 
			moves, whiteToMove, null, 
			halfmoveClock, fullmoveNumber, new ArrayList<Integer>(),
			positionHistory
			
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
