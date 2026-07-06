package vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import chessgame.BoardFactory;
import chessgame.ChessBoard;
import chessgame.Settings;
import chessgame.errors.MoveNotationException;
import chessgame.pieces.Pawn;
import chessgame.pieces.Piece;
import services.GameSaveFileManager;
import services.SettingsFileManager;
import services.errors.LoadingException;
import services.errors.SavingException;

/**
 * The main Graphical User Interface (GUI) window for the chess game.
 * This class handles the rendering of the board, user interactions (clicks and text inputs),
 * and updates the visual state based on the underlying {@link ChessBoard} logic.
 */
public class ChessgameWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    
    /** 2D array of buttons representing the 64 squares of the chess board. */
    private JButton[][] boardButtons = new JButton[8][8];
    
    /** The main panel that holds the grid of board buttons. */
    private JPanel boardPanel;
    
    /** Text field for the user to input moves using algebraic notation. */
    private JTextField txtMoveInput;
    
    /** Label to display the current fullmove and halfmove counts. */
    private JLabel lblCounters;
    
    /** Label to indicate whose turn it is to play, or the final game result. */
    private JLabel lblTurn;
    
    /** Label to display error messages (e.g., invalid moves) to the user. */
    private JLabel lblError;

    /** The background color used for the light squares on the board. */
    private static final Color lightSquare = new Color(180, 180, 180);
    
    /** The background color used for the dark squares on the board. */
    private static final Color darkSquare = new Color(130, 127, 127);
    
    /** The underlying logic model of the chess board. */
    private ChessBoard chessboard;
    
    /** The row index of the currently selected square (-1 if no square is selected). */
    private int selectedRow;
    
    /** The column index of the currently selected square (-1 if no square is selected). */
    private int selectedCol;
    
    /** Flag indicating whether the board is drawn from Black's perspective (true) or White's (false). */
    private boolean blackPerspective = false;
    
    /** The configuration settings for the game (auto-reverse, auto-promote, etc.). */
    private Settings settings;
    
    /**
     * Constructs a new ChessgameWindow initializing a standard chess board from scratch.
     */
    public ChessgameWindow() {
        this(BoardFactory.standardChessBoard());
    }
    
    /**
     * Constructs a new ChessgameWindow using a specific {@link ChessBoard} instance.
     * Initializes the UI components, event listeners, and loads the user's settings.
     * * @param chessboard The logical chess board to be rendered and played.
     */
    public ChessgameWindow(ChessBoard chessboard) {
        this.chessboard = chessboard;
        this.selectedRow = -1;
        this.selectedCol = -1;
        
        this.loadSettings();
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setBounds(100, 100, 550, 650);
        this.setTitle("Java Chess Engine");
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	saveAndGoToMenu();
            }
        });
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(5, 5)); 
        this.setContentPane(contentPane);

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        
        this.lblTurn = new JLabel("White to play", SwingConstants.CENTER);
        this.lblTurn.setFont(new Font("Arial", Font.BOLD, 22));
        
        this.lblCounters = new JLabel("Fullmoves: 1 | Halfmoves (50-move rule): 0/100", SwingConstants.CENTER);
        this.lblCounters.setFont(new Font("Arial", Font.PLAIN, 14));
        this.lblCounters.setForeground(Color.GRAY);
        
        topPanel.add(this.lblTurn);
        topPanel.add(this.lblCounters);
        
        contentPane.add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (Board) ---
        this.boardPanel = new JPanel();
        this.boardPanel.setLayout(new GridLayout(8, 8, 0, 0));
        contentPane.add(boardPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL ---
        JPanel southContainer = new JPanel();
        southContainer.setLayout(new BorderLayout());
        
        // Input Panel
        JPanel inputPanel = new JPanel(); 
        this.txtMoveInput = new JTextField(10); 
        this.txtMoveInput.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnSendMove = createControlBtn("Send move");
        
        inputPanel.add(txtMoveInput);
        inputPanel.add(btnSendMove);
        
        // Control Panel
        JPanel controlPanel = new JPanel();
        JButton btnGoToMenu = createControlBtn("Go to Menu");
        JButton btnReverse = createControlBtn("Reverse Board");
        JButton btnUndoMove = createControlBtn("Undo move");
        
        controlPanel.add(btnGoToMenu);
        controlPanel.add(btnReverse);
        controlPanel.add(btnUndoMove);
        
        // Error Label
        this.lblError = new JLabel(" ", SwingConstants.CENTER);
        this.lblError.setForeground(Color.RED);
        this.lblError.setFont(new Font("Arial", Font.BOLD, 14));

        southContainer.add(inputPanel, BorderLayout.NORTH);
        southContainer.add(controlPanel, BorderLayout.CENTER);
        southContainer.add(this.lblError, BorderLayout.SOUTH);
        
        contentPane.add(southContainer, BorderLayout.SOUTH);

        // --- ACTION LISTENERS ---
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fbtnSendMove();
            }
        };
        btnSendMove.addActionListener(sendAction);
        this.txtMoveInput.addActionListener(sendAction);
        
        btnGoToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fbtnbtnGoToMenu();
            }
        });
        
        btnReverse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fbtnReverse();
            }
        });
        
        btnUndoMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fbtnUndoMove();
            }
        });

        // --- INITIALIZATION ---
        this.initializeBoard();
        this.updateScreen();
    }

    // --- BUTTON CREATION HELPER ---
    /**
     * Helper method to create standardized control buttons for the UI.
     * * @param text The text to be displayed on the button.
     * @return A styled {@link JButton} ready to be added to a panel.
     */
    private JButton createControlBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }
    
    // --- BUTTON FUNCTIONS ---
    
    /**
     * Handles the action of sending a typed algebraic move.
     */
    private void fbtnSendMove() {
        String typedMove = txtMoveInput.getText().trim();
        if (!typedMove.isEmpty()) {
            executeTypedMove(typedMove);
            txtMoveInput.setText(""); 
        }
    }
    
    /**
     * Handles the action of navigating back to the main menu.
     */
    private void fbtnbtnGoToMenu() {
    	saveAndGoToMenu();
    }
    
    /**
     * Handles the action of manually flipping the board perspective.
     */
    private void fbtnReverse() {
        reverseBoardVision();
    }
    
    /**
     * Handles the action of undoing the last played move.
     */
    private void fbtnUndoMove() {
        undoMove();
    }

    // --- BOARD LOGIC AND GUI UPDATES ---
    
    /**
     * Initializes the 8x8 grid of buttons, sets their alternating background colors,
     * assigns action listeners, and overrides the paintComponent to draw coordinates if enabled.
     */
    private void initializeBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                
                final int row = i; 
                final int col = j;

                
                JButton btn = new JButton("") {
					@Override
					protected void paintComponent(java.awt.Graphics g) {
						super.paintComponent(g); //paint the normal stuff first
						
						//if showCoordinates settings is true draw it
						if (ChessgameWindow.this.settings.isShowCoordinates()) {
							g.setFont(new Font("Arial", Font.BOLD, 12));
							
							//UI trick: Color opposite the background for contrast (like on Chess.com)
							if ((row + col) % 2 != 0) {
								g.setColor(darkSquare); 
							} else {
								g.setColor(lightSquare);
							}
							
							//Draw numbers (1–8) in the top-left corner of the left edge.
							if ((!blackPerspective && col == 0) || (blackPerspective && col == 7)) {
								g.drawString(String.valueOf(row + 1), 3, 14);
							}
							
							//Draw letters (a–h) in the bottom right corner of the bottom edge.
							if ((!blackPerspective && row == 0) || (blackPerspective && row == 7)) {
								String letter = String.valueOf((char)('a' + col));
								g.drawString(letter, getWidth() - 12, getHeight() - 3);
							}
						}
					}
				};
                
                btn.setFont(new Font("Dialog", Font.BOLD, 36)); 
                btn.setFocusPainted(false); 
                
                if ((row + col) % 2 != 0) {
                    btn.setBackground(lightSquare);
                } else {
                    btn.setBackground(darkSquare);
                }

                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPressed(row, col);
                    }
                });

                this.boardButtons[row][col] = btn;
                this.boardPanel.add(btn);
            }
        }
    }

    /**
     * Handles the logic when a square on the board is clicked by the user.
     * Manages the selection of pieces and attempts to move them if a second square is clicked.
     * * @param row The row index of the clicked square.
     * @param col The column index of the clicked square.
     */
    private void btnPressed(int row, int col) {
        System.out.println("Clique no tabuleiro -> Linha: " + row + " | Coluna: " + col);
        if (chessboard.getResult() == null) {
            if (selectedRow == -1) {
                //first click
                selectedRow = row;
                selectedCol = col;
                boardButtons[row][col].setBackground(Color.YELLOW); 
            } else {
                //second click
                if(selectedRow == row && selectedCol == col) {
                    //reset
                    this.resetSquareColor(selectedRow, selectedCol);
                    selectedRow = -1;
                    selectedCol = -1;
                }else {
                    try {
                        char promoPiece = 'Q';
                        Piece movingPiece = chessboard.getPiece(selectedRow, selectedCol);
                        if (movingPiece instanceof Pawn && (row == 0 || row == 7)) {
                            promoPiece = this.getPromoPiece();
                        }
                        //if user closed popup without choosing it cancel the move
                        if(promoPiece != ' ') {
                            //try to move
                            chessboard.move(selectedRow, selectedCol, row, col, promoPiece);
                            
                            lblError.setText(" ");
                            this.updateScreen();
                        }
                    } catch (MoveNotationException e) {
                        //Error IlegalMove
                        lblError.setText(e.getWhyIsInvalid());
                    } catch (AssertionError e) {
                        lblError.setText("Erro crítico: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        //reset
                        this.resetSquareColor(selectedRow, selectedCol);
                        selectedRow = -1;
                        selectedCol = -1;
                    }
                }
            }
        }
    }
    
    /**
     * Tries to execute a move based on algebraic notation provided by the user.
     * * @param move The algebraic notation string representing the move.
     */
    private void executeTypedMove(String move) {
        System.out.println("Movimento digitado: " + move);
        
        try {
            this.chessboard.move(move);
            lblError.setText(" ");
            this.updateScreen();
        } catch (MoveNotationException e) {
            lblError.setText(e.getInvalidInput() + " is an invalid move - " + e.getWhyIsInvalid());
        } catch (AssertionError e) {
            lblError.setText("Erro crítico: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //reset
            if(selectedRow != -1) {
                this.resetSquareColor(selectedRow, selectedCol);
                selectedRow = -1;
                selectedCol = -1;
            }
        }
    }
    
    /**
     * Resets the background color of a specific square back to its original board color.
     * * @param row The row index of the square.
     * @param col The column index of the square.
     */
    private void resetSquareColor(int row, int col) {
        if ((row + col) % 2 != 0) {
            boardButtons[row][col].setBackground(lightSquare);
        } else {
            boardButtons[row][col].setBackground(darkSquare);
        }
    }
    
    /**
     * Updates the top label to reflect whose turn it currently is.
     */
    private void updateTurnOnScreen() {
        if(chessboard.isWhiteToMove()) {
            lblTurn.setText("White to play");
        } else {
            lblTurn.setText("Black to play");
        }
    }
    
    /**
     * Syncs the visual board buttons with the logical state of the {@link ChessBoard},
     * updating icons and text colors.
     */
    private void updateBoardOnScreen() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = this.chessboard.getPiece(row, col);
                JButton btn = boardButtons[row][col];
                if (p != null) {
                    btn.setText(p.getNoColorIcon());
                    btn.setForeground(p.isWhite() ? Color.WHITE : Color.BLACK);
                } else {
                    btn.setText("");
                }
            }
        }
    }
    
    /**
     * Updates the turn label if the game has concluded (checkmate, stalemate, etc.).
     */
    private void updateResultOnScreen() {
        if(chessboard.getResult() != null) {
            lblTurn.setText("FIM DE JOGO! - Resultado: " + chessboard.getResult());
        }
    }
    
    /**
     * Master update method that refreshes all visual components on the screen,
     * including the board, turn indicator, counters, and handles auto-reversing if enabled.
     */
    private void updateScreen() {
        this.updateTurnOnScreen();
        this.updateBoardOnScreen();
        this.updateResultOnScreen();
        
        int fullmoves = this.chessboard.getFullmoveNumber();
        int halfmoves = this.chessboard.getHalfmoveClock();
        this.lblCounters.setText(String.format("Fullmoves: %d | Halfmoves (50-move rule): %d/100", fullmoves, halfmoves));
        
        
        if(this.settings.isAutoReverseBoard()) {
        	if(this.chessboard.isWhiteToMove()) {
        		this.setWhiteVision();
        	}else {
        		this.setBlackVision();
        	}
        }
    }
    
    /**
     * Requests the underlying board model to undo the last move, then updates the screen.
     * Displays an error message if there are no moves to undo.
     */
    private void undoMove() {
        if(this.chessboard.undoMove()) {
            lblError.setText(" ");
            this.updateScreen();
        }else {
            lblError.setText("Can't undo more, because there are no more moves saved");
        }
    }
    
    /**
     * Determines which piece a Pawn should promote to.
     * If auto-promote is enabled in settings, automatically returns a Queen.
     * Otherwise, presents a visual dialog for the user to select the desired piece.
     * * @return The character representing the chosen piece ('Q', 'R', 'B', or 'N'), or ' ' if cancelled.
     */
    private char getPromoPiece() {
    	if (this.settings.isAutoPromoteQueen()) {
            return 'Q';
        }
    	
        String[] options = {"Queen (♕)", "Rook (♖)", "Bishop (♗)", "Knight (♘)"};
        
        //Show popup
        int choice = JOptionPane.showOptionDialog(
                this, 
                "Choose a piece to promote your pawn:", 
                "Pawn Promotion", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0] // Queen is the pre selected
        );

        switch (choice) {
            case 0: return 'Q';
            case 1: return 'R';
            case 2: return 'B';
            case 3: return 'N';
            default: return ' ';
        }
    }
    
    /**
     * Adjusts the layout of the board panel to be viewed from White's perspective 
     * (Rank 1 at the bottom).
     */
    private void setWhiteVision() {
        this.blackPerspective = false;
        this.boardPanel.removeAll();
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                this.boardPanel.add(this.boardButtons[row][col]);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    
    /**
     * Adjusts the layout of the board panel to be viewed from Black's perspective 
     * (Rank 8 at the bottom).
     */
    private void setBlackVision() {
        this.blackPerspective = true;
        this.boardPanel.removeAll();
        for (int row = 0; row < 8; row++) {
            for (int col = 7; col >= 0; col--) {
                this.boardPanel.add(this.boardButtons[row][col]);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    
    /**
     * Toggles the current board perspective between White and Black views.
     */
    public void reverseBoardVision() {
        if(this.blackPerspective) {
            this.setWhiteVision();
        }else {
            this.setBlackVision();
        }
    }
    
    /**
     * Closes the current game window and opens the Main Menu window.
     */
    public void goToMenu() {
    	MenuWindow menuWindow = new MenuWindow();
		menuWindow.setVisible(true);
		this.dispose();
    }
    
    /**
     * Attempts to save the current game state via the {@link GameSaveFileManager} 
     * before navigating back to the main menu.
     * If saving fails, prompts the user to either abort or exit without saving.
     */
    private void saveAndGoToMenu() {
    	try {
    		GameSaveFileManager.saveGame(this.chessboard);
    		this.goToMenu();
    	}catch(SavingException e) {
    		int choice = JOptionPane.showOptionDialog(
    	            this,
    	            "Error while saving: " + e.getMessage() + "\n\nDo you want to exit without saving?",
    	            "Save Error",
    	            JOptionPane.YES_NO_OPTION,
    	            JOptionPane.ERROR_MESSAGE,
    	            null,
    	            new String[] {"Yes", "No"},
    	            "No" // "No" pre-selected, safer default
    	    );
    		if(choice == JOptionPane.YES_OPTION) {
    			this.goToMenu();
    		}
    	}
    }
    
    /**
     * Loads the user's saved preferences using the {@link SettingsFileManager}.
     * If loading fails, displays an error message and falls back to default settings.
     */
    private void loadSettings() {
    	try {
    		this.settings = SettingsFileManager.loadSettings();
    	}catch(LoadingException e) {
    		JOptionPane.showMessageDialog(
    			null,
    			"Error loading settings: " + e.getMessage(),
    			"Settings will be reset to default",
    			JOptionPane.ERROR_MESSAGE
    		);
    		this.settings = new Settings();
    	}
    }
    
}