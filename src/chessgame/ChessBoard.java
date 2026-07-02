package chessgame;

import chessgame.errors.MoveNotationException;
import chessgame.moves.Move;
import chessgame.pieces.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChessBoard {
	//atributes
	private Piece[][] board; //[row][col]
	
	private ArrayList<Rook> whiteRooks;
    private ArrayList<Rook> blackRooks;

    private ArrayList<Knight> whiteKnights;
    private ArrayList<Knight> blackKnights;

    private ArrayList<Bishop> whiteBishops;
    private ArrayList<Bishop> blackBishops;

    private ArrayList<Queen> whiteQueens;
    private ArrayList<Queen> blackQueens;
    
    private ArrayList<Pawn> whitePawns;
    private ArrayList<Pawn> blackPawns;

    private King whiteKing;
    private King blackKing;
    
    private ArrayList<Move> moves; 
    
    private boolean whiteToMove;
    
    private String result;
    
    private int halfmoveClock;
    private int fullmoveNumber;
    private ArrayList<Integer> halfmoveResetHistory;
    
    private HashMap<String, Integer> positionHistory;
    
    //constructors
    
    //package private
	ChessBoard(Piece[][] board, ArrayList<Rook> whiteRooks, ArrayList<Rook> blackRooks,
			ArrayList<Pawn> whitePawns, ArrayList<Pawn> blackPawns, ArrayList<Knight> whiteKnights,
			ArrayList<Knight> blackKnights, ArrayList<Bishop> whiteBishops, ArrayList<Bishop> blackBishops,
			ArrayList<Queen> whiteQueens, ArrayList<Queen> blackQueens, King whiteKing, King blackKing,
			ArrayList<Move> moves, boolean whiteToMove, String result,
			int halfmoveClock, int fullmoveNumber, ArrayList<Integer> halfmoveResetHistory, HashMap<String, Integer> positionHistory) {
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
	}
	
	
	public String getResult() {
		return this.result;
	}
	public boolean isWhiteToMove() {
		return this.whiteToMove;
	}
	public Piece getPiece(int row, int col) {
		return this.board[row][col];
	}
	public int getHalfmoveClock() {
		return this.halfmoveClock;
	}
	public int getFullmoveNumber() {
		return this.fullmoveNumber;
	}
	
	
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
		}
		
	}
	
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
	    
	}
	
	public boolean undoMove() {
		if(this.moves.isEmpty()) {
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
			
			if (!this.whiteToMove) { 
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
		
		
		
		this.board[move.getMovedPiece().getRow()][move.getMovedPiece().getCol()] = null;
		move.getMovedPiece().setInfo(move.getMovedPieceOldInfo());
		
		if(move.isPromotion()) {
			Pawn pawn = this.undoPromotion(move.getMovedPiece());
			if(pawn == null)throw new AssertionError("Program error: can't generate old pawn when undoing promotion");
			this.board[move.getMovedPiece().getRow()][move.getMovedPiece().getCol()] = pawn;
			
		}else {
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
	private static byte convertCol(char colChar, String moveStr) throws MoveNotationException{
		int col = (int)(colChar - 'a');
		if(col < 0 || col > 7)throw new MoveNotationException(moveStr, "invalid col");
		return (byte)col;
	}
		
	private static byte convertRow(char rowChar, String moveStr) throws MoveNotationException{
		int row = (int)(rowChar - '1');
		if(row < 0 || row > 7)throw new MoveNotationException(moveStr, "invalid row");
		return (byte)row;
	}
	
	//castle movement
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
	private Move executePawnForwardMove(Pawn pawn, byte fromR, byte toR, int col, boolean promotion, char promotionPiece) {
		byte oldPawnInfo = pawn.getPieceInfo();
	    pawn.setRow(toR);
	    this.board[fromR][col] = null;
	    Piece movedPiece = promotion? this.promotePawn(pawn, promotionPiece) : pawn;
	    this.board[toR][col] = movedPiece;
	    pawn.setMoved(true);
	    
	    return new Move(oldPawnInfo, movedPiece, promotion);
	    
	    
	}
	
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
	
	private Pawn undoPromotion(Piece piece) {
		Pawn pawn = new Pawn(piece.getPieceInfo());
		if(!((pawn.isWhite()? this.whitePawns : this.blackPawns).add(pawn))) {
			return null;
		}
		return this.removePieceFromArrays(piece)?  pawn : null;
	}
	
	
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
	    
	    return new Move(oldPawnInfo, movedPiece, pieceCaptured, promotion);
	}
	
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
	private boolean knightCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return Math.abs(rowFrom - rowTo) * Math.abs(colFrom - colTo) == 2;
	}
	private boolean knightCanMove(byte rowTo, byte colTo, Knight n) {
		return this.knightCanMove(rowTo, colTo, n.getRow(), n.getCol());
	}
	
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Knight n)-> 
			knightCanMove(rowTo, colTo, n), this.whiteToMove?this.whiteKnights:this.blackKnights, Knight.class);
	}
	
	
	
	
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom ,isRow, (Knight n)-> 
			knightCanMove(rowTo, colTo, n), this.whiteToMove?this.whiteKnights:this.blackKnights, Knight.class);
	}
	
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, knightCanMove(rowTo, colTo, rowFrom, colFrom), Knight.class);
		
	}
	
	//bishop movement
	
	private boolean bishopCanMove(byte rowTo, byte colTo, Bishop b) {
		return this.bishopCanMove(rowTo, colTo, b.getRow(), b.getCol());
	}
	private boolean bishopCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return (checkIfIsSameDiagonal(rowTo, colTo, rowFrom, colFrom) 
				&& this.isDiagonalEmptyBetween2Pieces(rowTo, colTo, rowFrom, colFrom)) ||
				(checkIfIsSameAntiDiagonal(rowTo, colTo, rowFrom, colFrom) 
				&& this.isAntiDiagonalEmptyBetween2Pieces(rowTo, colTo, rowFrom, colFrom));
	}
	
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Bishop b)-> 
			this.bishopCanMove(rowTo, colTo, b)
		,(this.whiteToMove?this.whiteBishops:this.blackBishops), Bishop.class);
	}
	
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Bishop b)-> 
			this.bishopCanMove(rowTo, colTo, b)
		,(this.whiteToMove?this.whiteBishops:this.blackBishops), Bishop.class);
	}
	
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			bishopCanMove(rowTo, colTo, rowFrom, colFrom)
		, Bishop.class);
	}
	
	
	//Rook movement
	private boolean rookCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom){
		return (rowTo == rowFrom && this.isRowEmptyBetween2Col(rowTo, colFrom, colTo)) ||
				(colTo == colFrom && this.isColEmptyBetween2Row(colTo, rowFrom, rowTo)); 
	}
	private boolean rookCanMove(byte rowTo, byte colTo, Rook r) {
		return this.rookCanMove(rowTo, colTo, r.getRow(), r.getCol());
	}
	
	private Move rookMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Rook r)-> 
			this.rookCanMove(rowTo, colTo, r)
		,(this.whiteToMove?this.whiteRooks:this.blackRooks), Rook.class);
	}
	
	private Move rookMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Rook r)-> 
			this.rookCanMove(rowTo, colTo, r)
		,(this.whiteToMove?this.whiteRooks:this.blackRooks), Rook.class);
	}
	
	private Move rookMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			rookCanMove(rowTo, colTo, rowFrom, colFrom)
		, Rook.class);
	}
	
	//Queen movement
	private boolean queenCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return this.rookCanMove(rowTo, colTo, rowFrom, colFrom) || this.bishopCanMove(rowTo, colTo, rowFrom, colFrom);
	}
	private boolean queenCanMove(byte rowTo, byte colTo, Queen q) {
		return this.queenCanMove(rowTo, colTo, q.getRow(), q.getCol());
	}
	
	private Move queenMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Queen q)-> 
			this.queenCanMove(rowTo, colTo, q)
		,(this.whiteToMove?this.whiteQueens:this.blackQueens), Queen.class);
	}
	
	private Move queenMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Queen q)-> 
			this.queenCanMove(rowTo, colTo, q)
		,(this.whiteToMove?this.whiteQueens:this.blackQueens), Queen.class);
	}
	
	private Move queenMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			queenCanMove(rowTo, colTo, rowFrom, colFrom)
		, Queen.class);
	}
	
	//King movement
	private boolean kingCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return Math.abs(rowTo - rowFrom) <= 1 && Math.abs(colTo - colFrom) <= 1;
	}
	private boolean kingCanMove(byte rowTo, byte colTo, King k) {
		return this.kingCanMove(rowTo, colTo, k.getRow(), k.getCol());
	}
	
	private Move kingMove(byte rowTo, byte colTo, boolean capture, String moveStr) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing : this.blackKing;
		if(!this.kingCanMove(rowTo, colTo, king)) {
			throw new MoveNotationException(moveStr, "its impossible to to the King move to this position");
		}
		Move move = this.executeMove(king, rowTo, colTo, this.getCapturedPiece(rowTo, colTo, capture, moveStr));
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
		
	}
	private Move kingMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing : this.blackKing;
		if((isRow?king.getRow():king.getCol()) != rowOrColFrom) {
			throw new MoveNotationException(moveStr, "king is not at this position");
		}
		return this.kingMove(rowTo, colTo, capture, moveStr);
	}
	
	private Move kingMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) throws MoveNotationException{
		King king = this.whiteToMove? this.whiteKing : this.blackKing;
		if(king.getRow() != rowFrom || king.getCol() != colFrom) {
			throw new MoveNotationException(moveStr, "king is not at this position");
		}
		return this.kingMove(rowTo, colTo, capture, moveStr);
	}
	
	
	
	//general movements functions
	
	private interface Condition<P extends Piece>{
		boolean isTrue(P piece);
	}
	
	//interface DelayedContition used to only execute  slow boolean functions later prioritizing faster operations that can fail before this function get executed 
	private interface DelayedCondition{
		boolean isTrue();
	}
	
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, Condition<P> cond, ArrayList<P> pieces, Class<P> classPiece) throws MoveNotationException{
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		P pieceToMove = searchPieceToMove(pieces, cond, moveStr, classPiece);
		Move move = this.executeMove(pieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow, Condition<P> cond, ArrayList<P> pieces, Class<P> classPiece) throws MoveNotationException {
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		P pieceToMove = searchPieceToMove(pieces, (P p)->((isRow? p.getRow(): p.getCol()) == rowOrColFrom) && cond.isTrue(p), moveStr, classPiece);
		Move move = this.executeMove(pieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	
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
	
	private Move executeMove(Piece pieceToMove, byte rowTo, byte colTo, Piece captured) {
		if(captured != null && !this.removePieceFromArrays(captured))throw new AssertionError("Error when removing captured piece");
		byte pieceToMoveOldInfo = pieceToMove.getPieceInfo();
		this.board[pieceToMove.getRow()][pieceToMove.getCol()] = null;
		pieceToMove.setPos(rowTo, colTo);
		this.board[pieceToMove.getRow()][pieceToMove.getCol()] = pieceToMove;
		pieceToMove.setMoved(true);
		
		return new Move(pieceToMoveOldInfo, pieceToMove, captured);
	}
	
	
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
	private boolean isKingInCheck() {
		return this.isKingInCheck(this.whiteToMove);
	}
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
	private void checkIfKingIsInCheckAfterMove(Move move, String moveStr) throws MoveNotationException{
		if(this.isKingInCheck()) {
			this.undoMove(move);
			throw new MoveNotationException(moveStr, "can't move this piece because your king is or will be in danger");
		}
	}
	
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
	
	private void resolveStateOfGameAfterMove() {
		
		boolean isCheck = this.isKingInCheck(!this.whiteToMove);
	    boolean hasMoves = this.haslegalMove(!this.whiteToMove);
		
	    if (isCheck && !hasMoves) {
	        this.result = this.whiteToMove ? "1-0" : "0-1"; 
	        
	    } else if ((!isCheck && !hasMoves) || this.halfmoveClock >= 100) {
	    	this.result = "1/2-1/2";
	    }
	}
	
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
	
	private boolean rookHasLegalMoves(Rook r) {
		return rayCasting(r, new int[] {1,-1, 0, 0}, new int[]{0, 0, 1, -1});
	}
	
	private boolean bishopHasLegalMoves(Bishop b) {
		return rayCasting(b, new int[] {1,-1, 1, -1}, new int[]{1, -1, -1, 1});
	}
	
	private boolean queenHasLegalMoves(Queen q) {
		return rayCasting(q, new int[] {1,-1, 0, 0, 1,-1, 1, -1}, new int[]{0, 0, 1, -1, 1, -1, -1, 1});
	}
	
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
	private static boolean checkIfIsSameDiagonal(byte row1, byte col1, byte row2, byte col2) {
		return (row1 - col1) == (row2 - col2);
	}
	private static boolean checkIfIsSameDiagonal(Piece piece1, Piece piece2) {
		return checkIfIsSameDiagonal(piece1.getRow(), piece1.getCol(), piece2.getRow(), piece2.getCol());
	}
	
	private static boolean checkIfIsSameAntiDiagonal(byte row1, byte col1, byte row2, byte col2) {
		return (row1 + col1) == (row2 + col2);
	}
	private static boolean checkIfIsSameAntiDiagonal(Piece piece1, Piece piece2) {
		return checkIfIsSameAntiDiagonal(piece1.getRow(), piece1.getCol(), piece2.getRow(), piece2.getCol());
	}
	
	
    
    
    //util functions
	
	
	
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
	
	private boolean isRowEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isRowEmptyBetween2Col(piece1.getRow(), piece1.getCol(), piece2.getCol());
	}
	
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
	
	private boolean isColEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isColEmptyBetween2Row(piece1.getCol(), piece1.getRow(), piece2.getRow());
	}
	
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
	
	
	private boolean isDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2, boolean anti) {
		return this.isDiagonalEmptyBetween2Pieces(piece1.getRow(), piece1.getCol(), piece2.getRow(), piece2.getCol(), anti);
	}
	
	
	private boolean isDiagonalEmptyBetween2Pieces(byte row1, byte col1, byte row2, byte col2) {
		return this.isDiagonalEmptyBetween2Pieces(row1, col1, row2, col2, false);
	}
	
	private boolean isDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isDiagonalEmptyBetween2Pieces(piece1, piece2, false);
	}
	
	private boolean isAntiDiagonalEmptyBetween2Pieces(byte row1, byte col1, byte row2, byte col2) {
		return this.isDiagonalEmptyBetween2Pieces(row1, col1, row2, col2, true);
	}
	private boolean isAntiDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isDiagonalEmptyBetween2Pieces(piece1, piece2, true);
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
	private String getCutFEN() {
		return this.getCutFENStrBuilder().toString();
	}
	
	//FEN: Forsyth-Edwards Notation
	public String getFEN() {
        StringBuilder fen = this.getCutFENStrBuilder();

        // 5. Halfmove Clock
        fen.append(" ").append(this.halfmoveClock);

        // 6. Fullmove Number
        fen.append(" ").append(this.fullmoveNumber);

        return fen.toString();
    }
	
	
}




