package services.errors;

public class LoadingException extends Exception{

	public LoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadingException(String message) {
		super(message);
	}

	public LoadingException(Throwable cause) {
		super(cause);
	}
	
}
