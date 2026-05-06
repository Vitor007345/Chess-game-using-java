package chessgame;

import chessgame.moves.MoveNotationError;
import chessgame.moves.Move;
import chessgame.pieces.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ChessBoard {
	//atributes
	private Piece[][] board; //[row][col]
	
	private ArrayList<Rook> whiteRooks = new ArrayList<>();
    private ArrayList<Rook> blackRooks = new ArrayList<>();

    private ArrayList<Pawn> whitePawns = new ArrayList<>();
    private ArrayList<Pawn> blackPawns = new ArrayList<>();

    private ArrayList<Knight> whiteKnights = new ArrayList<>();
    private ArrayList<Knight> blackKnights = new ArrayList<>();

    private ArrayList<Bishop> whiteBishops = new ArrayList<>();
    private ArrayList<Bishop> blackBishops = new ArrayList<>();

    private ArrayList<Queen> whiteQueens = new ArrayList<>();
    private ArrayList<Queen> blackQueens = new ArrayList<>();

    private King whiteKing;
    private King blackKing;
    
    private ArrayList<Move> moves; 
    
    //constructors
    
    //package private
	ChessBoard(Piece[][] board, ArrayList<Rook> whiteRooks, ArrayList<Rook> blackRooks,
			ArrayList<Pawn> whitePawns, ArrayList<Pawn> blackPawns, ArrayList<Knight> whiteKnights,
			ArrayList<Knight> blackKnights, ArrayList<Bishop> whiteBishops, ArrayList<Bishop> blackBishops,
			ArrayList<Queen> whiteQueens, ArrayList<Queen> blackQueens, King whiteKing, King blackKing,
			ArrayList<Move> moves) {
		this.board = board;
		this.whiteRooks = whiteRooks;
		this.blackRooks = blackRooks;
		this.whitePawns = whitePawns;
		this.blackPawns = blackPawns;
		this.whiteKnights = whiteKnights;
		this.blackKnights = blackKnights;
		this.whiteBishops = whiteBishops;
		this.blackBishops = blackBishops;
		this.whiteQueens = whiteQueens;
		this.blackQueens = blackQueens;
		this.whiteKing = whiteKing;
		this.blackKing = blackKing;
		this.moves = moves;
	}
	
	
	
	public void move(String moveStr) {
		
		if(moveStr.length() < 2) throw new MoveNotationError(moveStr, "move string is too short");
		
		int lastIndex = moveStr.length() - 1;
		char[] moveStrchar = moveStr.toCharArray();
		char lastChar = moveStrchar[lastIndex];
		boolean checkOrCheckmate = false;
		if(lastChar == '+' || lastChar == '#') {
			checkOrCheckmate = true;
			lastIndex --;
		}
		
		if(Arrays.equals(moveStrchar, 0, lastIndex + 1, "O-O-O".toCharArray(), 0, 5)) {
			//long castle logic
		}else if(Arrays.equals(moveStrchar, 0, lastIndex + 1, "O-O".toCharArray(), 0, 3)) {
			//short castle logic
		}else {
			if(lastIndex < 1) throw new MoveNotationError(moveStr, "move string is too short");
			char possiblePromotionPiece = moveStrchar[lastIndex];
			boolean promotion = false;
			if(possiblePromotionPiece == 'Q' || possiblePromotionPiece == 'R' || possiblePromotionPiece == 'B' || possiblePromotionPiece == 'N') {
				lastIndex--;
				promotion = true;
				if(moveStrchar[lastIndex] == '=') {
					lastIndex--;;
				}
			}
			
			if(lastIndex < 1) throw new MoveNotationError(moveStr, "move string is too short");
			byte rowTo = convertRow(moveStrchar[lastIndex], moveStr);
			byte colTo = convertCol(moveStrchar[lastIndex - 1], moveStr);
			lastIndex -= 2;
			
			if(lastIndex < 0) {
				//pawn going forward logic
			}else {
				boolean capture = false;
				if(moveStrchar[lastIndex] == 'x') {
					lastIndex --;
					capture = true;
				}
				if(lastIndex == 0) {
					char charRemaining = moveStrchar[lastIndex];
					if(capture && (charRemaining >= 'a' && charRemaining <= 'h')) {
						//pawn capture logic
					}else {
						char pieceChar = charRemaining;
						//any pieces that is not a pawn logic when there is no conflict moves
						//do switch case with pieceChar to each piece
					}
					
				}else if(lastIndex <= 2) {
					char pieceChar = moveStrchar[0];
					if(lastIndex == 2) {
						byte rowFrom = convertRow(moveStrchar[2], moveStr);
						byte colFrom = convertCol(moveStrchar[1], moveStr);
						//any pieces that is not a pawn logic with exact from pos
						//do switch case with pieceChar to each piece
					}else {
						char colOrRowChar = moveStrchar[1];
						if(colOrRowChar >= 'a' && colOrRowChar <= 'h') {
							byte colFrom = convertCol(colOrRowChar, moveStr);
							//any pieces that is not a pawn logic with exact from col
							//do switch case with pieceChar to each piece
							
						}else if(colOrRowChar >= '1' && colOrRowChar <= '8') {
							byte rowFrom = convertRow(colOrRowChar, moveStr);
							
							//any pieces that is not a pawn logic with exact from row
							//do switch case with pieceChar to each piece
						}else {
							throw new MoveNotationError(moveStr, colOrRowChar + "is not a file or a rank in chess");
						}
					}
				}else {
					throw new MoveNotationError(moveStr, "move string is too long");
				}
			}		
		}
	}
	
	//position converters
	private static byte convertCol(char colChar, String moveStr) {
		int col = (int)(colChar - 'a');
		if(col < 0 || col > 7)throw new MoveNotationError(moveStr, "invalid col");
		return (byte)col;
	}
		
	private static byte convertRow(char rowChar, String moveStr) {
		int row = (int)(rowChar - '1');
		if(row < 0 || row > 7)throw new MoveNotationError(moveStr, "invalid row");
		return (byte)row;
	}
    
	
    
    
    
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 7; i >= 0; i--) {
			for(int j = 0; j <= 7; j++) {
				Piece piece = this.board[i][j];
				
				sb.append((piece == null)?"·":piece.getIcon()).append(" ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
		
	}
	
	
	
	
	
}
