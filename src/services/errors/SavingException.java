package services.errors;

public class SavingException extends Exception{

	public SavingException(String message, Throwable cause) {
		super(message, cause);
	}

	public SavingException(String message) {
		super(message);
	}

	public SavingException(Throwable cause) {
		super(cause);
	}
	
}
