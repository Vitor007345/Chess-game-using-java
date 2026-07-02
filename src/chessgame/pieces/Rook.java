package chessgame.pieces;

public class Rook extends Piece {
    public Rook(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    public Rook(int row, int col, boolean isWhite) {
	    super(row, col, isWhite);
	}

    public Rook(byte pieceInfo) {
        super(pieceInfo);
    }
    
    public String getIcon() {
    	return this.isWhite()? "♜":"♖";
    }
    public String getNoColorIcon() {
    	return "♜";
    }
    public char getPieceLetter() {
    	return 'R';
    }
}