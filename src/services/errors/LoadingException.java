package services.errors;

/**
 * Exception thrown to indicate that an error occurred while attempting
 * to load data from the file system (e.g., reading a saved game or user settings).
 */
public class LoadingException extends Exception{

	/**
	 * Constructs a new LoadingException with the specified detail message and cause.
	 * * @param message The detail message explaining the reason for the exception.
	 * @param cause   The underlying cause of the exception (usually an IOException or parsing error).
	 */
	public LoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new LoadingException with the specified detail message.
	 * * @param message The detail message explaining the reason for the exception.
	 */
	public LoadingException(String message) {
		super(message);
	}

	/**
	 * Constructs a new LoadingException with the specified cause.
	 * * @param cause The underlying cause of the exception (usually an IOException or parsing error).
	 */
	public LoadingException(Throwable cause) {
		super(cause);
	}
	
}