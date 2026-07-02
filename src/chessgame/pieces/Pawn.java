package chessgame.pieces;

public class Pawn extends Piece {
    public Pawn(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    public Pawn(int row, int col, boolean isWhite) {
	    super(row, col, isWhite);
	}

    public Pawn(byte pieceInfo) {
        super(pieceInfo);
    }
    
    public String getIcon() {
    	return this.isWhite()? "♟":"♙";
    }
    public String getNoColorIcon() {
    	return "♟";
    }
    public char getPieceLetter() {
    	return 'P';
    }
}