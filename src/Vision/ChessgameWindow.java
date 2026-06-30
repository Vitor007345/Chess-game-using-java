package Vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import chessgame.moves.MoveNotationError;
import chessgame.pieces.Pawn;
import chessgame.pieces.Piece;

public class ChessgameWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JButton[][] boardButtons = new JButton[8][8];

	private JTextField txtMoveInput;
	private JButton btnSendMove;
	private JButton btnUndoMove;
	
	private JLabel lblTurn;
	private JLabel lblError;

	private static final Color lightSquare = new Color(180, 180, 180);
	private static final Color darkSquare = new Color(130, 127, 127);
	
	private ChessBoard chessboard;
	private int selectedRow;
	private int selectedCol;
	
	public ChessgameWindow() {
		this(BoardFactory.standartChessBoard());
	}
	
	public ChessgameWindow(ChessBoard chessboard) {
		this.chessboard = chessboard;
		this.selectedRow = -1;
		this.selectedCol = -1;
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 650);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5)); 
		setContentPane(contentPane);

		//top
		lblTurn = new JLabel("White to play", SwingConstants.CENTER);
		lblTurn.setFont(new Font("Arial", Font.BOLD, 22));
		contentPane.add(lblTurn, BorderLayout.NORTH);

		//center
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(8, 8, 0, 0));
		contentPane.add(boardPanel, BorderLayout.CENTER);

		//bottom
		JPanel southContainer = new JPanel();
		southContainer.setLayout(new BorderLayout());
		
		//input Panel
		JPanel inputPanel = new JPanel(); 
		txtMoveInput = new JTextField(10); 
		txtMoveInput.setFont(new Font("Arial", Font.PLAIN, 18));
		
		btnSendMove = new JButton("Send move");
		btnSendMove.setFont(new Font("Arial", Font.BOLD, 14));
		
		ActionListener sendAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String typedMove = txtMoveInput.getText().trim();
				if (!typedMove.isEmpty()) {
					executeTypedMove(typedMove);
					txtMoveInput.setText(""); 
				}
			}
		};
		
		btnSendMove.addActionListener(sendAction);
		txtMoveInput.addActionListener(sendAction); 
		
		
		
		inputPanel.add(txtMoveInput);
		inputPanel.add(btnSendMove);
		
		//control panel
		JPanel controlPanel = new JPanel();
		
		btnUndoMove = new JButton("Undo move");
		btnUndoMove.setFont(new Font("Arial", Font.BOLD, 14));
		
		
		ActionListener undoMove = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undoMove();
			}
		};
		
		btnUndoMove.addActionListener(undoMove);
		
		controlPanel.add(btnUndoMove);
		
		//Label to show errors
		lblError = new JLabel(" ", SwingConstants.CENTER);
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font("Arial", Font.BOLD, 14));

		
		southContainer.add(inputPanel, BorderLayout.NORTH);
		southContainer.add(controlPanel, BorderLayout.CENTER);
		southContainer.add(lblError, BorderLayout.SOUTH);
		
		
		contentPane.add(southContainer, BorderLayout.SOUTH);

		this.initializeBoard(boardPanel);
		this.updateScreen();
		
		
	}

	private void initializeBoard(JPanel boardPanel) {
		for (int row = 7; row >= 0; row--) {
			for (int col = 0; col < 8; col++) {
				
				final int logicalRow = row; 
				final int logicalCol = col;

				JButton btn = new JButton("");
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
						btnPressed(logicalRow, logicalCol);
					}
				});

				boardButtons[row][col] = btn;
				boardPanel.add(btn);
			}
		}
	}

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
			            
			            
			        } catch (MoveNotationError e) {
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
	
	private void executeTypedMove(String move) {
		System.out.println("Movimento digitado: " + move);
		
		try {
			this.chessboard.move(move);
			lblError.setText(" ");
			this.updateScreen();
			
			
		} catch (MoveNotationError e) {
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
	
	private void resetSquareColor(int row, int col) {
        if ((row + col) % 2 != 0) {
            boardButtons[row][col].setBackground(lightSquare);
        } else {
            boardButtons[row][col].setBackground(darkSquare);
        }
	}
	
	private void updateTurnOnScreen() {
		if(chessboard.isWhiteToMove()) {
            lblTurn.setText("White to play");
            
        } else {
            lblTurn.setText("Black to play");
            
        }
	}
	
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
	
	private void updateResultOnScreen() {
		if(chessboard.getResult() != null) {
            lblTurn.setText("FIM DE JOGO! - Resultado: " + chessboard.getResult());
        }
	}
	
	private void updateScreen() {
		this.updateTurnOnScreen();
		this.updateBoardOnScreen();
		this.updateResultOnScreen();
	}
	
	private void undoMove() {
		if(this.chessboard.undoMove()) {
			lblError.setText(" ");
			this.updateScreen();
			
		}else {
			lblError.setText("Can't undo more, because there are no more moves saved");
		}
	}
	
	private char getPromoPiece() {
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
}

