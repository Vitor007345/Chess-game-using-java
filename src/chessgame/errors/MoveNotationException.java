package chessgame.errors;

/**
 * Exception thrown when a chess move notation is invalid, cannot be parsed, 
 * or represents an illegal move on the board.
 * It encapsulates the invalid input string and the specific reason why it failed.
 */
public class MoveNotationException extends Exception{
	
	/** The original string input that caused the exception. */
	private final String invalidInput;
	
	/** A descriptive message explaining why the input is invalid. */
	private final String whyIsInvalid;
	
	/**
	 * Constructs a new MoveNotationException with the invalid input and the reason.
	 * * @param invalidInput The exact string input that failed to be parsed or executed.
	 * @param whyIsInvalid A descriptive message detailing why the move is invalid.
	 */
	public MoveNotationException(String invalidInput, String whyIsInvalid) {
		this(invalidInput, whyIsInvalid, null);
	}
	
	/**
	 * Constructs a new MoveNotationException with the invalid input, the reason, 
	 * and the underlying cause (if any).
	 * * @param invalidInput The exact string input that failed to be parsed or executed.
	 * @param whyIsInvalid A descriptive message detailing why the move is invalid.
	 * @param e            The underlying cause of the exception.
	 */
	public MoveNotationException(String invalidInput, String whyIsInvalid, Throwable e) {
		super("MoveNotationException: " + invalidInput + ":" + whyIsInvalid, e);
		this.invalidInput = invalidInput;
		this.whyIsInvalid = whyIsInvalid;
	}
	
	/**
	 * Retrieves the original input string that caused this exception.
	 * * @return The invalid move notation string.
	 */
	public String getInvalidInput() {
		return this.invalidInput;
	}
	
	/**
	 * Retrieves the specific reason why the move notation is considered invalid.
	 * * @return A string explaining the cause of the invalidity.
	 */
	public String getWhyIsInvalid() {
		return this.whyIsInvalid;
	}
}