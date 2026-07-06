package chessgame.pieces;

/**
 * Represents a Pawn piece in the chess game.
 * Extends the base {@link Piece} class and provides specific implementations
 * for the Pawn's algebraic letter and Unicode icons.
 */
public class Pawn extends Piece {
	
    /**
     * Constructs a Pawn piece with the specified board coordinates and color.
     * * @param row     The initial row position on the board (0-7).
     * @param col     The initial column position on the board (0-7).
     * @param isWhite True if the piece belongs to the white player, false if black.
     */
    public Pawn(byte row, byte col, boolean isWhite) {
        super(row, col, isWhite);
    }
    
    /**
     * Constructs a Pawn piece using integer coordinates and color.
     * This is a convenience constructor that passes the integers to the superclass.
     * * @param row     The initial row position on the board (0-7).
     * @param col     The initial column position on the board (0-7).
     * @param isWhite True if the piece belongs to the white player, false if black.
     */
    public Pawn(int row, int col, boolean isWhite) {
	    super(row, col, isWhite);
	}

    /**
     * Constructs a Pawn piece restoring its state from a packed information byte.
     * * @param pieceInfo The byte containing the encoded state (color, position, moved flag) of the piece.
     */
    public Pawn(byte pieceInfo) {
        super(pieceInfo);
    }
    
    /**
     * Retrieves the Unicode icon representing this Pawn, specific to its color.
     * * @return A string containing the Unicode character for the Pawn.
     */
    public String getIcon() {
    	return this.isWhite()? "♟":"♙";
    }
    
    /**
     * Retrieves a neutral Unicode icon representing a Pawn, regardless of its color.
     * Typically used for GUI representations where color is determined by the component's foreground.
     * * @return A string containing the solid Unicode character for the Pawn.
     */
    public String getNoColorIcon() {
    	return "♟";
    }
    
    /**
     * Retrieves the standard algebraic notation letter for the Pawn.
     * * @return The character 'P'.
     */
    public char getPieceLetter() {
    	return 'P';
    }
}