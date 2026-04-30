package chessgame.pieces;

public class Queen extends Piece {
    public Queen(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    public Queen(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    public Queen(byte pieceInfo) {
        super(pieceInfo);
    }
}