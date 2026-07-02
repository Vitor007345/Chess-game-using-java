package chessgame.errors;

public class MoveNotationException extends Exception{
	private final String invalidInput;
	private final String whyIsInvalid;
	
	
	
	public MoveNotationException(String invalidInput, String whyIsInvalid) {
		this(invalidInput, whyIsInvalid, null);
	}
	
	public MoveNotationException(String invalidInput, String whyIsInvalid, Throwable e) {
		super("MoveNotationException: " + invalidInput + ":" + whyIsInvalid, e);
		this.invalidInput = invalidInput;
		this.whyIsInvalid = whyIsInvalid;
	}
	
	public String getInvalidInput() {
		return this.invalidInput;
	}
	public String getWhyIsInvalid() {
		return this.whyIsInvalid;
	}
}
