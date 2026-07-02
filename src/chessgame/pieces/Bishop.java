package chessgame.pieces;

public class Bishop extends Piece {
    public Bishop(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    public Bishop(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }


    public Bishop(byte pieceInfo) {
        super(pieceInfo);
    }
    
    public String getIcon() {
    	return this.isWhite()? "♝":"♗";
    }
    public String getNoColorIcon() {
    	return "♝";
    }
    public char getPieceLetter() {
    	return 'B';
    }
}
