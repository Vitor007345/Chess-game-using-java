package services.errors;

/**
 * Exception thrown to indicate that an error occurred while attempting
 * to save data to the file system (e.g., writing a game state or user settings).
 */
public class SavingException extends Exception{

	/**
	 * Constructs a new SavingException with the specified detail message and cause.
	 * * @param message The detail message explaining the reason for the exception.
	 * @param cause   The underlying cause of the exception (usually an IOException).
	 */
	public SavingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new SavingException with the specified detail message.
	 * * @param message The detail message explaining the reason for the exception.
	 */
	public SavingException(String message) {
		super(message);
	}

	/**
	 * Constructs a new SavingException with the specified cause.
	 * * @param cause The underlying cause of the exception (usually an IOException).
	 */
	public SavingException(Throwable cause) {
		super(cause);
	}
	
}