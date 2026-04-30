package chessgame.pieces;

public class Knight extends Piece {
    public Knight(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    public Knight(int row, int col, boolean isWhite) {
	    super(row, col, isWhite);
	}

    public Knight(byte pieceInfo) {
        super(pieceInfo);
    }
}