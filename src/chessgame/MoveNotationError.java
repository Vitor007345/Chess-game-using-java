package chessgame;

public class MoveNotationError extends IllegalArgumentException{
	private final String invalidInput;
	private final String whyIsInvalid;
	
	
	
	public MoveNotationError(String invalidInput, String whyIsInvalid) {
		this(invalidInput, whyIsInvalid, null);
	}
	
	public MoveNotationError(String invalidInput, String whyIsInvalid, Throwable e) {
		super("MoveNotationError: " + invalidInput + ":" + whyIsInvalid, e);
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
