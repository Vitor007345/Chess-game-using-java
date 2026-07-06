package chessgame.moves;

import chessgame.pieces.Pawn;
import chessgame.pieces.Piece;

/**
 * Represents a single chess move.
 * This class encapsulates all the information needed to execute, record, and undo a move,
 * including the pieces involved, special move flags (castling, promotion), and the algebraic notation.
 */
public class Move {
    
    /** The algebraic notation of this move (e.g., "e4", "Nf3", "O-O"). */
    private String algebricNotation;
    
    /** The previous state information (like position and moved flag) of the piece before the move. */
    private final byte movedPieceOldInfo;
    
    /** The piece that is making the move. */
    private final Piece movedPiece;
    
    /** The piece that was captured during this move, if any. Null if no piece was captured. */
    private final Piece capturedPiece;
    
    /** The original pawn object before it was promoted. Null if this is not a promotion move. */
    private final Pawn originalPawn;
    
    /** Flag indicating whether this move is a pawn promotion. */
    private final boolean promotion;
    
    /** Flag indicating whether this move is a castling maneuver. */
    private final boolean castle;
    
    /** Flag indicating whether this move is a short (kingside) castle. */
    private final boolean shortCastle;
    
    /** Flag indicating if this is a dummy move (used internally, e.g., for setting up En Passant from FEN). */
    private final boolean dummy;
    
    /** Flag indicating whether this move has been undone or deleted. */
    private boolean deleted;
    
    
    /**
     * Private master constructor that initializes all fields.
     * * @param movedPieceOldInfo The state byte of the piece before moving.
     * @param moved             The piece being moved.
     * @param captured          The piece being captured (null if none).
     * @param promotion         True if it's a promotion.
     * @param castle            True if it's a castling move.
     * @param shortCastle       True if it's a short castle.
     * @param dummy             True if it's a dummy/ghost move.
     */
    private Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion, boolean castle, boolean shortCastle, boolean dummy){
        this(movedPieceOldInfo, moved, captured, promotion, castle, shortCastle, dummy, null);
    }

    /**
     * Private master constructor that initializes all fields including the original pawn.
     * * @param movedPieceOldInfo The state byte of the piece before moving.
     * @param moved             The piece being moved.
     * @param captured          The piece being captured (null if none).
     * @param promotion         True if it's a promotion.
     * @param castle            True if it's a castling move.
     * @param shortCastle       True if it's a short castle.
     * @param dummy             True if it's a dummy/ghost move.
     * @param originalPawn      The original pawn piece before it was promoted.
     */
    private Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion, boolean castle, boolean shortCastle, boolean dummy, Pawn originalPawn) {
        this.movedPieceOldInfo = movedPieceOldInfo;
        
        this.movedPiece = moved;
        this.capturedPiece = captured;
        this.originalPawn = originalPawn;
        
        this.promotion = promotion;
        
        this.castle = castle;
        this.shortCastle = shortCastle;
        
        this.dummy = dummy;
        this.deleted = false;
    }
    
    /**
     * Private constructor for a standard move with potential capture, promotion, or castling.
     */
    private Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion, boolean castle, boolean shortCastle) {
        this(movedPieceOldInfo, moved, captured, promotion, castle, shortCastle, false);
    }
    
    
    /**
     * Constructs a standard move that might be a capture or a promotion.
     * * @param movedPieceOldInfo The state byte of the piece before moving.
     * @param moved             The piece being moved.
     * @param captured          The piece being captured (null if none).
     * @param promotion         True if it's a promotion.
     */
    public Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion){
        this(movedPieceOldInfo, moved, captured, promotion, false, false);
        
    }
    
    /**
     * Constructs a castling move.
     * * @param castle            True to indicate this is a castle move.
     * @param shortCastle       True for short (kingside) castle, false for long (queenside).
     * @param movedPieceOldInfo The state byte of the king before moving.
     * @param moved             The king piece being moved.
     */
    public Move(boolean castle, boolean shortCastle, byte movedPieceOldInfo, Piece moved) {
        this(movedPieceOldInfo, moved, null, false, castle, shortCastle);
        
    }
    
    
    /**
     * Constructs a standard move with a capture.
     * * @param movedPieceOldInfo The state byte of the piece before moving.
     * @param moved             The piece being moved.
     * @param captured          The piece being captured.
     */
    public Move(byte movedPieceOldInfo, Piece moved, Piece captured) {
        this(movedPieceOldInfo, moved, captured, false);
    }
    
    /**
     * Constructs a standard move that involves a promotion (no capture).
     * * @param movedPieceOldInfo The state byte of the piece before moving.
     * @param moved             The piece being moved (the newly promoted piece).
     * @param promotion         True to indicate this is a promotion.
     */
    public Move(byte movedPieceOldInfo, Piece moved, boolean promotion) {
        this(movedPieceOldInfo, moved, null, promotion);
    }
    
    /**
     * Constructs a simple, quiet move with no captures or special flags.
     * * @param movedPieceOldInfo The state byte of the piece before moving.
     * @param moved             The piece being moved.
     */
    public Move(byte movedPieceOldInfo, Piece moved) {
        this(movedPieceOldInfo, moved,  null);
    }

    /**
     * Constructs a dummy move, typically used to represent En Passant targets 
     * generated from a FEN string.
     * * @param dummy             True to indicate this is a dummy/ghost move.
     * @param movedPieceOldInfo The state byte of the dummy piece.
     * @param moved             The dummy piece being moved.
     */
    public Move(boolean dummy, byte movedPieceOldInfo, Piece moved) {
        this(movedPieceOldInfo, moved,  null, false, false, false, dummy);
    }
    
    /**
     * Constructs a promotion move that includes a capture, storing the original pawn.
     * * @param movedPieceOldInfo The state byte of the pawn before moving.
     * @param moved             The newly promoted piece.
     * @param captured          The piece being captured.
     * @param promotion         True to indicate this is a promotion.
     * @param originalPawn      The original pawn object before promotion.
     */
    public Move(byte movedPieceOldInfo, Piece moved, Piece captured, boolean promotion, Pawn originalPawn){
        this(movedPieceOldInfo, moved, captured, promotion, false, false, false, originalPawn);
    }

    /**
     * Constructs a promotion move without a capture, storing the original pawn.
     * * @param movedPieceOldInfo The state byte of the pawn before moving.
     * @param moved             The newly promoted piece.
     * @param promotion         True to indicate this is a promotion.
     * @param originalPawn      The original pawn object before promotion.
     */
    public Move(byte movedPieceOldInfo, Piece moved, boolean promotion, Pawn originalPawn) {
        this(movedPieceOldInfo, moved, null, promotion, originalPawn);
    }

    /**
     * Checks if this move is a pawn promotion.
     * * @return True if it is a promotion, false otherwise.
     */
    public boolean isPromotion() {
        return this.promotion;
    }

    /**
     * Checks if this move has been marked as deleted (e.g., during an undo operation).
     * * @return True if deleted, false otherwise.
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Marks this move as deleted.
     */
    public void delete() {
        this.deleted = true;
    }
    
    /**
     * Checks if this move is a castling move.
     * * @return True if it is a castle, false otherwise.
     */
    public boolean isCastle() {
        return this.castle;
    }
    
    /**
     * Checks if this move is a short (kingside) castle.
     * * @return True if it is a short castle, false otherwise.
     */
    public boolean isShortCastle() {
        return this.shortCastle;
    }
    
    /**
     * Retrieves the stored state byte of the piece before it moved.
     * * @return The previous info byte of the moved piece.
     */
    public byte getMovedPieceOldInfo() {
        return this.movedPieceOldInfo;
    }

    /**
     * Retrieves the piece that was moved.
     * * @return The moved {@link Piece} object.
     */
    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    /**
     * Retrieves the piece that was captured during this move.
     * * @return The captured {@link Piece} object, or null if no piece was captured.
     */
    public Piece getCapturedPiece() {
        return this.capturedPiece;
    }
    
    /**
     * Retrieves the original pawn object that was promoted.
     * * @return The original {@link Pawn}, or null if this was not a promotion.
     */
    public Pawn getOriginalPawn() {
        return this.originalPawn;
    }
    
    /**
     * Checks if this is a dummy move.
     * * @return True if this is a dummy move, false otherwise.
     */
    public boolean isDummy() {
        return this.dummy;
    }
    
    /**
     * Determines if this move was a pawn moving two squares forward from its starting position.
     * Note: This method accurately computes the condition only if this object represents the last move made.
     * * @return True if the move was a double pawn push, false otherwise.
     */
    //only works if this is the lastMove
    public boolean isPawnDoubleFowardMove() {
        Pawn oldPawn = new Pawn(this.movedPieceOldInfo);
        return this.movedPiece instanceof Pawn p && oldPawn.getCol() == p.getCol() && (p.getRow() == oldPawn.getRow() + (p.isWhite()?2:-2));
    }

    /**
     * Sets the algebraic notation string for this move.
     * * @param algebricNotation The algebraic notation (e.g., "Nf3").
     */
    public void setAlgebricNotation(String algebricNotation) {
        this.algebricNotation = algebricNotation;
    }
    
    /**
     * Retrieves the algebraic notation string of this move.
     * * @return The algebraic notation string.
     */
    public String getAlgebricNotation() {
        return this.algebricNotation;
    }
    
}