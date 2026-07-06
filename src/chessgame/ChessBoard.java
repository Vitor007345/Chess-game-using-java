package chessgame;

import chessgame.errors.MoveNotationException;
import chessgame.moves.Move;
import chessgame.pieces.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a full chess game state and board.
 * <p>
 * This class is responsible for holding the current position of all pieces,
 * the move history, the game result, and all the rules needed to validate
 * and execute moves, either from algebraic notation (SAN-like strings) or
 * from raw board coordinates (e.g. mouse clicks in a GUI).
 * <p>
 * It also keeps track of auxiliary game state required by the official
 * chess rules, such as the halfmove clock (for the fifty-move rule),
 * the fullmove number, and a position history (for the threefold
 * repetition rule).
 */
public class ChessBoard {
	//atributes

	/** The 8x8 board matrix, indexed as [row][col], holding a {@link Piece} or {@code null} for empty squares. */
	private Piece[][] board; //[row][col]

	/** List of the white rooks currently on the board. */
	private ArrayList<Rook> whiteRooks;
	/** List of the black rooks currently on the board. */
    private ArrayList<Rook> blackRooks;

	/** List of the white knights currently on the board. */
    private ArrayList<Knight> whiteKnights;
	/** List of the black knights currently on the board. */
    private ArrayList<Knight> blackKnights;

	/** List of the white bishops currently on the board. */
    private ArrayList<Bishop> whiteBishops;
	/** List of the black bishops currently on the board. */
    private ArrayList<Bishop> blackBishops;

	/** List of the white queens currently on the board. */
    private ArrayList<Queen> whiteQueens;
	/** List of the black queens currently on the board. */
    private ArrayList<Queen> blackQueens;

	/** List of the white pawns currently on the board. */
    private ArrayList<Pawn> whitePawns;
	/** List of the black pawns currently on the board. */
    private ArrayList<Pawn> blackPawns;

	/** The white king. */
    private King whiteKing;
	/** The black king. */
    private King blackKing;

	/** Ordered history of moves already played in this game. */
    private ArrayList<Move> moves;

	/** {@code true} if it is white's turn to move, {@code false} if it is black's turn. */
    private boolean whiteToMove;

	/**
	 * The result of the game in standard notation ("1-0", "0-1", "1/2-1/2"),
	 * or {@code null} if the game is still in progress.
	 */
    private String result;

	/** Number of halfmoves (plies) since the last pawn move or capture, used for the fifty-move rule. */
    private int halfmoveClock;
	/** The number of the current full move, incremented after black moves. */
    private int fullmoveNumber;
	/** Stack of previous halfmove clock values, used to restore the clock correctly when undoing moves. */
    private ArrayList<Integer> halfmoveResetHistory;

	/** Map counting how many times each reduced FEN position (without clocks) has occurred, used for threefold repetition. */
    private HashMap<String, Integer> positionHistory;

	/** The FEN string that represents the initial position of this game. */
    private String initialFEN;

    //constructors

    /**
     * Package-private constructor used to build a {@code ChessBoard} with all of its internal state
     * already prepared (typically by a factory/parser that builds the board from a FEN string or
     * from the standard starting position).
     *
     * @param board the initial board matrix [row][col]
     * @param whiteRooks list of white rooks
     * @param blackRooks list of black rooks
     * @param whitePawns list of white pawns
     * @param blackPawns list of black pawns
     * @param whiteKnights list of white knights
     * @param blackKnights list of black knights
     * @param whiteBishops list of white bishops
     * @param blackBishops list of black bishops
     * @param whiteQueens list of white queens
     * @param blackQueens list of black queens
     * @param whiteKing the white king
     * @param blackKing the black king
     * @param moves the move history so far
     * @param whiteToMove {@code true} if it is white's turn to move
     * @param result the current result of the game, or {@code null} if still ongoing
     * @param halfmoveClock the current halfmove clock value
     * @param fullmoveNumber the current fullmove number
     * @param halfmoveResetHistory the history of halfmove clock values before each reset
     * @param positionHistory the map of position occurrences used for repetition detection
     * @param initialFEN the FEN string representing the starting position of this game
     */
	ChessBoard(Piece[][] board, ArrayList<Rook> whiteRooks, ArrayList<Rook> blackRooks,
			ArrayList<Pawn> whitePawns, ArrayList<Pawn> blackPawns, ArrayList<Knight> whiteKnights,
			ArrayList<Knight> blackKnights, ArrayList<Bishop> whiteBishops, ArrayList<Bishop> blackBishops,
			ArrayList<Queen> whiteQueens, ArrayList<Queen> blackQueens, King whiteKing, King blackKing,
			ArrayList<Move> moves, boolean whiteToMove, String result,
			int halfmoveClock, int fullmoveNumber, ArrayList<Integer> halfmoveResetHistory, HashMap<String, Integer> positionHistory,
			String initialFEN) {
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
		this.whiteToMove = whiteToMove;
		this.result = result;
		this.halfmoveClock = halfmoveClock;
		this.fullmoveNumber = fullmoveNumber;
		this.halfmoveResetHistory = halfmoveResetHistory;
		this.positionHistory = positionHistory;
		this.initialFEN = initialFEN;
	}


	/**
	 * Returns the current result of the game.
	 *
	 * @return "1-0" if white won, "0-1" if black won, "1/2-1/2" if drawn,
	 *         or {@code null} if the game is still in progress
	 */
	public String getResult() {
		return this.result;
	}
	/**
	 * Returns whose turn it is to move.
	 *
	 * @return {@code true} if it's white's turn, {@code false} if it's black's turn
	 */
	public boolean isWhiteToMove() {
		return this.whiteToMove;
	}
	/**
	 * Gets the piece located at the given board coordinates.
	 *
	 * @param row the row index (0-7)
	 * @param col the column index (0-7)
	 * @return the {@link Piece} at that square, or {@code null} if the square is empty
	 */
	public Piece getPiece(int row, int col) {
		return this.board[row][col];
	}
	/**
	 * Returns the current halfmove clock, i.e. the number of halfmoves (plies)
	 * since the last pawn move or capture. Used for the fifty-move draw rule.
	 *
	 * @return the halfmove clock value
	 */
	public int getHalfmoveClock() {
		return this.halfmoveClock;
	}
	/**
	 * Returns the current fullmove number of the game.
	 *
	 * @return the fullmove number
	 */
	public int getFullmoveNumber() {
		return this.fullmoveNumber;
	}
	/**
	 * Returns the FEN string representing the initial position of this game.
	 *
	 * @return the initial FEN string
	 */
	public String getInitialFEN() {
        return this.initialFEN;
    }


	/**
	 * Parses and executes a move given in algebraic-like notation (e.g. "e4", "Nf3", "exd5",
	 * "O-O", "O-O-O", "e8=Q", "Qh5+", "Ra8#").
	 * <p>
	 * This method parses the move string from the end backwards, identifying check/checkmate
	 * suffixes, promotion pieces, destination square, capture indicator, and disambiguation
	 * information (origin file, rank, or full square), then dispatches to the appropriate
	 * piece-specific move logic. After the move is executed, it updates the halfmove clock,
	 * fullmove number, move history, position repetition tracking, and game result.
	 *
	 * @param moveStr the move in algebraic notation
	 * @throws MoveNotationException if the move string is malformed or the move is illegal
	 */
	public void move(String moveStr) throws MoveNotationException{
		Move move = null;
		
		if(moveStr.length() < 2) throw new MoveNotationException(moveStr, "move string is too short");
		
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
			move = this.castle(false, moveStr);
		}else if(Arrays.equals(moveStrchar, 0, lastIndex + 1, "O-O".toCharArray(), 0, 3)) {
			//short castle logic
			move = this.castle(true, moveStr);
		}else {
			if(lastIndex < 1) throw new MoveNotationException(moveStr, "move string is too short");
			char possiblePromotionPiece = moveStrchar[lastIndex];
			boolean promotion = false;
			if(possiblePromotionPiece == 'Q' || possiblePromotionPiece == 'R' || possiblePromotionPiece == 'B' || possiblePromotionPiece == 'N') {
				lastIndex--;
				promotion = true;
				if(moveStrchar[lastIndex] == '=') {
					lastIndex--;;
				}
			}
			
			if(lastIndex < 1) throw new MoveNotationException(moveStr, "move string is too short");
			byte rowTo = convertRow(moveStrchar[lastIndex], moveStr);
			byte colTo = convertCol(moveStrchar[lastIndex - 1], moveStr);
			lastIndex -= 2;
			
			if(lastIndex < 0) {
				//pawn going forward logic
				move = this.pawnGoingForwardMove(rowTo, colTo, promotion, possiblePromotionPiece, moveStr);
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
						move = this.pawnCaptureMove(rowTo, colTo, convertCol(charRemaining, moveStr), promotion, possiblePromotionPiece, moveStr);
					}else {
						char pieceChar = charRemaining;
						//any pieces that is not a pawn logic when there is no conflict moves
						//do switch case with pieceChar to each piece
						switch(pieceChar) {
							case 'N':
								move = this.knightMove(rowTo, colTo, capture, moveStr);
								break;
							case 'B':
								move = this.bishopMove(rowTo, colTo, capture, moveStr);
								break;
							case 'R':
								move = this.rookMove(rowTo, colTo, capture, moveStr);
								break;
							case 'Q':
								move = this.queenMove(rowTo, colTo, capture, moveStr);
								break;
							case 'K':
								move = this.kingMove(rowTo, colTo, capture, moveStr);
								break;
							default:
								throw new MoveNotationException(moveStr, "there io no piece with this letter");
						}
					}
					
				}else if(lastIndex <= 2) {
					char pieceChar = moveStrchar[0];
					if(lastIndex == 2) {
						byte rowFrom = convertRow(moveStrchar[2], moveStr);
						byte colFrom = convertCol(moveStrchar[1], moveStr);
						//any pieces that is not a pawn logic with exact from pos
						//do switch case with pieceChar to each piece
						switch(pieceChar) {
							case 'N':
								move = this.knightMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom);
								break;
							case 'B':
								move = this.bishopMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom);
								break;
							case 'R':
								move = this.rookMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom);
								break;
							case 'Q':
								move = this.queenMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom);
								break;
							case 'K':
								move = this.kingMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom);
								break;
							default:
								throw new MoveNotationException(moveStr, "there io no piece with this letter");
						}
					}else {
						char colOrRowChar = moveStrchar[1];
						if(colOrRowChar >= 'a' && colOrRowChar <= 'h') {
							byte colFrom = convertCol(colOrRowChar, moveStr);
							//any pieces that is not a pawn logic with exact from col
							//do switch case with pieceChar to each piece
							switch(pieceChar) {
								case 'N':
									move = this.knightMove(rowTo, colTo, capture, moveStr, colFrom, false);
									break;
								case 'B':
									move = this.bishopMove(rowTo, colTo, capture, moveStr, colFrom, false);
									break;
								case 'R':
									move = this.rookMove(rowTo, colTo, capture, moveStr, colFrom, false);
									break;
								case 'Q':
									move = this.queenMove(rowTo, colTo, capture, moveStr, colFrom, false);
									break;
								case 'K':
									move = this.kingMove(rowTo, colTo, capture, moveStr, colFrom, false);
									break;
								default:
									throw new MoveNotationException(moveStr, "there io no piece with this letter");
							}
							
						}else if(colOrRowChar >= '1' && colOrRowChar <= '8') {
							byte rowFrom = convertRow(colOrRowChar, moveStr);
							//any pieces that is not a pawn logic with exact from row
							//do switch case with pieceChar to each piece
							switch(pieceChar) {
							case 'N':
								move = this.knightMove(rowTo, colTo, capture, moveStr, rowFrom, true);
								break;
							case 'B':
								move = this.bishopMove(rowTo, colTo, capture, moveStr, rowFrom, true);
								break;
							case 'R':
								move = this.rookMove(rowTo, colTo, capture, moveStr, rowFrom, true);
								break;
							case 'Q':
								move = this.queenMove(rowTo, colTo, capture, moveStr, rowFrom, true);
								break;
							case 'K':
								move = this.kingMove(rowTo, colTo, capture, moveStr, rowFrom, true);
								break;
							default:
								throw new MoveNotationException(moveStr, "there io no piece with this letter");
						}
						}else {
							throw new MoveNotationException(moveStr, colOrRowChar + "is not a file or a rank in chess");
						}
					}
				}else {
					throw new MoveNotationException(moveStr, "move string is too long");
				}
			}		
		}
		
		
		
		if(move != null) {
			this.resolveStateOfGameAfterMove(move, checkOrCheckmate, moveStr);
			
			if (move.getMovedPiece() instanceof Pawn || move.getCapturedPiece() != null) {
                this.halfmoveResetHistory.add(this.halfmoveClock);
                this.halfmoveClock = 0;
            } else {
                this.halfmoveClock++;
            }
            if (!this.whiteToMove) {
                this.fullmoveNumber++;
            }

            
            if (this.result == null && this.halfmoveClock >= 100) {
                this.result = "1/2-1/2"; //draw 50 moves rule
            }
			this.moves.add(move);
			this.whiteToMove = !this.whiteToMove;
			
			String cutFen = this.getCutFEN();
            this.positionHistory.put(cutFen, this.positionHistory.getOrDefault(cutFen, 0) + 1);
            
            if (this.result == null && this.positionHistory.get(cutFen) >= 3) {
                this.result = "1/2-1/2"; //draw for triple repetition
            }
            
            move.setAlgebricNotation(moveStr);
		}
		
	}
	
	/**
	 * Parses and executes a move given as raw board coordinates, typically originating
	 * from a GUI mouse click or drag-and-drop action.
	 * <p>
	 * Validates coordinate bounds, ownership of the moving piece, and whether the
	 * destination square can legally be reached according to each piece's movement
	 * geometry (including castling for the king). After execution, verifies the move
	 * does not leave the mover's own king in check, updates halfmove/fullmove counters,
	 * move history, repetition tracking, and computes the resulting algebraic notation.
	 *
	 * @param rFrom the origin row (0-7)
	 * @param cFrom the origin column (0-7)
	 * @param rTo the destination row (0-7)
	 * @param cTo the destination column (0-7)
	 * @param promoPiece the piece letter to promote to if the move is a pawn promotion (e.g. 'Q', 'R', 'B', 'N')
	 * @throws MoveNotationException if the coordinates are out of bounds, there is no piece
	 *         to move, the piece does not belong to the player to move, the move is not
	 *         geometrically legal, or it would leave the mover's king in check
	 */
	public void move(int rFrom, int cFrom, int rTo, int cTo, char promoPiece) throws MoveNotationException{
	    byte rowFrom = (byte) rFrom;
	    byte colFrom = (byte) cFrom;
	    byte rowTo = (byte) rTo;
	    byte colTo = (byte) cTo;
	    
	    String errTag = "Mouse click";

	    //validate limits
	    if (rowFrom < 0 || rowFrom > 7 || colFrom < 0 || colFrom > 7 || rowTo < 0 || rowTo > 7 || colTo < 0 || colTo > 7) {
	        throw new MoveNotationException(errTag, "Coordinates out of bounds");
	    }
	    
	    Piece p = this.board[rowFrom][colFrom];
	    if (p == null) {
	        throw new MoveNotationException(errTag, "Theres is no piece in the origin position");
	    }
	    if (p.isWhite() != this.whiteToMove) {
	        throw new MoveNotationException(errTag, "You can't move enemy pieces");
	    }
	    
	    Piece captured = this.board[rowTo][colTo];
	    if (captured != null && captured.isWhite() == this.whiteToMove) {
	        throw new MoveNotationException(errTag, "You can't capture your own piece");
	    }
	    
	    Move move = null;
	    
	    //geometry validation
	    switch (p) {
	        case Knight n -> {
	            if(!knightCanMove(rowTo, colTo, rowFrom, colFrom)) throw new MoveNotationException(errTag, "Knights can move only in L");
	        }
	        case Bishop b -> {
	            if(!bishopCanMove(rowTo, colTo, rowFrom, colFrom)) throw new MoveNotationException(errTag, "This is not a diagonal or your bishop is blocked");
	        }
	        case Rook r -> {
	            if(!rookCanMove(rowTo, colTo, rowFrom, colFrom)) throw new MoveNotationException(errTag, "This is not an straight line or your rook is blocked");
	        }
	        case Queen q -> {
	            if(!queenCanMove(rowTo, colTo, rowFrom, colFrom)) throw new MoveNotationException(errTag, "Your queen is blocked or this is not a diagonal, neither an straight line");
	        }
	        case King k -> {
	            if (this.kingCanMove(rowTo, colTo, rowFrom, colFrom)) {
	                //normal king Move
	            } else if (rowFrom == (this.whiteToMove ? 0 : 7) && rowTo == rowFrom && Math.abs(colTo - colFrom) == 2) {
	                //try to castle
	                boolean isShort = colTo > colFrom;
	                move = this.castle(isShort, errTag); 
	            } else {
	                throw new MoveNotationException(errTag, "King does not move that way");
	            }
	        }
	        case Pawn pawn -> {
	            move = this.executePawnMoveFromCoordinates(pawn, rowFrom, colFrom, rowTo, colTo, captured, promoPiece, errTag);
	        }
	        default -> throw new MoveNotationException(errTag, "Error unknown piece");
	    }
	    
	    
	    if (move == null) {
	        move = this.executeMove(p, rowTo, colTo, captured);
	    }
	    
	    //check king safety
	    if (this.isKingInCheck(this.whiteToMove)) {
	        this.undoMove(move); //revert movement if the king was in danger
	        throw new MoveNotationException(errTag, "You can't blunder your king in chess");
	    }
	    
	    if (p instanceof Pawn || captured != null) {
	    	this.halfmoveResetHistory.add(this.halfmoveClock);
	        this.halfmoveClock = 0;
	    }else {
	    	this.halfmoveClock++;
	    }
	    
	    if (!this.whiteToMove) {
	        this.fullmoveNumber++;
	    }
	    
	    this.resolveStateOfGameAfterMove();
	    
	    this.moves.add(move);
	    this.whiteToMove = !this.whiteToMove;
	    
	    String cutFen = this.getCutFEN();
        this.positionHistory.put(cutFen, this.positionHistory.getOrDefault(cutFen, 0) + 1);
        
        if (this.result == null && this.positionHistory.get(cutFen) >= 3) {
            this.result = "1/2-1/2"; //draw for triple repetition
        }
        
        move.setAlgebricNotation(this.getAlgebraicNotation(move, rowFrom, colFrom, rowTo, colTo, promoPiece));
	    
	}
	
	/**
	 * Undoes the last move played, restoring the board, piece lists, halfmove clock,
	 * fullmove number, turn, position history, and game result to their previous state.
	 *
	 * @return {@code true} if a move was successfully undone, {@code false} if there was
	 *         no move to undo (the move list is empty or the last entry is a dummy move)
	 */
	public boolean undoMove() {
		if(this.moves.isEmpty() || this.moves.getLast().isDummy()) {
			return false;
		}else {
			
			String cutFen = this.getCutFEN();
			int count = this.positionHistory.getOrDefault(cutFen, 0);
			if (count <= 1) {
				this.positionHistory.remove(cutFen);
			} else {
				this.positionHistory.put(cutFen, count - 1);
			}
			
			Move lastMove = this.moves.getLast();
			
			if (this.whiteToMove) { 
                this.fullmoveNumber--;
            }
			
			boolean wasPawnMove = lastMove.getMovedPiece() instanceof Pawn || lastMove.isPromotion();
            boolean wasCapture = lastMove.getCapturedPiece() != null;
            
            //restore the clock even if it was reseted on the lastMove
            if (wasPawnMove || wasCapture) {
                this.halfmoveClock = this.halfmoveResetHistory.removeLast();
            } else {
                this.halfmoveClock--;
            }
			
			this.undoMove(lastMove);
			this.moves.removeLast();
			this.whiteToMove = !this.whiteToMove;
			this.result = null;
			return true;
		}
		
	
	}
	
	/**
	 * Reverts the effects of a single executed move on the board and piece lists,
	 * including restoring castled rooks, undoing pawn promotions, and restoring
	 * captured pieces to the board and to their respective piece lists.
	 *
	 * @param move the move to undo
	 * @throws IllegalArgumentException if the move has already been deleted/undone
	 */
	private void undoMove(Move move) {
		if(move.isDeleted())throw new IllegalArgumentException("This move is aready deleted");
		
		if(move.isCastle()) {
			byte rookRow = move.getMovedPiece().getRow();
			byte rookCol = (byte)(move.isShortCastle()?5:3);
			Rook rook = (Rook)this.board[rookRow][rookCol];
			this.board[rookRow][rookCol] = null;
			byte originRookCol = (byte)(move.isShortCastle()?7:0);
			rook.setCol(originRookCol);
			rook.setMoved(false);
			this.board[rookRow][originRookCol] = rook;
			
		}
		
		
		
		if(move.isPromotion()) {
	        
	        Piece promotedPiece = move.getMovedPiece();
	        this.board[promotedPiece.getRow()][promotedPiece.getCol()] = null;
	        
	        if(!this.removePieceFromArrays(promotedPiece)) {
	            throw new AssertionError("Error when removing promoted piece when undoing promotion");
	        }
	        
	        Pawn pawn = move.getOriginalPawn();
	        pawn.setInfo(move.getMovedPieceOldInfo());
	        (pawn.isWhite() ? this.whitePawns : this.blackPawns).add(pawn);
	        this.board[pawn.getRow()][pawn.getCol()] = pawn;
	    } else {
	        this.board[move.getMovedPiece().getRow()][move.getMovedPiece().getCol()] = null;
	        move.getMovedPiece().setInfo(move.getMovedPieceOldInfo());
	        this.board[move.getMovedPiece().getRow()][move.getMovedPiece().getCol()] = move.getMovedPiece();
	    }
		
		if(move.getCapturedPiece() != null) {
			this.board[move.getCapturedPiece().getRow()][move.getCapturedPiece().getCol()] = move.getCapturedPiece();
			
			switch(move.getCapturedPiece()) {
				case Knight n -> {
					(n.isWhite()? this.whiteKnights : this.blackKnights).add(n);
					break;
				}
				case Bishop b -> {
					(b.isWhite()? this.whiteBishops : this.blackBishops).add(b);
					break;
				}
				case Rook r -> {
					(r.isWhite()? this.whiteRooks : this.blackRooks).add(r);
					break;
				}
				case Queen q -> {
					(q.isWhite()? this.whiteQueens : this.blackQueens).add(q);
					break;
				}
				case Pawn p -> {
					(p.isWhite()? this.whitePawns : this.blackPawns).add(p);
					break;
				}
				default -> {
					
				}
			}
			
		}
		move.delete();
	}
	
	//position converters
	/**
	 * Converts a file character ('a' to 'h') into a zero-based column index.
	 *
	 * @param colChar the file character
	 * @param moveStr the original move string, used for error reporting
	 * @return the column index (0-7)
	 * @throws MoveNotationException if the character does not represent a valid file
	 */
	private static byte convertCol(char colChar, String moveStr) throws MoveNotationException{
		int col = (int)(colChar - 'a');
		if(col < 0 || col > 7)throw new MoveNotationException(moveStr, "invalid col");
		return (byte)col;
	}
		
	/**
	 * Converts a rank character ('1' to '8') into a zero-based row index.
	 *
	 * @param rowChar the rank character
	 * @param moveStr the original move string, used for error reporting
	 * @return the row index (0-7)
	 * @throws MoveNotationException if the character does not represent a valid rank
	 */
	private static byte convertRow(char rowChar, String moveStr) throws MoveNotationException{
		int row = (int)(rowChar - '1');
		if(row < 0 || row > 7)throw new MoveNotationException(moveStr, "invalid row");
		return (byte)row;
	}
	
	//castle movement
	/**
	 * Validates and executes a castling move for the player to move.
	 * <p>
	 * Checks that the king is on its initial square and has not moved, that the
	 * corresponding rook is on its initial square and has not moved, that the
	 * squares between the king and rook are empty, and that the king is not
	 * currently in check.
	 *
	 * @param isShort {@code true} for kingside (short) castling, {@code false} for queenside (long) castling
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move} representing the castle
	 * @throws MoveNotationException if any castling precondition is violated
	 */
	private Move castle(boolean isShort, String moveStr) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing:this.blackKing;
		byte kingRow = (byte)(this.whiteToMove? 0:7);
		if(king.getRow() != kingRow || king.getCol() != 4) {
			throw new MoveNotationException(moveStr, "You can´t castle if your king is not on the initial square");
		}
		if(king.hasMoved()) {
			throw new MoveNotationException(moveStr, "You can´t castle if your king has moved");
		}
		
		byte rookRow = kingRow;
		byte rookCol = (byte)(isShort?7:0);
		Piece possibleRook = this.board[rookRow][rookCol];
		if(!(possibleRook instanceof Rook rook)) {
			throw new MoveNotationException(moveStr, "You can´t castle if your rook is not on the initial square");
		}
		if(rook.hasMoved()) {
			throw new MoveNotationException(moveStr, "You can´t castle if your rook has moved");
		}
		if(!this.isRowEmptyBetween2Pieces(king, rook)) {
			throw new MoveNotationException(moveStr, "You can´t castle if the path between your King and Rook is not free");
		}
		
		if(this.isKingInCheck()) {
			throw new MoveNotationException(moveStr, "You cannot castle while in check");
		}
		
		
		return this.executeCastleMove(king, kingRow, rook, isShort, moveStr);
	}
	
	/**
	 * Executes the actual castling move on the board, after all preconditions have
	 * already been validated by {@link #castle(boolean, String)}.
	 * <p>
	 * First simulates the king's intermediate square to make sure it is not attacked
	 * (a king cannot pass through check while castling), then moves both the king
	 * and the rook to their final squares, and finally verifies the king is not left
	 * in check.
	 *
	 * @param king the castling king
	 * @param kingAndRookRow the row where both the king and the rook are located (0 for white, 7 for black)
	 * @param rook the castling rook
	 * @param isShort {@code true} for kingside castling, {@code false} for queenside castling
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move} representing the castle
	 * @throws MoveNotationException if the king passes through or ends up in check
	 */
	private Move executeCastleMove(King king, byte kingAndRookRow, Rook rook, boolean isShort, String moveStr) throws MoveNotationException{
		int direction = isShort?1:-1;
		//test if the middle case of the king movement when castle is safe
		Move test = this.executeMove(king, king.getRow(), (byte)(king.getCol() + direction), null);
		if(this.isKingInCheck()) {
			this.undoMove(test);
			throw new MoveNotationException(moveStr, "The square that is in the middle of the path of the king is atacked so you can't castle this side");
		}
		this.undoMove(test);
		
		Move kingMove = this.executeMove(king, kingAndRookRow, (byte)(king.getCol() + (direction * 2)), null);
		Move rookMove = this.executeMove(rook, kingAndRookRow, (byte)(king.getCol() - direction), null);
		
		if(this.isKingInCheck()) {
			this.undoMove(rookMove);
			this.undoMove(kingMove);
			throw new MoveNotationException(moveStr, "Can't castle beacuse you will put your king in danger");
		}
		
		return new Move(true, isShort, kingMove.getMovedPieceOldInfo(), king);
		
		
	}
	
	
	//pawn movement logics
	/**
	 * Executes a pawn moving straight forward (including double-square first moves),
	 * optionally promoting the pawn upon reaching the last rank.
	 *
	 * @param pawn the pawn being moved
	 * @param fromR the origin row
	 * @param toR the destination row
	 * @param col the column (same for origin and destination, since this is a forward move)
	 * @param promotion {@code true} if this move results in a promotion
	 * @param promotionPiece the piece letter to promote to, if {@code promotion} is {@code true}
	 * @return the resulting {@link Move}
	 */
	private Move executePawnForwardMove(Pawn pawn, byte fromR, byte toR, int col, boolean promotion, char promotionPiece) {
		byte oldPawnInfo = pawn.getPieceInfo();
	    pawn.setRow(toR);
	    this.board[fromR][col] = null;
	    Piece movedPiece = promotion? this.promotePawn(pawn, promotionPiece) : pawn;
	    this.board[toR][col] = movedPiece;
	    pawn.setMoved(true);
	    
	    return promotion ? new Move(oldPawnInfo, movedPiece, promotion, pawn) : new Move(oldPawnInfo, movedPiece, promotion);
	    
	    
	}
	
	/**
	 * Promotes a pawn into a new piece of the given type, removing the pawn from its
	 * piece list and adding the newly created piece to the corresponding piece list.
	 *
	 * @param p the pawn being promoted
	 * @param pieceLetter the letter of the piece to promote to ('Q', 'R', 'B', or 'N')
	 * @return the newly created promoted piece
	 * @throws AssertionError if the pawn could not be removed from its piece list, or
	 *         if {@code pieceLetter} does not match any valid promotion piece
	 */
	private Piece promotePawn(Pawn p, char pieceLetter) {
		if(!((p.isWhite()?this.whitePawns : this.blackPawns).remove(p))) {
			throw new AssertionError("Error when deleting pawn when promoting");
		}
    	switch(pieceLetter) {
    		case 'Q':
    			Queen queen = new Queen(p.getPieceInfo());
    			return ((p.isWhite()? this.whiteQueens : this.blackQueens).add(queen))? queen : null;
    		case 'R':
    			Rook rook = new Rook(p.getPieceInfo());
    			return ((p.isWhite()? this.whiteRooks : this.blackRooks).add(rook))? rook : null;
    		case 'B':
    			Bishop bishop = new Bishop(p.getPieceInfo());
    			return ((p.isWhite()? this.whiteBishops : this.blackBishops).add(bishop))? bishop : null;
    		case 'N':
    			Knight knight = new Knight(p.getPieceInfo());
    			return ((p.isWhite()? this.whiteKnights : this.blackKnights).add(knight))? knight : null;
    		default:
    			throw new AssertionError("Error when promoting couldn't find piece to promote");
    	}
    }
	
	/**
	 * Reverts a pawn promotion, recreating the original {@link Pawn} from the promoted
	 * piece's info and removing the promoted piece from its piece list.
	 * <p>
	 * Note: this method currently appears unused in favor of the inline promotion-undo
	 * logic inside {@link #undoMove(Move)}.
	 *
	 * @param piece the promoted piece being reverted back into a pawn
	 * @return the recreated {@link Pawn}, or {@code null} if the operation failed
	 */
	private Pawn undoPromotion(Piece piece) {
		Pawn pawn = new Pawn(piece.getPieceInfo());
		if(!((pawn.isWhite()? this.whitePawns : this.blackPawns).add(pawn))) {
			return null;
		}
		return this.removePieceFromArrays(piece)?  pawn : null;
	}
	
	
	/**
	 * Handles a pawn move parsed from algebraic notation where no origin square/file/rank
	 * was specified and no capture was indicated (i.e. a simple forward pawn push, e.g. "e4").
	 * <p>
	 * Determines whether the pawn is moving one or two squares forward based on which
	 * square behind the destination actually contains an unmoved pawn, validates
	 * promotion rules, executes the move, and verifies the mover's king is not left in check.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param promotion {@code true} if the move string indicates a promotion
	 * @param promotionPiece the piece letter to promote to, if applicable
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if the destination is occupied, the promotion
	 *         requirement is violated, no valid pawn can make this move, or the move
	 *         leaves the mover's king in check
	 */
	private Move pawnGoingForwardMove(byte rowTo, byte colTo, boolean promotion, char promotionPiece, String moveStr) throws MoveNotationException{
		Move move = null;
		if(this.board[rowTo][colTo] != null) throw new MoveNotationException(moveStr, "position occupied");
		if((this.whiteToMove && rowTo <=1) || (!this.whiteToMove && rowTo >=6)) {
			throw new MoveNotationException(moveStr, "its impossible to a pawn move to this square");
		}
		if(promotion) {
			if((this.whiteToMove && rowTo != 7) || (!this.whiteToMove && rowTo != 0)) {
				throw new MoveNotationException(moveStr, "Can't promote without beeing in the top of the board");
			}
		}else if((this.whiteToMove && rowTo == 7) || (!this.whiteToMove && rowTo == 0)){
			throw new MoveNotationException(moveStr, "Can't move pawn there without promote");
		}
		
		byte possibleRowFrom = (byte)(rowTo + (this.whiteToMove?-1:1));
		Piece possiblePawn =  this.board[possibleRowFrom][colTo]; 
		if(possiblePawn instanceof Pawn pawn) {
			if(pawn.isWhite() != this.whiteToMove) throw new MoveNotationException(moveStr, "can't move a piece that's not yours");
			move = this.executePawnForwardMove(pawn, possibleRowFrom, rowTo, colTo, promotion, promotionPiece);
			
			
		}else if(possiblePawn == null){
			possibleRowFrom +=  (this.whiteToMove?-1:1);
			possiblePawn = this.board[possibleRowFrom][colTo];
			if(possiblePawn instanceof Pawn pawn) {
				if(!possiblePawn.hasMoved()) {
					if(pawn.isWhite() != this.whiteToMove) throw new MoveNotationException(moveStr, "can't move a piece that's not yours");
					
					move =  this.executePawnForwardMove(pawn, possibleRowFrom, rowTo, colTo, promotion, promotionPiece);
					
				}else {
					throw new MoveNotationException(moveStr, "its not the fist move of the pawn 2 square behind");
				}
			}else {
				throw new MoveNotationException(moveStr, "its impossible to a pawn move to this square");
			}
		}else {
			throw new MoveNotationException(moveStr, "there is a piece blocking");
		}
		
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	/**
	 * Executes a pawn capture move (diagonal move onto an occupied square, or an
	 * en passant capture), removing the captured piece from the board and its
	 * piece list, moving the pawn to the destination square, and applying
	 * promotion if applicable.
	 *
	 * @param pawn the capturing pawn
	 * @param pieceCaptured the piece being captured (may be on a different square than the destination, for en passant)
	 * @param toRow the destination row
	 * @param toCol the destination column
	 * @param promotion {@code true} if this capture results in a promotion
	 * @param promotionPiece the piece letter to promote to, if applicable
	 * @return the resulting {@link Move}
	 * @throws AssertionError if the captured piece could not be removed from its piece list
	 */
	private Move executePawnCaptureMove(Pawn pawn, Piece pieceCaptured, byte toRow, byte toCol, boolean promotion, char promotionPiece) {
		
		if(!this.removePieceFromArrays(pieceCaptured)) {
			throw new AssertionError("Error when removing captured piece");
		}
		
		byte oldPawnInfo = pawn.getPieceInfo();
	    
	    
	    this.board[pawn.getRow()][pawn.getCol()] = null;
	    this.board[pieceCaptured.getRow()][pieceCaptured.getCol()] = null;
	    
	    pawn.setPos(toRow, toCol);
	    
	    
	    
	    Piece movedPiece = promotion? this.promotePawn(pawn, promotionPiece) : pawn;
	    this.board[toRow][toCol] = movedPiece;
	    pawn.setMoved(true);
	    
	    return promotion ? new Move(oldPawnInfo, movedPiece, pieceCaptured, promotion, pawn) : new Move(oldPawnInfo, movedPiece, pieceCaptured, promotion);
	}
	
	/**
	 * Handles a pawn capture move parsed from algebraic notation (e.g. "exd5"), including
	 * regular diagonal captures and en passant captures.
	 * <p>
	 * Validates promotion rules, locates the capturing pawn based on the origin file,
	 * determines the captured piece (checking for en passant if the destination square
	 * is empty), executes the capture, and verifies the mover's king is not left in check.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param colFrom the origin file (column) of the capturing pawn
	 * @param promotion {@code true} if the move string indicates a promotion
	 * @param promotionPiece the piece letter to promote to, if applicable
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if the promotion requirement is violated, the
	 *         capture geometry is invalid, there is no valid pawn or piece to capture,
	 *         en passant preconditions are not met, or the move leaves the mover's king in check
	 */
	private Move pawnCaptureMove(byte rowTo, byte colTo, byte colFrom, boolean promotion, char promotionPiece, String moveStr) throws MoveNotationException{
		
		
		if((this.whiteToMove && rowTo <=1) || (!this.whiteToMove && rowTo >=6)) {
			throw new MoveNotationException(moveStr, "its impossible to a pawn move to this square");
		}
		if(promotion) {
			if((this.whiteToMove && rowTo != 7) || (!this.whiteToMove && rowTo != 0)) {
				throw new MoveNotationException(moveStr, "Can't promote without beeing in the top of the board");
			}
		}else if((this.whiteToMove && rowTo == 7) || (!this.whiteToMove && rowTo == 0)){
			throw new MoveNotationException(moveStr, "Can't move pawn there without promote");
		}
		
		int direction = colTo - colFrom;
		if(Math.abs(direction) != 1)throw new MoveNotationException(moveStr, "can´t capture in a file that is not at next to the file the pawn is");
		
		int num = (this.whiteToMove?-1:1);
		
		Piece pawnToMove = this.board[rowTo + num][colFrom];
		if(!(pawnToMove instanceof Pawn p) || (p.isWhite() != this.whiteToMove)) {
			throw new MoveNotationException(moveStr, "There is no pawn in this file for you to move to that square");
		}
		
		Piece pieceCaptured = this.board[rowTo][colTo];
		
		if(pieceCaptured == null) {
			pieceCaptured = this.board[rowTo + num][colTo];
			if(pieceCaptured == null) throw new MoveNotationException(moveStr, "there are no pieces to capture cant move pawn diagonaly without capture");
			if(!(pieceCaptured instanceof Pawn)) throw new MoveNotationException(moveStr, "can't do en passant in a piece that is not a pawn");
			if(this.moves.isEmpty()) throw new MoveNotationException(moveStr, "can't do an en passant on the first move of the game");
			Move lastMove = this.moves.getLast();
			if(lastMove.getMovedPiece() != pieceCaptured || !lastMove.isPawnDoubleFowardMove()) {
				throw new MoveNotationException(moveStr, "can't do an en passant when the last move was not a pawn moving 2 squares foward");
			}
			
		}
		if(pieceCaptured.isWhite() == this.whiteToMove) throw new MoveNotationException(moveStr, "You cant capture your piece");
		
		
		
		Move move = this.executePawnCaptureMove((Pawn)pawnToMove, pieceCaptured, rowTo, colTo, promotion, promotionPiece);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		
		return move;
	}
	
	//validate pawn movement using when the move is from GUI
	/**
	 * Validates and executes a pawn move given as raw board coordinates (e.g. from a
	 * GUI mouse click), covering single/double forward pushes, diagonal captures, and
	 * en passant captures.
	 *
	 * @param pawn the pawn being moved
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param captured the piece currently occupying the destination square, or {@code null} if empty
	 * @param promoPiece the piece letter to promote to, if this move reaches the last rank
	 * @param errTag a tag/context string used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if the pawn is blocked, the destination is invalid for
	 *         the given move type, or there is no valid capture/en-passant target
	 */
	private Move executePawnMoveFromCoordinates(Pawn pawn, byte rowFrom, byte colFrom, byte rowTo, byte colTo, Piece captured, char promoPiece, String errTag) throws MoveNotationException{
		int dir = this.whiteToMove ? 1 : -1;
	    boolean isPromo = (rowTo == (this.whiteToMove ? 7 : 0));
	    
	    // Straight move
	    if (colTo == colFrom) { 
	        if (rowTo == rowFrom + dir && captured == null) {
	            return this.executePawnForwardMove(pawn, rowFrom, rowTo, colTo, isPromo, promoPiece);
	        } else if (!pawn.hasMoved() && rowTo == rowFrom + (dir * 2) && captured == null && this.board[rowFrom + dir][colFrom] == null) {
	            return this.executePawnForwardMove(pawn, rowFrom, rowTo, colTo, false, ' '); //double jump
	        } else {
	            throw new MoveNotationException(errTag, "Pawn is blocked or position invalid");
	        }
	    } 
	    //Diagonal move (Capture)
	    else if (Math.abs(colTo - colFrom) == 1 && rowTo == rowFrom + dir) { 
	        if (captured != null) {
	            return this.executePawnCaptureMove(pawn, captured, rowTo, colTo, isPromo, promoPiece);
	        } else if (!this.moves.isEmpty()) { 
	            //En Passant
	            Move lastMove = this.moves.getLast();
	            if (lastMove.isPawnDoubleFowardMove() && lastMove.getMovedPiece().isWhite() != this.whiteToMove) {
	                Piece epTarget = lastMove.getMovedPiece();
	                if (epTarget.getRow() == rowFrom && epTarget.getCol() == colTo) {
	                    return this.executePawnCaptureMove(pawn, epTarget, rowTo, colTo, false, ' ');
	                }
	            }
	        }
	        throw new MoveNotationException(errTag, "There is no piece on the diagonal next to the pawn to capture");
	    }
	    
	    throw new MoveNotationException(errTag, "Pawn does not move in this direction");
	}
	
	
	//knight movement
	/**
	 * Checks whether a knight move between two squares is geometrically valid (an "L" shape).
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @return {@code true} if the move is a valid knight move
	 */
	private boolean knightCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return Math.abs(rowFrom - rowTo) * Math.abs(colFrom - colTo) == 2;
	}
	/**
	 * Checks whether the given knight can move to the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param n the knight to check
	 * @return {@code true} if the move is a valid knight move
	 */
	private boolean knightCanMove(byte rowTo, byte colTo, Knight n) {
		return this.knightCanMove(rowTo, colTo, n.getRow(), n.getCol());
	}
	
	/**
	 * Resolves a knight move parsed from algebraic notation with no disambiguation
	 * (e.g. "Nf3"), finding the single knight of the player to move that can legally
	 * reach the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no knight or more than one knight can make this move,
	 *         the capture rules are violated, or the move leaves the mover's king in check
	 */
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Knight n)-> 
			knightCanMove(rowTo, colTo, n), this.whiteToMove?this.whiteKnights:this.blackKnights, Knight.class);
	}
	
	
	
	
	/**
	 * Resolves a knight move parsed from algebraic notation disambiguated by a single
	 * origin file or rank (e.g. "Nbd7" or "N1d7").
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowOrColFrom the disambiguating origin row or column value
	 * @param isRow {@code true} if {@code rowOrColFrom} represents a row (rank), {@code false} if it represents a column (file)
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no matching knight can make this move, the capture
	 *         rules are violated, or the move leaves the mover's king in check
	 */
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom ,isRow, (Knight n)-> 
			knightCanMove(rowTo, colTo, n), this.whiteToMove?this.whiteKnights:this.blackKnights, Knight.class);
	}
	
	/**
	 * Resolves a knight move parsed from algebraic notation with a fully specified
	 * origin square (e.g. "Nb1d2").
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if there is no knight on the origin square, the move
	 *         geometry is invalid, the capture rules are violated, or the move leaves the
	 *         mover's king in check
	 */
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, knightCanMove(rowTo, colTo, rowFrom, colFrom), Knight.class);
		
	}
	
	//bishop movement
	
	/**
	 * Checks whether the given bishop can move to the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param b the bishop to check
	 * @return {@code true} if the move is a valid, unobstructed diagonal move
	 */
	private boolean bishopCanMove(byte rowTo, byte colTo, Bishop b) {
		return this.bishopCanMove(rowTo, colTo, b.getRow(), b.getCol());
	}
	/**
	 * Checks whether a bishop move between two squares is geometrically valid, i.e.
	 * lies on the same diagonal or anti-diagonal, and the path between them is empty.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @return {@code true} if the move is a valid, unobstructed diagonal move
	 */
	private boolean bishopCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return (checkIfIsSameDiagonal(rowTo, colTo, rowFrom, colFrom) 
				&& this.isDiagonalEmptyBetween2Pieces(rowTo, colTo, rowFrom, colFrom)) ||
				(checkIfIsSameAntiDiagonal(rowTo, colTo, rowFrom, colFrom) 
				&& this.isAntiDiagonalEmptyBetween2Pieces(rowTo, colTo, rowFrom, colFrom));
	}
	
	/**
	 * Resolves a bishop move parsed from algebraic notation with no disambiguation
	 * (e.g. "Bf4"), finding the single bishop of the player to move that can legally
	 * reach the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no bishop or more than one bishop can make this move,
	 *         the capture rules are violated, or the move leaves the mover's king in check
	 */
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Bishop b)-> 
			this.bishopCanMove(rowTo, colTo, b)
		,(this.whiteToMove?this.whiteBishops:this.blackBishops), Bishop.class);
	}
	
	/**
	 * Resolves a bishop move parsed from algebraic notation disambiguated by a single
	 * origin file or rank.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowOrColFrom the disambiguating origin row or column value
	 * @param isRow {@code true} if {@code rowOrColFrom} represents a row (rank), {@code false} if it represents a column (file)
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no matching bishop can make this move, the capture
	 *         rules are violated, or the move leaves the mover's king in check
	 */
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Bishop b)-> 
			this.bishopCanMove(rowTo, colTo, b)
		,(this.whiteToMove?this.whiteBishops:this.blackBishops), Bishop.class);
	}
	
	/**
	 * Resolves a bishop move parsed from algebraic notation with a fully specified origin square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if there is no bishop on the origin square, the move
	 *         geometry is invalid, the capture rules are violated, or the move leaves the
	 *         mover's king in check
	 */
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			bishopCanMove(rowTo, colTo, rowFrom, colFrom)
		, Bishop.class);
	}
	
	
	//Rook movement
	/**
	 * Checks whether a rook move between two squares is geometrically valid, i.e.
	 * lies on the same row or column, and the path between them is empty.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @return {@code true} if the move is a valid, unobstructed straight-line move
	 */
	private boolean rookCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom){
		return (rowTo == rowFrom && this.isRowEmptyBetween2Col(rowTo, colFrom, colTo)) ||
				(colTo == colFrom && this.isColEmptyBetween2Row(colTo, rowFrom, rowTo)); 
	}
	/**
	 * Checks whether the given rook can move to the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param r the rook to check
	 * @return {@code true} if the move is a valid, unobstructed straight-line move
	 */
	private boolean rookCanMove(byte rowTo, byte colTo, Rook r) {
		return this.rookCanMove(rowTo, colTo, r.getRow(), r.getCol());
	}
	
	/**
	 * Resolves a rook move parsed from algebraic notation with no disambiguation
	 * (e.g. "Rd1"), finding the single rook of the player to move that can legally
	 * reach the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no rook or more than one rook can make this move,
	 *         the capture rules are violated, or the move leaves the mover's king in check
	 */
	private Move rookMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Rook r)-> 
			this.rookCanMove(rowTo, colTo, r)
		,(this.whiteToMove?this.whiteRooks:this.blackRooks), Rook.class);
	}
	
	/**
	 * Resolves a rook move parsed from algebraic notation disambiguated by a single
	 * origin file or rank.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowOrColFrom the disambiguating origin row or column value
	 * @param isRow {@code true} if {@code rowOrColFrom} represents a row (rank), {@code false} if it represents a column (file)
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no matching rook can make this move, the capture
	 *         rules are violated, or the move leaves the mover's king in check
	 */
	private Move rookMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Rook r)-> 
			this.rookCanMove(rowTo, colTo, r)
		,(this.whiteToMove?this.whiteRooks:this.blackRooks), Rook.class);
	}
	
	/**
	 * Resolves a rook move parsed from algebraic notation with a fully specified origin square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if there is no rook on the origin square, the move
	 *         geometry is invalid, the capture rules are violated, or the move leaves the
	 *         mover's king in check
	 */
	private Move rookMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			rookCanMove(rowTo, colTo, rowFrom, colFrom)
		, Rook.class);
	}
	
	//Queen movement
	/**
	 * Checks whether a queen move between two squares is geometrically valid, i.e.
	 * valid as either a rook-like or a bishop-like move.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @return {@code true} if the move is a valid, unobstructed queen move
	 */
	private boolean queenCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return this.rookCanMove(rowTo, colTo, rowFrom, colFrom) || this.bishopCanMove(rowTo, colTo, rowFrom, colFrom);
	}
	/**
	 * Checks whether the given queen can move to the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param q the queen to check
	 * @return {@code true} if the move is a valid, unobstructed queen move
	 */
	private boolean queenCanMove(byte rowTo, byte colTo, Queen q) {
		return this.queenCanMove(rowTo, colTo, q.getRow(), q.getCol());
	}
	
	/**
	 * Resolves a queen move parsed from algebraic notation with no disambiguation
	 * (e.g. "Qh5"), finding the single queen of the player to move that can legally
	 * reach the destination square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no queen or more than one queen can make this move,
	 *         the capture rules are violated, or the move leaves the mover's king in check
	 */
	private Move queenMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Queen q)-> 
			this.queenCanMove(rowTo, colTo, q)
		,(this.whiteToMove?this.whiteQueens:this.blackQueens), Queen.class);
	}
	
	/**
	 * Resolves a queen move parsed from algebraic notation disambiguated by a single
	 * origin file or rank.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowOrColFrom the disambiguating origin row or column value
	 * @param isRow {@code true} if {@code rowOrColFrom} represents a row (rank), {@code false} if it represents a column (file)
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no matching queen can make this move, the capture
	 *         rules are violated, or the move leaves the mover's king in check
	 */
	private Move queenMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Queen q)-> 
			this.queenCanMove(rowTo, colTo, q)
		,(this.whiteToMove?this.whiteQueens:this.blackQueens), Queen.class);
	}
	
	/**
	 * Resolves a queen move parsed from algebraic notation with a fully specified origin square.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if there is no queen on the origin square, the move
	 *         geometry is invalid, the capture rules are violated, or the move leaves the
	 *         mover's king in check
	 */
	private Move queenMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			queenCanMove(rowTo, colTo, rowFrom, colFrom)
		, Queen.class);
	}
	
	//King movement
	/**
	 * Checks whether a king move between two squares is geometrically valid, i.e.
	 * at most one square away in any direction.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @return {@code true} if the destination is within one square of the origin
	 */
	private boolean kingCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return Math.abs(rowTo - rowFrom) <= 1 && Math.abs(colTo - colFrom) <= 1;
	}
	/**
	 * Checks whether the given king can move to the destination square (ignoring castling).
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param k the king to check
	 * @return {@code true} if the destination is within one square of the king's current position
	 */
	private boolean kingCanMove(byte rowTo, byte colTo, King k) {
		return this.kingCanMove(rowTo, colTo, k.getRow(), k.getCol());
	}
	
	/**
	 * Resolves a king move parsed from algebraic notation with no disambiguation (e.g. "Kg1").
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if the king cannot geometrically move to the destination,
	 *         the capture rules are violated, or the move leaves the king in check
	 */
	private Move kingMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing : this.blackKing;
		if(!this.kingCanMove(rowTo, colTo, king)) {
			throw new MoveNotationException(moveStr, "its impossible to to the King move to this position");
		}
		Move move = this.executeMove(king, rowTo, colTo, this.getCapturedPiece(rowTo, colTo, capture, moveStr));
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
		
	}
	/**
	 * Resolves a king move parsed from algebraic notation disambiguated by a single
	 * origin file or rank. Since there is only one king per side, this simply validates
	 * that the king is at the specified row/column before delegating to
	 * {@link #kingMove(byte, byte, boolean, String)}.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowOrColFrom the disambiguating origin row or column value
	 * @param isRow {@code true} if {@code rowOrColFrom} represents a row (rank), {@code false} if it represents a column (file)
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if the king is not at the specified position, or any
	 *         condition of {@link #kingMove(byte, byte, boolean, String)} is violated
	 */
	private Move kingMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing : this.blackKing;
		if((isRow?king.getRow():king.getCol()) != rowOrColFrom) {
			throw new MoveNotationException(moveStr, "king is not at this position");
		}
		return this.kingMove(rowTo, colTo, capture, moveStr);
	}
	
	/**
	 * Resolves a king move parsed from algebraic notation with a fully specified origin
	 * square. Since there is only one king per side, this simply validates that the king
	 * is at the specified square before delegating to {@link #kingMove(byte, byte, boolean, String)}.
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if the king is not at the specified square, or any
	 *         condition of {@link #kingMove(byte, byte, boolean, String)} is violated
	 */
	private Move kingMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing : this.blackKing;
		if(king.getRow() != rowFrom || king.getCol() != colFrom) {
			throw new MoveNotationException(moveStr, "king is not at this position");
		}
		return this.kingMove(rowTo, colTo, capture, moveStr);
	}
	
	
	
	//general movements functions
	
	/**
	 * Functional interface representing a movement condition test for a specific piece type.
	 *
	 * @param <P> the type of piece being tested
	 */
	private interface Condition<P extends Piece>{
		/**
		 * Tests whether the given piece satisfies the movement condition.
		 *
		 * @param piece the piece to test
		 * @return {@code true} if the condition holds for this piece
		 */
		boolean isTrue(P piece);
	}
	
	//interface DelayedContition used to only execute  slow boolean functions later prioritizing faster operations that can fail before this function get executed 
	/**
	 * Functional interface representing a movement condition test that is evaluated lazily,
	 * used to prioritize cheaper validations (e.g. piece-type checks) before executing
	 * potentially more expensive geometry/obstruction checks.
	 */
	private interface DelayedCondition{
		/**
		 * Tests whether the delayed condition currently holds.
		 *
		 * @return {@code true} if the condition holds
		 */
		boolean isTrue();
	}
	
	/**
	 * Generic helper that resolves a move for a given piece type when no disambiguation
	 * was provided, by searching all pieces of that type belonging to the player to move
	 * and finding exactly one that satisfies the given movement condition.
	 *
	 * @param <P> the type of piece being moved
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param cond the movement condition each candidate piece must satisfy
	 * @param pieces the list of candidate pieces of the given type
	 * @param classPiece the {@link Class} object for the piece type, used for error messages
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no piece or more than one piece satisfies the
	 *         condition, the capture rules are violated, or the move leaves the mover's
	 *         king in check
	 */
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, Condition<P> cond, ArrayList<P> pieces, Class<P> classPiece) throws MoveNotationException{
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		P pieceToMove = searchPieceToMove(pieces, cond, moveStr, classPiece);
		Move move = this.executeMove(pieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	/**
	 * Generic helper that resolves a move for a given piece type when disambiguated by
	 * a single origin file or rank, narrowing the search to pieces matching that
	 * row/column before applying the movement condition.
	 *
	 * @param <P> the type of piece being moved
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowOrColFrom the disambiguating origin row or column value
	 * @param isRow {@code true} if {@code rowOrColFrom} represents a row (rank), {@code false} if it represents a column (file)
	 * @param cond the movement condition each candidate piece must satisfy
	 * @param pieces the list of candidate pieces of the given type
	 * @param classPiece the {@link Class} object for the piece type, used for error messages
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if no matching piece satisfies the condition, the
	 *         capture rules are violated, or the move leaves the mover's king in check
	 */
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow, Condition<P> cond, ArrayList<P> pieces, Class<P> classPiece) throws MoveNotationException {
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		P pieceToMove = searchPieceToMove(pieces, (P p)->((isRow? p.getRow(): p.getCol()) == rowOrColFrom) && cond.isTrue(p), moveStr, classPiece);
		Move move = this.executeMove(pieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	
	/**
	 * Generic helper that resolves a move for a given piece type when the exact origin
	 * square is specified, using a lazily-evaluated ({@link DelayedCondition}) geometry
	 * check so that the cheaper piece-type check on the origin square runs first.
	 *
	 * @param <P> the type of piece being moved
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @param cond the lazily-evaluated movement/geometry condition
	 * @param classPiece the expected {@link Class} of the piece on the origin square
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if there is no piece of the expected type on the
	 *         origin square, the condition fails, the capture rules are violated, or
	 *         the move leaves the mover's king in check
	 */
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom, DelayedCondition cond, Class<P> classPiece) throws MoveNotationException{
		
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		
		Piece possiblePieceToMove = this.board[rowFrom][colFrom];
		if(!(classPiece.isInstance(possiblePieceToMove))) throw new MoveNotationException(moveStr, "there is no" + classPiece.getSimpleName() + " in this square");
		
		if(!cond.isTrue()){
			throw new MoveNotationException(moveStr, "its impossible to a " + classPiece.getSimpleName() + " move beetween those 2 position");
		}
		
		Move move = this.executeMove(possiblePieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	/**
	 * Generic helper that resolves a move for a given piece type when the exact origin
	 * square is specified and the geometry condition has already been evaluated eagerly
	 * (as a plain {@code boolean}) before this call.
	 *
	 * @param <P> the type of piece being moved
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @param rowFrom the exact origin row
	 * @param colFrom the exact origin column
	 * @param fastCond the already-evaluated movement/geometry condition
	 * @param classPiece the expected {@link Class} of the piece on the origin square
	 * @return the resulting {@link Move}
	 * @throws MoveNotationException if {@code fastCond} is {@code false}, there is no
	 *         piece of the expected type on the origin square, the capture rules are
	 *         violated, or the move leaves the mover's king in check
	 */
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom, boolean fastCond, Class<P> classPiece) throws MoveNotationException{
		if(!fastCond){
			throw new MoveNotationException(moveStr, "its impossible to a " + classPiece.getSimpleName() + " move beetween those 2 position");
		}
		
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		
		Piece possiblePieceToMove = this.board[rowFrom][colFrom];
		if(!(classPiece.isInstance(possiblePieceToMove))) throw new MoveNotationException(moveStr, "there is no" + classPiece.getSimpleName() + " in this square");
		
		
		
		Move move = this.executeMove(possiblePieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	
	/**
	 * Searches a list of pieces of a given type for exactly one piece that satisfies
	 * the given movement condition, used to resolve ambiguous algebraic notation
	 * (when no explicit origin square was given).
	 *
	 * @param <P> the type of piece being searched
	 * @param pieces the candidate pieces to search
	 * @param cond the movement condition each candidate must satisfy
	 * @param moveStr the original move string, used for error reporting
	 * @param classPiece the {@link Class} object for the piece type, used for error messages
	 * @return the single piece that satisfies the condition
	 * @throws MoveNotationException if no piece satisfies the condition, or more than
	 *         one piece does (ambiguous move)
	 */
	private static <P extends Piece> P searchPieceToMove(ArrayList<P> pieces, Condition<P> cond, String moveStr, Class<P> classPiece) throws MoveNotationException{
		P pieceToMove = null;
		int numOfPiecesOfThisTypeThatCanMove = 0;
		for(P p : pieces) {
			if(cond.isTrue(p)) {
				pieceToMove = p;
				numOfPiecesOfThisTypeThatCanMove++;
			}
		}
		if(pieceToMove == null) 
			throw new MoveNotationException(moveStr, "there is no " + classPiece.getSimpleName()  + " that can move to this square");
		if (numOfPiecesOfThisTypeThatCanMove > 1) 
			throw new MoveNotationException(moveStr, "there is more than one " + classPiece.getSimpleName() + " that can move to this square");
		
		return pieceToMove;
		
	}
	
	
	/**
	 * Resolves the piece being captured (if any) at the destination square, validating
	 * that the capture indicator in the move string is consistent with the actual board
	 * state (i.e. a capture symbol requires an enemy piece to be present, and the absence
	 * of one requires the square to be empty).
	 *
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param capture {@code true} if the move string indicates a capture
	 * @param moveStr the original move string, used for error reporting
	 * @return the captured piece, or {@code null} if the destination square is empty
	 * @throws MoveNotationException if the capture indicator does not match the board
	 *         state, or the destination square already contains one of the mover's own pieces
	 */
	private Piece getCapturedPiece(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		Piece pieceCaptured = this.board[rowTo][colTo];
		if(pieceCaptured == null) {
			if(capture) {
				throw new MoveNotationException(moveStr, "You are not capturing, the position this piece is going has no pieces to capture");
			}	
		}else {
			if(!capture) {
				throw new MoveNotationException(moveStr, "The position you want to go is occupied, if you want to capture you forgot to put the x symbol");
			}
			if(pieceCaptured.isWhite() == this.whiteToMove) {
				throw new MoveNotationException(moveStr, "The square you want to go alredy has one of your pieces");
			}
		}
		
		return pieceCaptured;
	}
	
	/**
	 * Executes a generic (non-pawn, non-castle) piece move on the board: removes any
	 * captured piece from its piece list, updates the board matrix, updates the piece's
	 * internal position, and marks it as moved.
	 *
	 * @param pieceToMove the piece being moved
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param captured the piece being captured, or {@code null} if none
	 * @return the resulting {@link Move}
	 * @throws AssertionError if the captured piece could not be removed from its piece list
	 */
	private Move executeMove(Piece pieceToMove, byte rowTo, byte colTo, Piece captured) {
		if(captured != null && !this.removePieceFromArrays(captured))throw new AssertionError("Error when removing captured piece");
		byte pieceToMoveOldInfo = pieceToMove.getPieceInfo();
		this.board[pieceToMove.getRow()][pieceToMove.getCol()] = null;
		pieceToMove.setPos(rowTo, colTo);
		this.board[pieceToMove.getRow()][pieceToMove.getCol()] = pieceToMove;
		pieceToMove.setMoved(true);
		
		return new Move(pieceToMoveOldInfo, pieceToMove, captured);
	}
	
	
	/**
	 * Removes the given piece from its corresponding type-specific piece list
	 * (e.g. a captured queen is removed from {@link #whiteQueens} or {@link #blackQueens}).
	 *
	 * @param piece the piece to remove
	 * @return {@code true} if the piece was found and removed, {@code false} otherwise
	 *         (including when the piece is of an unrecognized type, e.g. a king)
	 */
	private Boolean removePieceFromArrays(Piece piece) {
		return switch (piece) {
        	case Queen q  -> (piece.isWhite() ? whiteQueens : blackQueens).remove(q);
        	case Rook r   -> (piece.isWhite() ? whiteRooks : blackRooks).remove(r);
        	case Bishop b -> (piece.isWhite() ? whiteBishops : blackBishops).remove(b);
        	case Knight n -> (piece.isWhite() ? whiteKnights : blackKnights).remove(n);
        	case Pawn p   -> (piece.isWhite() ? whitePawns : blackPawns).remove(p);
        	default       -> false;
		};
	}
	
	//king verifications
	/**
	 * Checks whether the king of the player currently to move is in check.
	 *
	 * @return {@code true} if the side to move's king is under attack
	 */
	private boolean isKingInCheck() {
		return this.isKingInCheck(this.whiteToMove);
	}
	/**
	 * Checks whether the king of the given color is currently under attack by any
	 * enemy piece, checking pawn, knight, rook, bishop, and queen attack patterns
	 * (the king itself cannot deliver check under normal rules, so it is not checked here).
	 *
	 * @param white {@code true} to check the white king, {@code false} to check the black king
	 * @return {@code true} if the specified king is in check
	 */
	private boolean isKingInCheck(Boolean white) {
		King king = white? this.whiteKing : this.blackKing;
		
		//pawns logic
		
		int row = king.getRow() + (white? 1:-1);
		int col = king.getCol();
		if(row >=0 && row <=7) {
			if((col > 0 && this.board[row][col - 1] instanceof Pawn p && p.isWhite() != white) ||
				(col < 7 && this.board[row][col + 1] instanceof Pawn p2 && p2.isWhite() != white)) 
			{
				return true;
			}
		}
		
		
		//Knigths logic
		ArrayList<Knight> enemyKnights = white? this.blackKnights : this.whiteKnights;
		for(Knight n : enemyKnights) {
			if(Math.abs(n.getRow() - king.getRow()) * Math.abs(n.getCol() - king.getCol()) == 2) {
				return true;
			}
		}
		
		//Rooks logic
		ArrayList<Rook> enemyRooks = white? this.blackRooks : this.whiteRooks;
		for(Rook r : enemyRooks) {
			if((r.getCol() == king.getCol() && this.isColEmptyBetween2Pieces(r, king)) ||
				(r.getRow() == king.getRow() && this.isRowEmptyBetween2Pieces(r, king)))
			{
				return true;
			}
		}
		
		//Bishops logic
		ArrayList<Bishop> enemyBishops = white? this.blackBishops : this.whiteBishops;
		for(Bishop b : enemyBishops) {
			if((checkIfIsSameDiagonal(b, king) && this.isDiagonalEmptyBetween2Pieces(b, king)) ||
				(checkIfIsSameAntiDiagonal(b, king) && this.isAntiDiagonalEmptyBetween2Pieces(b, king)))
			{
				return true;
			}
		}
		
		//queens logic
		ArrayList<Queen> enemyQueens = white? this.blackQueens : this.whiteQueens;
		for(Queen q : enemyQueens) {
			if((q.getCol() == king.getCol() && this.isColEmptyBetween2Pieces(q, king)) ||
				(q.getRow() == king.getRow() && this.isRowEmptyBetween2Pieces(q, king)) ||
				(checkIfIsSameDiagonal(q, king) && this.isDiagonalEmptyBetween2Pieces(q, king)) ||
				(checkIfIsSameAntiDiagonal(q, king) && this.isAntiDiagonalEmptyBetween2Pieces(q, king)))
			{
				return true;
			}
		}
		
		return false;
		
	}
	/**
	 * Verifies, after a move has been tentatively executed, that the mover's own king
	 * is not left in check. If it is, the move is undone and an exception is thrown.
	 *
	 * @param move the move that was just executed
	 * @param moveStr the original move string, used for error reporting
	 * @throws MoveNotationException if the move leaves the mover's king in check
	 */
	private void checkIfKingIsInCheckAfterMove(Move move, String moveStr) throws MoveNotationException{
		if(this.isKingInCheck()) {
			this.undoMove(move);
			throw new MoveNotationException(moveStr, "can't move this piece because your king is or will be in danger");
		}
	}
	
	/**
	 * Validates and resolves the game state after a move parsed from algebraic notation,
	 * cross-checking the check/checkmate suffix ('+' or '#') supplied in the move string
	 * against the actual resulting position. Sets {@link #result} to a win for the mover
	 * on checkmate, or to a draw if the opponent has no legal moves and is not in check
	 * (stalemate). If the suffix does not match the real game state, the move is undone
	 * and an exception is thrown.
	 *
	 * @param move the move that was just executed
	 * @param checkOrCheckmate {@code true} if the move string ended with '+' or '#'
	 * @param moveStr the original move string, used for error reporting
	 * @throws MoveNotationException if the check/checkmate suffix does not match the
	 *         actual resulting position
	 */
	private void resolveStateOfGameAfterMove(Move move, boolean checkOrCheckmate, String moveStr) throws MoveNotationException{
		if(this.isKingInCheck(!this.whiteToMove)) {
			if(this.haslegalMove(!this.whiteToMove)) {
				if(checkOrCheckmate) {
					if(moveStr.charAt(moveStr.length() - 1) != '+') {
						this.undoMove(move);
						throw new MoveNotationException(moveStr, "This is not a checkmate, just a check, you should use the + symbol");
					}
				}else {
					this.undoMove(move);
					throw new MoveNotationException(moveStr, "You forgot to put the +(check simbol) in the end of your move");
				}
			}else {
				if(checkOrCheckmate) {
					if(moveStr.charAt(moveStr.length() - 1) != '#') {
						this.undoMove(move);
						throw new MoveNotationException(moveStr, "This is not a check, its a checkmate, rewrite your move with the # symbol and you win");
					}else {
						this.result = this.whiteToMove? "1-0":"0-1";
					}
				}else {
					this.undoMove(move);
					throw new MoveNotationException(moveStr, "You forgot to put the #(checkmate simbol) in the end of your move");
				}
			}
			
		}else {
			if(checkOrCheckmate) {
				this.undoMove(move);
				throw new MoveNotationException(moveStr, "This is not a check neither a checkmate");
			}
			if(!this.haslegalMove(!this.whiteToMove)) {
				this.result = "1/2-1/2";
			}
		}
	}
	
	
	/**
	 * Resolves the game state after a move made via board coordinates (GUI), where there
	 * is no algebraic check/checkmate suffix to validate against; simply determines the
	 * result based on the actual resulting position.
	 */
	private void resolveStateOfGameAfterMove() {
		this.resolveStateOfGame(!this.whiteToMove);
	}
	
	//package private
	/**
	 * Determines and sets the game {@link #result} based on whether the given side is
	 * in checkmate, stalemate, or the fifty-move rule has been reached.
	 *
	 * @param whiteToMove {@code true} to evaluate the position from white's perspective
	 *                     (i.e. it is white's turn and white's status is being checked),
	 *                     {@code false} for black
	 */
	void resolveStateOfGame(boolean whiteToMove) {
		boolean isCheck = this.isKingInCheck(whiteToMove);
	    boolean hasMoves = this.haslegalMove(whiteToMove);
		
	    if (isCheck && !hasMoves) {
	        this.result = !whiteToMove ? "1-0" : "0-1"; 
	        
	    } else if ((!isCheck && !hasMoves) || this.halfmoveClock >= 100) {
	    	this.result = "1/2-1/2";
	    }
	}
	
	/**
	 * Checks whether the given side has at least one legal move available, checking
	 * piece types roughly in order of expected mobility/performance (queens, knights,
	 * rooks, bishops, pawns, then the king) to return as early as possible.
	 *
	 * @param white {@code true} to check white's legal moves, {@code false} for black
	 * @return {@code true} if at least one legal move exists for the given side
	 */
	private boolean haslegalMove(boolean white) {
		
		//queens (Extreme high mobility so they have the most chance of finding a legal move)
		for (Queen q : white? this.whiteQueens : this.blackQueens) {
	        if (this.queenHasLegalMoves(q)) return true;
	    }
		
		//Knights (Math for knight is faster to process)
		for (Knight n : (white? whiteKnights : blackKnights)) {
	        if (this.knightHasLegalMoves(n)) return true;
	    }
		
		//Rooks and bishops (High mobility so high chances of finding a legal move)
		for (Rook r : white? this.whiteRooks : this.blackRooks) {
	        if (this.rookHasLegalMoves(r)) return true;
	    }
		for (Bishop b : (white? whiteBishops : blackBishops)) {
	        if (this.bishopHasLegalMoves(b)) return true;
	    }
		
		//Pawns (In end game and mid game most of them are blocked)
		for (Pawn p : (white? this.whitePawns : this.blackPawns)) {
			if (this.pawnHasLegalMoves(p)) return true;
		}
		
		//king (Is locked in tiny spaces with almost no moves legal the majority of the game)
	    if (this.kingHasLegalMoves(white? this.whiteKing : this.blackKing)) return true;
	    
	    //if it reached here there are no moves legal
	    return false;
	    
	}
	
	
	//hasLegalMoves for each piece
	/**
	 * Checks whether moving the given piece to the specified square is legal, i.e. the
	 * destination is empty or holds an enemy piece, and doing so would not leave the
	 * mover's own king in check. The move is executed and immediately undone as part
	 * of this check (a "try and revert" approach).
	 *
	 * @param p the piece being tested
	 * @param nextRow the candidate destination row
	 * @param nextCol the candidate destination column
	 * @param isWhite {@code true} if the piece belongs to white
	 * @return {@code true} if the move is legal and safe for the mover's king
	 */
	private boolean isMoveSafe(Piece p, byte nextRow, byte nextCol, boolean isWhite) {
		boolean isLegal = false;
		
		Piece captured = this.board[nextRow][nextCol];
		if(captured == null || captured.isWhite() != isWhite) {
			Move move = this.executeMove(p, nextRow, nextCol, captured);
			isLegal = !this.isKingInCheck(isWhite);
			this.undoMove(move);
		}
		return isLegal;
	}
	
	/**
	 * Checks whether the given knight has at least one legal move, by testing all
	 * eight possible "L-shaped" destination squares.
	 *
	 * @param n the knight to check
	 * @return {@code true} if at least one legal move exists for this knight
	 */
	private boolean knightHasLegalMoves(Knight n) {
		boolean isWhite = n.isWhite();
		byte row = n.getRow();
		byte col = n.getCol();
		
		//All 8 possible knight jumps (L move)
		final int[] rowOffsets = {-2, -2, -1, -1,  1, 1,  2, 2};
	    final int[] colOffsets = {-1,  1, -2,  2, -2, 2, -1, 1};
		
	    for(int i = 0; i < 8; i++) {
	    	byte nextRow = (byte)(row + rowOffsets[i]);
			byte nextCol = (byte)(col + colOffsets[i]);
			if(nextCol >= 0 && nextCol <= 7 && nextRow >= 0 && nextRow <= 7 && this.isMoveSafe(n, nextRow, nextCol, isWhite)) {
				return true;
			}
	    }
		return false;
	}
	
	/**
	 * Checks whether the given king has at least one legal move, by testing all eight
	 * adjacent squares. Note: this does not consider castling as a possible legal move.
	 *
	 * @param k the king to check
	 * @return {@code true} if at least one legal (non-castling) move exists for this king
	 */
	private boolean kingHasLegalMoves(King k) {
	    boolean isWhite = k.isWhite();
	    byte row = k.getRow();
	    byte col = k.getCol();
	    
	    // All 8 possible king moves (1 square in any direction)
	    final int[] rowOffsets = {-1, -1, -1,  0, 0,  1, 1, 1};
	    final int[] colOffsets = {-1,  0,  1, -1, 1, -1, 0, 1};
	    
	    for(int i = 0; i < 8; i++) {
	        byte nextRow = (byte)(row + rowOffsets[i]);
	        byte nextCol = (byte)(col + colOffsets[i]);
	        
	        if(nextCol >= 0 && nextCol <= 7 && nextRow >= 0 && nextRow <= 7 && this.isMoveSafe(k, nextRow, nextCol, isWhite)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	//sends a ray in multiple direction passed in parameters testing each position until reach a piece
	/**
	 * Casts rays from a sliding piece's (rook, bishop, or queen) position in each of the
	 * given directions, testing each empty square along the way (and the first blocking
	 * square, if it holds an enemy piece) for a legal, king-safe move.
	 *
	 * @param p the sliding piece to check
	 * @param rowDirections the row deltas for each ray direction to test
	 * @param colDirections the column deltas for each ray direction to test (parallel to {@code rowDirections})
	 * @return {@code true} if at least one legal move exists along any of the given rays
	 */
	private boolean rayCasting(Piece p, int[] rowDirections, int[] colDirections) {
		boolean isWhite = p.isWhite();
	    byte row = p.getRow();
	    byte col = p.getCol();
		for(int i = 0; i < rowDirections.length; i++) {
			byte nextRow = (byte)(row + rowDirections[i]);
			byte nextCol = (byte)(col + colDirections[i]);
			while(nextRow >= 0 && nextRow <=7 && nextCol >= 0 && nextCol <=7 && this.board[nextRow][nextCol] == null) {
				if(this.isMoveSafe(p, nextRow, nextCol, isWhite)) {
					return true;
				}
				nextRow += rowDirections[i];
				nextCol += colDirections[i];
				
			}
			
			if(nextCol >= 0 && nextCol <= 7 && nextRow >= 0 && nextRow <= 7 && this.isMoveSafe(p, nextRow, nextCol, isWhite)) {
				return true;
			}
			

		}
		return false;
	}
	
	/**
	 * Checks whether the given rook has at least one legal move, casting rays along
	 * the four straight-line directions (up, down, left, right).
	 *
	 * @param r the rook to check
	 * @return {@code true} if at least one legal move exists for this rook
	 */
	private boolean rookHasLegalMoves(Rook r) {
		return rayCasting(r, new int[] {1,-1, 0, 0}, new int[]{0, 0, 1, -1});
	}
	
	/**
	 * Checks whether the given bishop has at least one legal move, casting rays along
	 * the four diagonal directions.
	 *
	 * @param b the bishop to check
	 * @return {@code true} if at least one legal move exists for this bishop
	 */
	private boolean bishopHasLegalMoves(Bishop b) {
		return rayCasting(b, new int[] {1,-1, 1, -1}, new int[]{1, -1, -1, 1});
	}
	
	/**
	 * Checks whether the given queen has at least one legal move, casting rays along
	 * all eight straight-line and diagonal directions.
	 *
	 * @param q the queen to check
	 * @return {@code true} if at least one legal move exists for this queen
	 */
	private boolean queenHasLegalMoves(Queen q) {
		return rayCasting(q, new int[] {1,-1, 0, 0, 1,-1, 1, -1}, new int[]{0, 0, 1, -1, 1, -1, -1, 1});
	}
	
	/**
	 * Checks whether the given pawn has at least one legal move, considering single
	 * forward pushes, diagonal captures, the initial double-square push, and en passant captures.
	 *
	 * @param p the pawn to check
	 * @return {@code true} if at least one legal move exists for this pawn
	 */
	private boolean pawnHasLegalMoves(Pawn p) {
		boolean isWhite = p.isWhite();
	    byte row = p.getRow();
	    byte col = p.getCol();
	    
	    byte nextRow = (byte)(row + (isWhite?1:-1));
	    byte nextCol = col;
	    if(nextRow >= 0 && nextRow <= 7) {
	    	if(this.board[nextRow][nextCol] == null && this.isMoveSafe(p, nextRow, nextCol, isWhite)) {
	 	    	return true;
	 	    }
	 	    nextCol++;
	 	    if(nextCol <= 7) {
	 	    	Piece captured = this.board[nextRow][nextCol];
	 	    	if(captured != null && captured.isWhite() != isWhite && this.isMoveSafe(p, nextRow, nextCol, isWhite)) {
	 	    		return true;
	 	    	}
	 	    }
	 	    nextCol -= 2;
	 	    if(nextCol >= 0) {
	 	    	Piece captured = this.board[nextRow][nextCol];
	 	    	if(captured != null && captured.isWhite() != isWhite && this.isMoveSafe(p, nextRow, nextCol, isWhite)) {
	 	    		return true;
	 	    	}
	 	    }
	    }
	    
	    //double forward logic
	    if(!p.hasMoved() && row == (isWhite?1:6)) {
	    	int direction = isWhite?1:-1;
	    	byte middleRow = (byte)(row + direction);
	    	nextRow = (byte)(row + (direction*2));
	    	nextCol = col;
	    	if(this.board[middleRow][col] == null && this.board[nextRow][nextCol] == null && this.isMoveSafe(p, nextRow, nextCol, isWhite)) {
	 	    	return true;
	 	    }
	    }
	    
	    //en passant logic
	    if(!this.moves.isEmpty()) {
		    Move lastMove = this.moves.getLast();
		    if(lastMove.isPawnDoubleFowardMove() && lastMove.getMovedPiece().isWhite() != isWhite) {
		    	
		    	Piece captured = lastMove.getMovedPiece();
		    	if(captured.getRow() == row && (captured.getCol() == col + 1 || captured.getCol() == col - 1)) {
		    		Move move = this.executePawnCaptureMove(p, captured, (byte)(captured.getRow() + (isWhite?1:-1)), captured.getCol(), false, ' ');
		    		boolean isLegal = !this.isKingInCheck(isWhite);
		    		this.undoMove(move);
		    		return isLegal;
		    	}
		    	
		    }
	    }

	   
	    
	    return false;
	}
	
	
	
	
    //diagonals function test
	/**
	 * Checks whether two squares lie on the same diagonal (top-left to bottom-right direction),
	 * i.e. the difference between row and column is equal for both squares.
	 *
	 * @param row1 row of the first square
	 * @param col1 column of the first square
	 * @param row2 row of the second square
	 * @param col2 column of the second square
	 * @return {@code true} if both squares lie on the same diagonal
	 */
	private static boolean checkIfIsSameDiagonal(byte row1, byte col1, byte row2, byte col2) {
		return (row1 - col1) == (row2 - col2);
	}
	/**
	 * Checks whether two pieces lie on the same diagonal.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece
	 * @return {@code true} if both pieces lie on the same diagonal
	 */
	private static boolean checkIfIsSameDiagonal(Piece piece1, Piece piece2) {
		return checkIfIsSameDiagonal(piece1.getRow(), piece1.getCol(), piece2.getRow(), piece2.getCol());
	}
	
	/**
	 * Checks whether two squares lie on the same anti-diagonal (top-right to bottom-left direction),
	 * i.e. the sum of row and column is equal for both squares.
	 *
	 * @param row1 row of the first square
	 * @param col1 column of the first square
	 * @param row2 row of the second square
	 * @param col2 column of the second square
	 * @return {@code true} if both squares lie on the same anti-diagonal
	 */
	private static boolean checkIfIsSameAntiDiagonal(byte row1, byte col1, byte row2, byte col2) {
		return (row1 + col1) == (row2 + col2);
	}
	/**
	 * Checks whether two pieces lie on the same anti-diagonal.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece
	 * @return {@code true} if both pieces lie on the same anti-diagonal
	 */
	private static boolean checkIfIsSameAntiDiagonal(Piece piece1, Piece piece2) {
		return checkIfIsSameAntiDiagonal(piece1.getRow(), piece1.getCol(), piece2.getRow(), piece2.getCol());
	}
	
	
    
    
    //util functions
	
	
	
	/**
	 * Checks whether all squares strictly between two columns on the same row are empty.
	 *
	 * @param row the row to check
	 * @param colBegin one boundary column (exclusive)
	 * @param colEnd the other boundary column (exclusive); order relative to {@code colBegin} does not matter
	 * @return {@code true} if all squares strictly between the two columns are empty
	 */
	private boolean isRowEmptyBetween2Col(byte row, byte colBegin, byte colEnd) {
		if(colBegin > colEnd) {
			byte temp = colBegin;
			colBegin = colEnd;
			colEnd = temp;
		}
		for(int i = colBegin + 1; i < colEnd; i++) {
			if(this.board[row][i] != null)return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the squares between two pieces on the same row are all empty.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece (assumed to be on the same row as {@code piece1})
	 * @return {@code true} if the path between the two pieces on their shared row is empty
	 */
	private boolean isRowEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isRowEmptyBetween2Col(piece1.getRow(), piece1.getCol(), piece2.getCol());
	}
	
	/**
	 * Checks whether all squares strictly between two rows on the same column are empty.
	 *
	 * @param col the column to check
	 * @param rowBegin one boundary row (exclusive)
	 * @param rowEnd the other boundary row (exclusive); order relative to {@code rowBegin} does not matter
	 * @return {@code true} if all squares strictly between the two rows are empty
	 */
	private boolean isColEmptyBetween2Row(byte col, byte rowBegin, byte rowEnd) {
		if(rowBegin > rowEnd) {
			byte temp = rowBegin;
			rowBegin = rowEnd;
			rowEnd = temp;
		}
		for(int i = rowBegin + 1; i < rowEnd; i++) {
			if(this.board[i][col] != null)return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the squares between two pieces on the same column are all empty.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece (assumed to be on the same column as {@code piece1})
	 * @return {@code true} if the path between the two pieces on their shared column is empty
	 */
	private boolean isColEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isColEmptyBetween2Row(piece1.getCol(), piece1.getRow(), piece2.getRow());
	}
	
	/**
	 * Checks whether all squares strictly between two squares on the same diagonal
	 * (or anti-diagonal) are empty.
	 *
	 * @param row1 row of the first square
	 * @param col1 column of the first square
	 * @param row2 row of the second square
	 * @param col2 column of the second square
	 * @param anti {@code true} to check along an anti-diagonal, {@code false} for a regular diagonal
	 * @return {@code true} if all squares strictly between the two squares are empty
	 */
	private boolean isDiagonalEmptyBetween2Pieces(byte row1, byte col1, byte row2, byte col2, boolean anti) {
		if(row1 > row2) {
			byte tempRow = row1;
			row1 = row2;
			row2 = tempRow;
			byte tempCol = col1;
			col1 = col2;
			col2 = tempCol;
		}
		int n = 1;
		if(anti) n = -1;
		for(int i = row1 + 1, j = col1 + n; i < row2; i++, j+=n) {
			if(this.board[i][j] != null)return false;
		}
		return true;
	}
	
	
	/**
	 * Checks whether the squares between two pieces on the same diagonal (or anti-diagonal) are empty.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece
	 * @param anti {@code true} to check along an anti-diagonal, {@code false} for a regular diagonal
	 * @return {@code true} if the path between the two pieces is empty
	 */
	private boolean isDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2, boolean anti) {
		return this.isDiagonalEmptyBetween2Pieces(piece1.getRow(), piece1.getCol(), piece2.getRow(), piece2.getCol(), anti);
	}
	
	
	/**
	 * Checks whether all squares strictly between two squares on the same (non-anti) diagonal are empty.
	 *
	 * @param row1 row of the first square
	 * @param col1 column of the first square
	 * @param row2 row of the second square
	 * @param col2 column of the second square
	 * @return {@code true} if all squares strictly between the two squares are empty
	 */
	private boolean isDiagonalEmptyBetween2Pieces(byte row1, byte col1, byte row2, byte col2) {
		return this.isDiagonalEmptyBetween2Pieces(row1, col1, row2, col2, false);
	}
	
	/**
	 * Checks whether the squares between two pieces on the same (non-anti) diagonal are empty.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece
	 * @return {@code true} if the path between the two pieces is empty
	 */
	private boolean isDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isDiagonalEmptyBetween2Pieces(piece1, piece2, false);
	}
	
	/**
	 * Checks whether all squares strictly between two squares on the same anti-diagonal are empty.
	 *
	 * @param row1 row of the first square
	 * @param col1 column of the first square
	 * @param row2 row of the second square
	 * @param col2 column of the second square
	 * @return {@code true} if all squares strictly between the two squares are empty
	 */
	private boolean isAntiDiagonalEmptyBetween2Pieces(byte row1, byte col1, byte row2, byte col2) {
		return this.isDiagonalEmptyBetween2Pieces(row1, col1, row2, col2, true);
	}
	/**
	 * Checks whether the squares between two pieces on the same anti-diagonal are empty.
	 *
	 * @param piece1 the first piece
	 * @param piece2 the second piece
	 * @return {@code true} if the path between the two pieces is empty
	 */
	private boolean isAntiDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isDiagonalEmptyBetween2Pieces(piece1, piece2, true);
	}
	
	
	
	
	//string functions
	
	/**
	 * Returns a simple text representation of the board, using each piece's icon
	 * (or a middle dot for empty squares), printed from rank 8 down to rank 1.
	 *
	 * @return a human-readable multi-line string representation of the board
	 */
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
	
	/**
	 * Builds a "reduced" FEN string containing only the fields relevant for detecting
	 * repeated positions: piece placement, active color, castling rights, and en
	 * passant target square (omitting the halfmove clock and fullmove number).
	 *
	 * @return a {@link StringBuilder} containing the reduced FEN representation
	 */
	private StringBuilder getCutFENStrBuilder() {
		StringBuilder fen = new StringBuilder();

        // 1. Piece Placement
        for (int i = 7; i >= 0; i--) { // FEN starts from rank 8 (index 7) to rank 1 (index 0)
            int emptyCount = 0;
            for (int j = 0; j <= 7; j++) {
                Piece piece = this.board[i][j];
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    char pieceChar = piece.getPieceLetter(); 
                    if (!piece.isWhite()) {
                        pieceChar = Character.toLowerCase(pieceChar);
                    }
                    fen.append(pieceChar);
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i > 0) {
                fen.append("/");
            }
        }

        // 2. Active Color
        fen.append(" ");
        fen.append(this.whiteToMove ? "w" : "b");

        // 3. Castling Rights
        fen.append(" ");
        StringBuilder castling = new StringBuilder();
        if (!this.whiteKing.hasMoved()) {
            if (this.board[0][7] instanceof Rook r && !r.hasMoved()) castling.append("K");
            if (this.board[0][0] instanceof Rook r && !r.hasMoved()) castling.append("Q");
        }
        if (!this.blackKing.hasMoved()) {
            if (this.board[7][7] instanceof Rook r && !r.hasMoved()) castling.append("k");
            if (this.board[7][0] instanceof Rook r && !r.hasMoved()) castling.append("q");
        }
        if (castling.length() == 0) {
            fen.append("-");
        } else {
            fen.append(castling);
        }

        // 4. En Passant Target Square
        fen.append(" ");
        if (!this.moves.isEmpty()) {
            Move lastMove = this.moves.getLast();
            if (lastMove.isPawnDoubleFowardMove()) {
                Piece movedPawn = lastMove.getMovedPiece();
                // Target square is behind the pawn that just moved two squares
                int epRow = movedPawn.getRow() + (movedPawn.isWhite() ? -1 : 1);
                char epColChar = (char) ('a' + movedPawn.getCol());
                fen.append(epColChar).append(epRow + 1); // +1 because FEN rows are 1-8
            } else {
                fen.append("-");
            }
        } else {
            fen.append("-");
        }
        
        return fen;
	}
	/**
	 * Returns the reduced FEN string (see {@link #getCutFENStrBuilder()}) as a {@link String},
	 * used as the key for tracking position repetition.
	 *
	 * @return the reduced FEN string
	 */
	private String getCutFEN() {
		return this.getCutFENStrBuilder().toString();
	}
	
	//FEN: Forsyth-Edwards Notation
	/**
	 * Builds the full FEN (Forsyth-Edwards Notation) string representing the current
	 * position, including the halfmove clock and fullmove number.
	 *
	 * @return the full FEN string of the current position
	 */
	public String getFEN() {
        StringBuilder fen = this.getCutFENStrBuilder();

        // 5. Halfmove Clock
        fen.append(" ").append(this.halfmoveClock);

        // 6. Fullmove Number
        fen.append(" ").append(this.fullmoveNumber);

        return fen.toString();
    }
	
	/**
	 * Builds a space-separated string of all moves played so far, in their
	 * algebraic notation form, in chronological order.
	 *
	 * @return the full move sequence as a single string
	 */
	public String getMoveSequence() {
		StringBuilder movesStr = new StringBuilder();
		for(Move move:this.moves) {
			movesStr.append(move.getAlgebricNotation()).append(" ");
		}
		return movesStr.toString();
	}
	
	//used to get algebric notation when the move is with clicks on board
	/**
	 * Computes the standard algebraic notation string for a move that was made via
	 * board coordinates (e.g. a GUI click), including piece letter, disambiguation,
	 * capture symbol, destination square, promotion suffix, and check/checkmate suffix.
	 *
	 * @param move the executed move
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @param promoPiece the piece letter promoted to, if this move was a promotion
	 * @return the algebraic notation string representing this move
	 */
	private String getAlgebraicNotation(Move move, byte rowFrom, byte colFrom, byte rowTo, byte colTo, char promoPiece) {
	    if (move.isCastle()) {
	        return (move.isShortCastle() ? "O-O" : "O-O-O") + this.getCheckOrMateSuffix();
	    }

	    Piece movedPiece = move.getMovedPiece();
	    boolean isCapture = move.getCapturedPiece() != null;

	    StringBuilder sb = new StringBuilder();

	    if (movedPiece instanceof Pawn) {
	        if (isCapture) {
	            sb.append((char) ('a' + colFrom)); // ex: "exd5"
	        }
	    } else {
	        sb.append(movedPiece.getPieceLetter());
	        sb.append(this.getDisambiguation(movedPiece, rowFrom, colFrom, rowTo, colTo));
	    }

	    if (isCapture) {
	        sb.append('x');
	    }

	    sb.append((char) ('a' + colTo));
	    sb.append((char) ('1' + rowTo));

	    if (move.isPromotion()) {
	        sb.append('=').append(Character.toUpperCase(promoPiece));
	    }

	    sb.append(this.getCheckOrMateSuffix());

	    return sb.toString();
	}

	/**
	 * Determines the minimal disambiguation string (none, origin file, origin rank,
	 * or full origin square) needed in algebraic notation to uniquely identify which
	 * piece made the move, by checking whether any other piece of the same type and
	 * color could have legally reached the same destination square.
	 *
	 * @param movedPiece the piece that was moved
	 * @param rowFrom origin row
	 * @param colFrom origin column
	 * @param rowTo destination row
	 * @param colTo destination column
	 * @return the disambiguation string to insert into the algebraic notation
	 *         (empty string if no disambiguation is needed)
	 */
	private String getDisambiguation(Piece movedPiece, byte rowFrom, byte colFrom, byte rowTo, byte colTo) {
	    boolean sameFileFound = false;
	    boolean sameRankFound = false;
	    boolean anyOtherFound = false;

	    switch (movedPiece) {
	        case Knight n -> {
	            for (Knight p : n.isWhite() ? this.whiteKnights : this.blackKnights) {
	                if (p == n) continue;
	                if (this.knightCanMove(rowTo, colTo, p)) {
	                    anyOtherFound = true;
	                    if (p.getCol() == colFrom) sameFileFound = true;
	                    if (p.getRow() == rowFrom) sameRankFound = true;
	                }
	            }
	        }
	        case Bishop b -> {
	            for (Bishop p : b.isWhite() ? this.whiteBishops : this.blackBishops) {
	                if (p == b) continue;
	                if (this.bishopCanMove(rowTo, colTo, p)) {
	                    anyOtherFound = true;
	                    if (p.getCol() == colFrom) sameFileFound = true;
	                    if (p.getRow() == rowFrom) sameRankFound = true;
	                }
	            }
	        }
	        case Rook r -> {
	            for (Rook p : r.isWhite() ? this.whiteRooks : this.blackRooks) {
	                if (p == r) continue;
	                if (this.rookCanMove(rowTo, colTo, p)) {
	                    anyOtherFound = true;
	                    if (p.getCol() == colFrom) sameFileFound = true;
	                    if (p.getRow() == rowFrom) sameRankFound = true;
	                }
	            }
	        }
	        case Queen q -> {
	            for (Queen p : q.isWhite() ? this.whiteQueens : this.blackQueens) {
	                if (p == q) continue;
	                if (this.queenCanMove(rowTo, colTo, p)) {
	                    anyOtherFound = true;
	                    if (p.getCol() == colFrom) sameFileFound = true;
	                    if (p.getRow() == rowFrom) sameRankFound = true;
	                }
	            }
	        }
	        default -> {
	        	//king and pawn never need disambiguation
	        }
	    }

	    if (!anyOtherFound) return "";
	    if (!sameFileFound) return String.valueOf((char) ('a' + colFrom));
	    if (!sameRankFound) return String.valueOf((char) ('1' + rowFrom));
	    return "" + (char) ('a' + colFrom) + (char) ('1' + rowFrom);
	}

	/**
	 * Returns the check ("+") or checkmate ("#") suffix that should be appended to the
	 * algebraic notation of the last move, based on the current game {@link #result}
	 * and whether the opponent's king is currently in check.
	 *
	 * @return "#" if the game just ended in checkmate, "+" if the opponent is in check,
	 *         or an empty string otherwise
	 */
	private String getCheckOrMateSuffix() {
	    if ("1-0".equals(this.result) || "0-1".equals(this.result)) {
	        return "#";
	    }
	    if (this.isKingInCheck(!this.whiteToMove)) {
	        return "+";
	    }
	    return "";
	}
	
	
}