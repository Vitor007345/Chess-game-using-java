package chessgame;

public class Settings {
	
	private boolean autoReverseBoard;
    private boolean autoPromoteQueen;
    private boolean showCoordinates;
    
    public Settings() {
        this.autoReverseBoard = false;
        this.autoPromoteQueen = false;
        this.showCoordinates = false;
    }
    
	public Settings(boolean autoReverseBoard, boolean autoPromoteQueen, boolean showCoordinates) {
		super();
		this.autoReverseBoard = autoReverseBoard;
		this.autoPromoteQueen = autoPromoteQueen;
		this.showCoordinates = showCoordinates;
	}

	public boolean isAutoReverseBoard() {
		return autoReverseBoard;
	}

	public void setAutoReverseBoard(boolean autoReverseBoard) {
		this.autoReverseBoard = autoReverseBoard;
	}

	public boolean isAutoPromoteQueen() {
		return autoPromoteQueen;
	}

	public void setAutoPromoteQueen(boolean autoPromoteQueen) {
		this.autoPromoteQueen = autoPromoteQueen;
	}

	public boolean isShowCoordinates() {
		return showCoordinates;
	}

	public void setShowCoordinates(boolean showCoordinates) {
		this.showCoordinates = showCoordinates;
	}
	
	
	
}
