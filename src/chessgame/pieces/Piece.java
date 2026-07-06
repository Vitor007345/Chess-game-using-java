package chessgame.pieces;

/**
 * Abstract base class representing a generic chess piece.
 * <p>
 * This class uses a highly optimized memory footprint by storing all the piece's
 * state (position, color, and movement history) within a single 8-bit byte.
 * </p>
 */
public abstract class Piece {
	
	/**
	 * A single byte storing the complete state of the piece using a bitfield:
	 * <ul>
	 * <li>Bits 0 - 2 (3 bits): Row position (0 to 7)</li>
	 * <li>Bits 3 - 5 (3 bits): Column position (0 to 7)</li>
	 * <li>Bit 6 (1 bit): Color flag (0 for white, 1 for black)</li>
	 * <li>Bit 7 (1 bit): hasMoved flag (0 for false, 1 for true)</li>
	 * </ul>
	 */
	private byte pieceInfo; 
	
	/**
	 * Constructs a Piece with specified row, column, and color.
	 * * @param row     The initial row position on the board (0-7).
	 * @param col     The initial column position on the board (0-7).
	 * @param isWhite True if the piece belongs to the white player, false if black.
	 */
	public Piece(byte row, byte col, boolean isWhite) {
	    this.setInfo((byte) (isWhite ? 0 : 0b01000000));
	    this.setPos(row, col);
	}
	
	/**
	 * Constructs a Piece using integer coordinates and color.
	 * This is a convenience constructor that casts the integers to bytes.
	 * * @param row     The initial row position on the board (0-7).
	 * @param col     The initial column position on the board (0-7).
	 * @param isWhite True if the piece belongs to the white player, false if black.
	 */
	public Piece(int row, int col, boolean isWhite) {
	    this((byte)row, (byte)col, isWhite);
	}

	/**
	 * Constructs a Piece by directly assigning the packed info byte.
	 * * @param pieceInfo The byte containing the encoded state of the piece.
	 */
	public Piece(byte pieceInfo) {
	    this.pieceInfo = pieceInfo;
	}
	
	/**
	 * Retrieves the row position of the piece from the bitfield.
	 * * @return The row position (0-7) extracted from the 3 least significant bits.
	 */
	public byte getRow() {
		return (byte)(this.getPieceInfo() & 0b111); //get 3 last bits
	}
	
	/**
	 * Retrieves the column position of the piece from the bitfield.
	 * * @return The column position (0-7) extracted from bits 3 to 5.
	 */
	public byte getCol() {
		return (byte) ((this.getPieceInfo() >> 3) & 0b111); //moving 3 bits to the right and then getting 3 last bits
	}
	
	/**
	 * Retrieves the combined row and column position of the piece.
	 * * @return A byte representing the position (0-63) extracted from the 6 least significant bits.
	 */
	public byte getPos() {
		return (byte) (this.getPieceInfo() & 0b111111); //get 6 last bits
	}
	
	/**
	 * Retrieves the raw packed byte containing the entire state of the piece.
	 * * @return The 8-bit piece information.
	 */
	public byte getPieceInfo() {
		return this.pieceInfo;
	}
	
	/**
	 * Retrieves the Unicode icon representing this specific piece and color.
	 * * @return A string containing the Unicode character.
	 */
	public abstract String getIcon();
	
	/**
	 * Retrieves a neutral Unicode icon representing this specific piece, regardless of color.
	 * * @return A string containing the solid Unicode character.
	 */
	public abstract String getNoColorIcon();
	
	/**
	 * Retrieves the standard algebraic notation letter for this piece.
	 * * @return A character representing the piece type (e.g., 'K', 'Q', 'N').
	 */
	public abstract char getPieceLetter();
	
	/**
	 * Overwrites the entire state byte of the piece.
	 * * @param info The new 8-bit piece information.
	 */
	public void setInfo(byte info) {
		this.pieceInfo = info;
	}
	
	/**
	 * Updates the row position of the piece within the bitfield.
	 * * @param row The new row position (0-7).
	 * @throws IllegalArgumentException If the provided row is out of the 0-7 bounds.
	 */
	public void setRow(byte row) {
		
		if((row & ~0b111) != 0) throw new IllegalArgumentException("Invalid row"); //row < 0 || row > 7
		
		//1 - clean the last row position
		//2 - get the last 3 bits of row and place it on the last 3 of pieces info
		this.setInfo((byte)((this.getPieceInfo() & 0b11111000) | row));
	}
	
	/**
	 * Updates the column position of the piece within the bitfield.
	 * * @param col The new column position (0-7).
	 * @throws IllegalArgumentException If the provided column is out of the 0-7 bounds.
	 */
	public void setCol(byte col) {
		if((col & ~0b111) != 0) throw new IllegalArgumentException("Invalid col"); //col < 0 || col > 7


		//1 - clean the last col position
		//2 - get the last 3 bits of col and place it on the correct place in info
		this.setInfo((byte)((this.getPieceInfo() & 0b11000111) | (col << 3)));
	}
	
	/**
	 * Updates both the row and column position of the piece within the bitfield simultaneously.
	 * * @param row The new row position (0-7).
	 * @param col The new column position (0-7).
	 * @throws IllegalArgumentException If either the row or column is out of bounds.
	 */
	public void setPos(byte row, byte col) {
		if((row & ~0b111) != 0) throw new IllegalArgumentException("Invalid row"); //row < 0 || row > 7

		if((col & ~0b111) != 0) throw new IllegalArgumentException("Invalid col"); //col < 0 || col > 7
		
		this.setInfo((byte)((this.getPieceInfo() & 0b11000000) | row | (col << 3)));
	}
	
	/**
	 * Updates the combined position (row and column) using a single 6-bit value.
	 * * @param pos The new combined position (0-63).
	 * @throws IllegalArgumentException If the position is out of bounds.
	 */
	public void setPos(byte pos) {
		if((pos & ~0b111111) != 0) throw new IllegalArgumentException("Invalid pos"); //pos < 0 || pos > 63
		this.setInfo((byte)((this.getPieceInfo() & 0b11000000) | pos));
	}
	
	/**
	 * Updates the 'hasMoved' status of the piece within the bitfield.
	 * * @param moved True to mark the piece as having moved, false to mark it as unmoved.
	 */
	public void setMoved(boolean moved) {
	    if (moved) {
	        // turn on bit 7
	        this.setInfo((byte)(this.getPieceInfo() | 0b10000000));
	    } else {
	        // turn off bit 7
	    	this.setInfo((byte)(this.getPieceInfo() & 0b01111111));
	    }
	}
	
	/**
	 * Checks if the piece belongs to the white player based on the bitfield.
	 * * @return True if the piece is white, false if it is black.
	 */
	public boolean isWhite() {
		return (this.getPieceInfo() & 0b01000000) == 0;
	}
	
	/**
	 * Checks if the piece has moved previously based on the bitfield.
	 * * @return True if the piece has moved, false otherwise.
	 */
	public boolean hasMoved() {
		return (this.getPieceInfo() & 0b10000000) != 0;
	}
	
}