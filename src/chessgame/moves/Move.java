package chessgame.moves;

import chessgame.pieces.Pawn;
import chessgame.pieces.Piece;

public class Move {
	
	private final byte movedPieceOldInfo;
	
	private final Piece movedPiece;
	private final Piece capturedPiece;
	
	private boolean promotion;
	
	private boolean deleted;
	
	
	
	public Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion){
		this.movedPieceOldInfo = movedPieceOldInfo;
		
		this.movedPiece = moved;
		this.capturedPiece = captured;
		
		this.promotion = promotion;
		this.deleted = false;
		
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
