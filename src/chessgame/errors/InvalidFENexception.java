package chessgame.errors;

/**
 * Exception thrown when a Forsyth-Edwards Notation (FEN) string is invalid,
 * malformed, or cannot be successfully parsed to create a board state.
 */
public class InvalidFENexception extends Exception{

	/**
	 * Constructs a new InvalidFENexception with the specified detail message.
	 * * @param message The detail message explaining why the FEN string is invalid.
	 */
	public InvalidFENexception(String message) {
		super(message);
	}
	
}