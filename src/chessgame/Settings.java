package chessgame;

/**
 * Represents the configuration settings for the chess game.
 * This class acts as a data container for user preferences such as
 * board rotation, pawn promotion behavior, and coordinate visibility.
 */
public class Settings {
	
	private boolean autoReverseBoard;
    private boolean autoPromoteQueen;
    private boolean showCoordinates;
    
    /**
     * Constructs a new Settings object with the standard default values:
     * Auto-reverse board is disabled, auto-promote to Queen is disabled,
     * and board coordinates are visible.
     */
    public Settings() {
    	//standard settings
        this.autoReverseBoard = false;
        this.autoPromoteQueen = false;
        this.showCoordinates = true;
    }
    
    /**
     * Constructs a new Settings object with specified configuration values.
     * * @param autoReverseBoard True if the board should automatically flip based on whose turn it is, false otherwise.
     * @param autoPromoteQueen True if pawns should automatically promote to Queens without prompting, false to ask the user.
     * @param showCoordinates True to display algebraic coordinates (a-h, 1-8) on the board edges, false to hide them.
     */
	public Settings(boolean autoReverseBoard, boolean autoPromoteQueen, boolean showCoordinates) {
		super();
		this.autoReverseBoard = autoReverseBoard;
		this.autoPromoteQueen = autoPromoteQueen;
		this.showCoordinates = showCoordinates;
	}

    /**
     * Checks if the auto-reverse board feature is enabled.
     * * @return True if the board reverses automatically, false otherwise.
     */
	public boolean isAutoReverseBoard() {
		return autoReverseBoard;
	}

    /**
     * Sets the auto-reverse board feature.
     * * @param autoReverseBoard True to enable automatic board reversal, false to disable.
     */
	public void setAutoReverseBoard(boolean autoReverseBoard) {
		this.autoReverseBoard = autoReverseBoard;
	}

    /**
     * Checks if the auto-promote to Queen feature is enabled.
     * * @return True if pawns automatically promote to Queens, false otherwise.
     */
	public boolean isAutoPromoteQueen() {
		return autoPromoteQueen;
	}

    /**
     * Sets the auto-promote to Queen feature.
     * * @param autoPromoteQueen True to enable automatic promotion to Queens, false to prompt the user.
     */
	public void setAutoPromoteQueen(boolean autoPromoteQueen) {
		this.autoPromoteQueen = autoPromoteQueen;
	}

    /**
     * Checks if the board coordinates (a-h, 1-8) are currently set to be visible.
     * * @return True if coordinates are visible, false otherwise.
     */
	public boolean isShowCoordinates() {
		return showCoordinates;
	}

    /**
     * Sets the visibility of the board coordinates.
     * * @param showCoordinates True to display coordinates, false to hide them.
     */
	public void setShowCoordinates(boolean showCoordinates) {
		this.showCoordinates = showCoordinates;
	}
	
}