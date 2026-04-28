package chessgame.pieces;

public abstract class Piece {
	//atributes
	private byte pieceInfo; // bits 0 - 2 for rows, bits 3 - 5 for cols, bit 6 for white(0) or black(1) and byte 7 to status hasMoved 
	
	//constructors
	public Piece() {
		this.pieceInfo = 0;
	}
	public Piece(boolean isWhite) {
		this();
		if(!isWhite) {
			this.setInfo((byte)0b01000000);
		}
	}
	public Piece(byte row, byte col, boolean isWhite) {
		this(isWhite);
		this.setPos(row, col);
	}
	public Piece(byte row, byte col) {
		this();
		this.setPos(row, col);
	}
	public Piece(byte pos, boolean isWhite) {
		this(isWhite);
		this.setPos(pos);
	}
	public Piece(byte pos) {
		this();
		this.setPos(pos);
	}
	
	//getters
	public byte getRow() {
		return (byte)(this.getPieceInfo() & 0b111); //get 3 last bits
	}
	public byte getCol() {
		return (byte) ((this.getPieceInfo() >> 3) & 0b111); //moving 3 bits to the right and then getting 3 last bits
	}
	public byte getPieceInfo() {
		return this.pieceInfo;
	}
	
	//setters
	private void setInfo(byte info) {
		this.pieceInfo = info;
	}
	
	public void setRow(byte row) {
		
		if((row & ~0b111) != 0) throw new IllegalArgumentException("Invalid row"); //row < 0 || row > 7
		
		//1 - clean the last row position
		//2 - get the last 3 bits of row and place it on the last 3 of pieces info
		this.setInfo((byte)((this.getPieceInfo() & 0b11111000) | row));
	}
	
	public void setCol(byte col) {
		if((col & ~0b111) != 0) throw new IllegalArgumentException("Invalid col"); //col < 0 || col > 7


		//1 - clean the last col position
		//2 - get the last 3 bits of col and place it on the correct place in info
		this.setInfo((byte)((this.getPieceInfo() & 0b11000111) | (col << 3)));
	}
	
	public void setPos(byte row, byte col) {
		if((row & ~0b111) != 0) throw new IllegalArgumentException("Invalid row"); //row < 0 || row > 7

		if((col & ~0b111) != 0) throw new IllegalArgumentException("Invalid col"); //col < 0 || col > 7
		
		this.setInfo((byte)((this.getPieceInfo() & 0b11000000) | row | (col << 3)));
	}
	public void setPos(byte pos) {
		if((pos & ~0b111111) != 0) throw new IllegalArgumentException("Invalid pos"); //pos < 0 || pos > 63
		this.setInfo((byte)((this.getPieceInfo() & 0b11000000) | pos));
	}
	
	public void setMoved(boolean moved) {
	    if (moved) {
	        // turn on bit 7
	        this.setInfo((byte)(this.getPieceInfo() | 0b10000000));
	    } else {
	        // turn off bit 7
	    	this.setInfo((byte)(this.getPieceInfo() & 0b01111111));
	    }
	}
	
	//methods
	public boolean isWhite() {
		return (this.getPieceInfo() & 0b01000000) == 0;
	}
	public boolean hasMoved() {
		return (this.getPieceInfo() & 0b10000000) != 0;
	}
	
	public abstract boolean isValidMove(byte targetRow, byte targetCol);
	
}
