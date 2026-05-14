package chessgame;

import chessgame.moves.MoveNotationError;
import chessgame.moves.Move;
import chessgame.pieces.*;
import java.util.ArrayList;
import java.util.Arrays;

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
    
    //constructors
    
    //package private
	ChessBoard(Piece[][] board, ArrayList<Rook> whiteRooks, ArrayList<Rook> blackRooks,
			ArrayList<Pawn> whitePawns, ArrayList<Pawn> blackPawns, ArrayList<Knight> whiteKnights,
			ArrayList<Knight> blackKnights, ArrayList<Bishop> whiteBishops, ArrayList<Bishop> blackBishops,
			ArrayList<Queen> whiteQueens, ArrayList<Queen> blackQueens, King whiteKing, King blackKing,
			ArrayList<Move> moves, boolean whiteToMove, String result) {
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
	}
	
	
	
	public void move(String moveStr) {
		Move move = null;
		
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
								//Rook logic here
								break;
							case 'Q':
								//Queen logic here
								break;
							default:
								throw new MoveNotationError(moveStr, "there io no piece with this letter");
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
								//Rook logic here
								break;
							case 'Q':
								//Queen logic here
								break;
							default:
								throw new MoveNotationError(moveStr, "there io no piece with this letter");
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
									//Rook logic here
									break;
								case 'Q':
									//Queen logic here
									break;
								default:
									throw new MoveNotationError(moveStr, "there io no piece with this letter");
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
								//Rook logic here
								break;
							case 'Q':
								//Queen logic here
								break;
							default:
								throw new MoveNotationError(moveStr, "there io no piece with this letter");
						}
						}else {
							throw new MoveNotationError(moveStr, colOrRowChar + "is not a file or a rank in chess");
						}
					}
				}else {
					throw new MoveNotationError(moveStr, "move string is too long");
				}
			}		
		}
		
		
		
		if(move != null) {
			this.resolveStateOfGameAfterMove(move, checkOrCheckmate, moveStr);
			this.moves.add(move);
			this.whiteToMove = !this.whiteToMove;
		}
		
	}
	
	public void undoMove() {
		this.undoMove(this.moves.getLast());
		this.moves.removeLast();
		this.whiteToMove = !this.whiteToMove;
	
	}
	
	private void undoMove(Move move) {
		if(move.isDeleted())throw new IllegalArgumentException("This move is aready deleted");
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
	
	
	private Move pawnGoingForwardMove(byte rowTo, byte colTo, boolean promotion, char promotionPiece, String moveStr) {
		Move move = null;
		if(this.board[rowTo][colTo] != null) throw new MoveNotationError(moveStr, "position occupied");
		if((this.whiteToMove && rowTo <=1) || (!this.whiteToMove && rowTo >=6)) {
			throw new MoveNotationError(moveStr, "its impossible to a pawn move to this square");
		}
		if(promotion) {
			if((this.whiteToMove && rowTo != 7) || (!this.whiteToMove && rowTo != 0)) {
				throw new MoveNotationError(moveStr, "Can't promote without beeing in the top of the board");
			}
		}else if((this.whiteToMove && rowTo == 7) || (!this.whiteToMove && rowTo == 0)){
			throw new MoveNotationError(moveStr, "Can't move pawn there without promote");
		}
		
		byte possibleRowFrom = (byte)(rowTo + (this.whiteToMove?-1:1));
		Piece possiblePawn =  this.board[possibleRowFrom][colTo]; 
		if(possiblePawn instanceof Pawn pawn) {
			if(pawn.isWhite() != this.whiteToMove) throw new MoveNotationError(moveStr, "can't move a piece that's not yours");
			move = this.executePawnForwardMove(pawn, possibleRowFrom, rowTo, colTo, promotion, promotionPiece);
			
			
		}else if(possiblePawn == null){
			possibleRowFrom +=  (this.whiteToMove?-1:1);
			possiblePawn = this.board[possibleRowFrom][colTo];
			if(possiblePawn instanceof Pawn pawn) {
				if(!possiblePawn.hasMoved()) {
					if(pawn.isWhite() != this.whiteToMove) throw new MoveNotationError(moveStr, "can't move a piece that's not yours");
					
					move =  this.executePawnForwardMove(pawn, possibleRowFrom, rowTo, colTo, promotion, promotionPiece);
					
				}else {
					throw new MoveNotationError(moveStr, "its not the fist move of the pawn 2 square behind");
				}
			}else {
				throw new MoveNotationError(moveStr, "its impossible to a pawn move to this square");
			}
		}else {
			throw new MoveNotationError(moveStr, "there is a piece blocking");
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
	
	private Move pawnCaptureMove(byte rowTo, byte colTo, byte colFrom, boolean promotion, char promotionPiece, String moveStr) {
		
		
		if((this.whiteToMove && rowTo <=1) || (!this.whiteToMove && rowTo >=6)) {
			throw new MoveNotationError(moveStr, "its impossible to a pawn move to this square");
		}
		if(promotion) {
			if((this.whiteToMove && rowTo != 7) || (!this.whiteToMove && rowTo != 0)) {
				throw new MoveNotationError(moveStr, "Can't promote without beeing in the top of the board");
			}
		}else if((this.whiteToMove && rowTo == 7) || (!this.whiteToMove && rowTo == 0)){
			throw new MoveNotationError(moveStr, "Can't move pawn there without promote");
		}
		
		int direction = colTo - colFrom;
		if(Math.abs(direction) != 1)throw new MoveNotationError(moveStr, "can´t capture in a file that is not at next to the file the pawn is");
		
		int num = (this.whiteToMove?-1:1);
		
		Piece pawnToMove = this.board[rowTo + num][colFrom];
		if(!(pawnToMove instanceof Pawn p) || (p.isWhite() != this.whiteToMove)) {
			throw new MoveNotationError(moveStr, "There is no pawn in this file for you to move to that square");
		}
		
		Piece pieceCaptured = this.board[rowTo][colTo];
		
		if(pieceCaptured == null) {
			pieceCaptured = this.board[rowTo + num][colTo];
			if(pieceCaptured == null) throw new MoveNotationError(moveStr, "there are no pieces to capture cant move pawn diagonaly without capture");
			if(!(pieceCaptured instanceof Pawn)) throw new MoveNotationError(moveStr, "can't do en passant in a piece that is not a pawn");
			Move lastMove = this.moves.getLast();
			if(lastMove.getMovedPiece() != pieceCaptured || !lastMove.isPawnDoubleFowardMove()) {
				throw new MoveNotationError(moveStr, "can't do an en passant when the last move was not a pawn moving 2 squares foward");
			}
			
		}
		if(pieceCaptured.isWhite() == this.whiteToMove) throw new MoveNotationError(moveStr, "You cant capture your piece");
		
		
		
		Move move = this.executePawnCaptureMove((Pawn)pawnToMove, pieceCaptured, rowTo, colTo, promotion, promotionPiece);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		
		return move;
	}
	
	
	//knight movement
	private boolean knightCanMove(byte rowTo, byte colTo, byte rowFrom, byte colFrom) {
		return Math.abs(rowFrom - rowTo) * Math.abs(colFrom - colTo) == 2;
	}
	private boolean knightCanMove(byte rowTo, byte colTo, Knight n) {
		return this.knightCanMove(rowTo, colTo, n.getRow(), n.getCol());
	}
	
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr) {
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Knight n)-> 
			knightCanMove(rowTo, colTo, n), this.whiteToMove?this.whiteKnights:this.blackKnights, Knight.class);
	}
	
	
	
	
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow) {
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom ,isRow, (Knight n)-> 
			knightCanMove(rowTo, colTo, n), this.whiteToMove?this.whiteKnights:this.blackKnights, Knight.class);
	}
	
	private Move knightMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) {
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
	
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr){
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, (Bishop b)-> 
			this.bishopCanMove(rowTo, colTo, b)
		,(this.whiteToMove?this.whiteBishops:this.blackBishops), Bishop.class);
	}
	
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow){
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowOrColFrom, isRow, (Bishop b)-> 
			this.bishopCanMove(rowTo, colTo, b)
		,(this.whiteToMove?this.whiteBishops:this.blackBishops), Bishop.class);
	}
	
	private Move bishopMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom) {
		return this.anyPieceMove(rowTo, colTo, capture, moveStr, rowFrom, colFrom, ()->
			bishopCanMove(rowTo, colTo, rowFrom, colFrom)
		, Bishop.class);
	}
	
	
	
	
	
	//general movements functions
	
	private interface Condition<P extends Piece>{
		boolean isTrue(P piece);
	}
	
	//interface DelayedContition used to only execute  slow boolean functions later prioritizing faster operations that can fail before this function get executed 
	private interface DelayedCondition{
		boolean isTrue();
	}
	
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, Condition<P> cond, ArrayList<P> pieces, Class<P> classPiece) {
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		P pieceToMove = searchPieceToMove(pieces, cond, moveStr, classPiece);
		Move move = this.executeMove(pieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowOrColFrom, boolean isRow, Condition<P> cond, ArrayList<P> pieces, Class<P> classPiece) {
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		P pieceToMove = searchPieceToMove(pieces, (P p)->((isRow? p.getRow(): p.getCol()) == rowOrColFrom) && cond.isTrue(p), moveStr, classPiece);
		Move move = this.executeMove(pieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom, DelayedCondition cond, Class<P> classPiece) {
		
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		
		Piece possiblePieceToMove = this.board[rowFrom][colFrom];
		if(!(classPiece.isInstance(possiblePieceToMove))) throw new MoveNotationError(moveStr, "there is no" + classPiece.getSimpleName() + " in this square");
		
		if(!cond.isTrue()){
			throw new MoveNotationError(moveStr, "its impossible to a " + classPiece.getSimpleName() + " move beetween those 2 position");
		}
		
		Move move = this.executeMove(possiblePieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	private <P extends Piece> Move anyPieceMove(byte rowTo, byte colTo, boolean capture, String moveStr, byte rowFrom, byte colFrom, boolean fastCond, Class<P> classPiece) {
		if(!fastCond){
			throw new MoveNotationError(moveStr, "its impossible to a " + classPiece.getSimpleName() + " move beetween those 2 position");
		}
		
		Piece pieceCaptured = this.getCapturedPiece(rowTo, colTo, capture, moveStr);
		
		Piece possiblePieceToMove = this.board[rowFrom][colFrom];
		if(!(classPiece.isInstance(possiblePieceToMove))) throw new MoveNotationError(moveStr, "there is no" + classPiece.getSimpleName() + " in this square");
		
		
		
		Move move = this.executeMove(possiblePieceToMove, rowTo, colTo, pieceCaptured);
		this.checkIfKingIsInCheckAfterMove(move, moveStr);
		return move;
	}
	
	
	private static <P extends Piece> P searchPieceToMove(ArrayList<P> pieces, Condition<P> cond, String moveStr, Class<P> classPiece) {
		P pieceToMove = null;
		int numOfPiecesOfThisTypeThatCanMove = 0;
		for(P p : pieces) {
			if(cond.isTrue(p)) {
				pieceToMove = p;
				numOfPiecesOfThisTypeThatCanMove++;
			}
		}
		if(pieceToMove == null) 
			throw new MoveNotationError(moveStr, "there is no " + classPiece.getSimpleName()  + " that can move to this square");
		if (numOfPiecesOfThisTypeThatCanMove > 1) 
			throw new MoveNotationError(moveStr, "there is more than one " + classPiece.getSimpleName() + " that can move to this square");
		
		return pieceToMove;
		
	}
	
	
	private Piece getCapturedPiece(byte rowTo, byte colTo, boolean capture, String moveStr) {
		Piece pieceCaptured = this.board[rowTo][colTo];
		if(pieceCaptured == null) {
			if(capture) {
				throw new MoveNotationError(moveStr, "You are not capturing, the position this piece is going has no pieces to capture");
			}	
		}else {
			if(!capture) {
				throw new MoveNotationError(moveStr, "The position you want to go is occupied, if you want to capture you forgot to put the x symbol");
			}
			if(pieceCaptured.isWhite() == this.whiteToMove) {
				throw new MoveNotationError(moveStr, "The square you want to go alredy has one of your pieces");
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
	private void checkIfKingIsInCheckAfterMove(Move move, String moveStr) {
		if(this.isKingInCheck()) {
			this.undoMove(move);
			throw new MoveNotationError(moveStr, "can't move this piece because your king is or will be in danger");
		}
	}
	
	private void resolveStateOfGameAfterMove(Move move, boolean checkOrCheckmate, String moveStr) {
		if(this.isKingInCheck(!this.whiteToMove)) {
			if(this.haslegalMove(!this.whiteToMove)) {
				if(checkOrCheckmate) {
					if(moveStr.charAt(moveStr.length() - 1) != '+') {
						this.undoMove(move);
						throw new MoveNotationError(moveStr, "This is not a checkmate, just a check, you should use the + symbol");
					}
				}else {
					this.undoMove(move);
					throw new MoveNotationError(moveStr, "You forgot to put the +(check simbol) in the end of your move");
				}
			}else {
				if(checkOrCheckmate) {
					if(moveStr.charAt(moveStr.length() - 1) != '#') {
						this.undoMove(move);
						throw new MoveNotationError(moveStr, "This is not a check, its a checkmate, rewrite your move with the # symbol and you win");
					}else {
						this.result = this.whiteToMove? "1-0":"0-1";
					}
				}else {
					this.undoMove(move);
					throw new MoveNotationError(moveStr, "You forgot to put the #(checkmate simbol) in the end of your move");
				}
			}
			
		}else {
			if(checkOrCheckmate) {
				this.undoMove(move);
				throw new MoveNotationError(moveStr, "This is not a check neither a checkmate");
			}
			if(!this.haslegalMove(!this.whiteToMove)) {
				this.result = "1/2-1/2";
			}
		}
	}
	
	private boolean haslegalMove(boolean white) {
		//implementar dps q tiver movimento de todas as peças
		return true;
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
	
	
	
	
	
}




