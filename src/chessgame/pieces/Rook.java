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
}