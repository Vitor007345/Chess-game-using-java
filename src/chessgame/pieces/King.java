package chessgame.pieces;

public class King extends Piece {
    public King(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    public King(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    public King(byte pieceInfo) {
        super(pieceInfo);
    }
    
    public String getIcon() {
    	return this.isWhite()? "♚":"♔";
    }
    public String getNoColorIcon() {
    	return "♚";
    }
}