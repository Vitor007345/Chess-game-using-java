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
    
    private boolean whiteToMove;
    
    //constructors
    
    //package private
	ChessBoard(Piece[][] board, ArrayList<Rook> whiteRooks, ArrayList<Rook> blackRooks,
			ArrayList<Pawn> whitePawns, ArrayList<Pawn> blackPawns, ArrayList<Knight> whiteKnights,
			ArrayList<Knight> blackKnights, ArrayList<Bishop> whiteBishops, ArrayList<Bishop> blackBishops,
			ArrayList<Queen> whiteQueens, ArrayList<Queen> blackQueens, King whiteKing, King blackKing,
			ArrayList<Move> moves, boolean whiteToMove) {
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
				//move = this.pawnGoingForwardMove();
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
		
		
		if(move != null)this.moves.add(move);
		this.whiteToMove = !this.whiteToMove;
	}
	
	public void undoMove() {
		this.undoMove(this.moves.getLast());
		this.moves.removeLast();
	
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
				}
				case Queen q -> {
					(q.isWhite()? this.whiteQueens : this.blackQueens).add(q);
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
    			return null;
    	}
    }
	
	private Pawn undoPromotion(Piece piece) {
		switch(piece) {
		case Queen q ->{
			return ((piece.isWhite()? this.whiteQueens : this.blackQueens).remove(q))? new Pawn(q.getPieceInfo()) : null;
		}
		case Rook r ->{
			return ((piece.isWhite()? this.whiteRooks : this.blackRooks).remove(r))? new Pawn(r.getPieceInfo()) : null;
		}
		case Bishop b ->{
			return ((piece.isWhite()? this.whiteBishops : this.blackBishops).remove(b))? new Pawn(b.getPieceInfo()) : null;
		}
		case Knight n ->{
			return ((piece.isWhite()? this.whiteKnights : this.blackKnights).remove(n))? new Pawn(n.getPieceInfo()) : null;
		}
		default ->{
			return null;
		}
	}
	}
	
	
	private Move pawnGoingForwardMove(byte rowTo, byte colTo, boolean promotion, char promotionPiece, boolean checkOrCheckmate, String moveStr) {
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
			Move move = this.executePawnForwardMove(pawn, possibleRowFrom, rowTo, colTo, promotion, promotionPiece);
			if(this.isKingInCheck()) {
				this.undoMove(move);
				throw new MoveNotationError(moveStr, "can't move this pawn because your king is in danger");
			}
			return move;
			
		}else if(possiblePawn == null){
			possibleRowFrom +=  (this.whiteToMove?-1:1);
			possiblePawn = this.board[possibleRowFrom][colTo];
			if(possiblePawn instanceof Pawn pawn) {
				if(!possiblePawn.hasMoved()) {
					if(pawn.isWhite() != this.whiteToMove) throw new MoveNotationError(moveStr, "can't move a piece that's not yours");
					
					Move move = this.executePawnForwardMove(pawn, possibleRowFrom, rowTo, colTo, promotion, promotionPiece);
					if(this.isKingInCheck()) {
						this.undoMove(move);
						throw new MoveNotationError(moveStr, "can't move this pawn because your king is in danger");
					}
					return move;
				}else {
					throw new MoveNotationError(moveStr, "its not the fist move of the pawn 2 square behind");
				}
			}else {
				throw new MoveNotationError(moveStr, "its impossible to a pawn move to this square");
			}
		}else {
			throw new MoveNotationError(moveStr, "there is a piece blocking");
		}
	}
	
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
	
	
	private boolean isDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2, boolean anti) {
		if(piece1.getRow() > piece2.getRow()) {
			Piece temp = piece1;
			piece1 = piece2;
			piece2 = temp;
		}
		int n = 1;
		if(anti) n = -1;
		for(int i = piece1.getRow() + 1, j = piece1.getCol() + n; i < piece2.getRow(); i++, j+=n) {
			if(this.board[i][j] != null)return false;
		}
		return true;
	}
	
	private boolean isDiagonalEmptyBetween2Pieces(Piece piece1, Piece piece2) {
		return this.isDiagonalEmptyBetween2Pieces(piece1, piece2, false);
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




/*
private void seeIfPawnForwardMoveDontPutKingInDanger(Piece pawn, String moveStr) {
	King king = this.whiteToMove? this.whiteKing : this.blackKing;
	if(pawn.getRow() == king.getRow()) {
		ArrayList<Rook> enemyRooks = this.whiteToMove? this.blackRooks : this.whiteRooks;
		ArrayList<Queen> enemyQueens = this.whiteToMove? this.blackQueens : this.whiteQueens;
		
		for(Rook r : enemyRooks) {
			if(r.getRow() == pawn.getRow() &&
				this.isRowEmptyBetween2col(pawn.getRow(), pawn.getCol(), r.getCol()) 
				&& this.isRowEmptyBetween2col(pawn.getRow(), pawn.getCol(), king.getCol()))
			{
				throw new MoveNotationError(moveStr, "Can't move pawn bescause it opens up an enemy's rook vision to your king");
			}
		}
		
		for(Queen q : enemyQueens) {
			if(q.getRow() == pawn.getRow() &&
				this.isRowEmptyBetween2col(pawn.getRow(), pawn.getCol(), q.getCol()) 
				&& this.isRowEmptyBetween2col(pawn.getRow(), pawn.getCol(), king.getCol()))
			{
				throw new MoveNotationError(moveStr, "Can't move pawn bescause it opens up an enemy's queen vision to your king");
			}
		}
	}else if(checkIfIsSameDiagonal(pawn, king) ) {
		ArrayList<Bishop> enemyBishops = this.whiteToMove? this.blackBishops : this.whiteBishops;
		ArrayList<Queen> enemyQueens = this.whiteToMove? this.blackQueens : this.whiteQueens;
		for(Bishop b : enemyBishops) {
			if(checkIfIsSameDiagonal(b, pawn) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, b, false) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, king, false))
			{
				throw new MoveNotationError(moveStr, "Can't move pawn bescause it opens up an enemy's bishop vision to your king");
			}
		}
		
		for(Queen q : enemyQueens) {
			if(checkIfIsSameDiagonal(q, pawn) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, q, false) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, king, false))
			{
				throw new MoveNotationError(moveStr, "Can't move pawn bescause it opens up an enemy's queen vision to your king");
			}
		}
		
		
		
		
	}else if(checkIfIsSameAntiDiagonal(pawn, king)) {
		ArrayList<Bishop> enemyBishops = this.whiteToMove? this.blackBishops : this.whiteBishops;
		ArrayList<Queen> enemyQueens = this.whiteToMove? this.blackQueens : this.whiteQueens;
		for(Bishop b : enemyBishops) {
			if(checkIfIsSameAntiDiagonal(b, pawn) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, b, true) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, king, true))
			{
				throw new MoveNotationError(moveStr, "Can't move pawn bescause it opens up an enemy's bishop vision to your king");
			}
		}
		
		for(Queen q : enemyQueens) {
			if(checkIfIsSameAntiDiagonal(q, pawn) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, q, true) &&
				this.isDiagonalEmptyBetween2Pieces(pawn, king, true))
			{
				throw new MoveNotationError(moveStr, "Can't move pawn bescause it opens up an enemy's queen vision to your king");
			}
		}
		
	}
}
*/
