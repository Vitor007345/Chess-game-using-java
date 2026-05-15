package chessgame.moves;

import chessgame.pieces.Pawn;
import chessgame.pieces.Piece;

public class Move {
	
	private final byte movedPieceOldInfo;
	
	private final Piece movedPiece;
	private final Piece capturedPiece;
	
	private final boolean promotion;
	
	private final boolean castle;
	private final boolean shortCastle;
	
	private boolean deleted;
	
	
	
	private Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion, boolean castle, boolean shortCastle){
		this.movedPieceOldInfo = movedPieceOldInfo;
		
		this.movedPiece = moved;
		this.capturedPiece = captured;
		
		this.promotion = promotion;
		
		this.castle = castle;
		this.shortCastle = shortCastle;
		
		this.deleted = false;
	}
	
	
	public Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion){
		this(movedPieceOldInfo, moved, captured, promotion, false, false);
		
	}
	
	public Move(boolean castle, boolean shortCastle, byte movedPieceOldInfo, Piece moved) {
		this(movedPieceOldInfo, moved, null, false, castle, shortCastle);
		
	}
	
	
	public Move(byte movedPieceOldInfo, Piece moved, Piece captured) {
		this(movedPieceOldInfo, moved, captured, false);
	}
	
	public Move(byte movedPieceOldInfo, Piece moved, boolean promotion) {
		this(movedPieceOldInfo, moved, null, promotion);
	}
	
	public Move(byte movedPieceOldInfo, Piece moved) {
		this(movedPieceOldInfo, moved,  null);
	}

	public boolean isPromotion() {
		return promotion;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void delete() {
		this.deleted = true;
	}
	
	public boolean isCastle() {
		return this.castle;
	}
	
	public boolean isShortCastle() {
		return this.shortCastle;
	}
	
	public byte getMovedPieceOldInfo() {
		return movedPieceOldInfo;
	}

	public Piece getMovedPiece() {
		return movedPiece;
	}


	public Piece getCapturedPiece() {
		return capturedPiece;
	}
	
	//only works if this is the lastMove
	public boolean isPawnDoubleFowardMove() {
		Pawn oldPawn = new Pawn(this.movedPieceOldInfo);
		return this.movedPiece instanceof Pawn p && oldPawn.getCol() == p.getCol() && (p.getRow() == oldPawn.getRow() + (p.isWhite()?2:-2));
	}
	
	
	
	
}
