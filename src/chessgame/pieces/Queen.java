package chessgame.pieces;

/**
 * Represents a Queen piece in the chess game.
 * Extends the base {@link Piece} class and provides specific implementations
 * for the Queen's algebraic letter and Unicode icons.
 */
public class Queen extends Piece {
	
    /**
     * Constructs a Queen piece with the specified board coordinates and color.
     * * @param row     The initial row position on the board (0-7).
     * @param col     The initial column position on the board (0-7).
     * @param isWhite True if the piece belongs to the white player, false if black.
     */
    public Queen(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    /**
     * Constructs a Queen piece using integer coordinates and color.
     * This is a convenience constructor that passes the integers to the superclass.
     * * @param row     The initial row position on the board (0-7).
     * @param col     The initial column position on the board (0-7).
     * @param isWhite True if the piece belongs to the white player, false if black.
     */
    public Queen(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    /**
     * Constructs a Queen piece restoring its state from a packed information byte.
     * * @param pieceInfo The byte containing the encoded state (color, position, moved flag) of the piece.
     */
    public Queen(byte pieceInfo) {
        super(pieceInfo);
    }
    
    /**
     * Retrieves the Unicode icon representing this Queen, specific to its color.
     * * @return A string containing the Unicode character for the Queen.
     */
    public String getIcon() {
    	return this.isWhite()? "♛":"♕";
    }
    
    /**
     * Retrieves a neutral Unicode icon representing a Queen, regardless of its color.
     * Typically used for GUI representations where color is determined by the component's foreground.
     * * @return A string containing the solid Unicode character for the Queen.
     */
    public String getNoColorIcon() {
    	return "♛";
    }
    
    /**
     * Retrieves the standard algebraic notation letter for the Queen.
     * * @return The character 'Q'.
     */
    public char getPieceLetter() {
    	return 'Q';
    }
}