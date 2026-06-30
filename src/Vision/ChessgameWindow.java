package Vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import chessgame.BoardFactory;
import chessgame.ChessBoard;
import chessgame.moves.MoveNotationError;
import chessgame.pieces.Piece;

public class ChessgameWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JButton[][] boardButtons = new JButton[8][8];

	private JTextField txtMoveInput;
	private JButton btnSendMove;
	
	private JLabel lblTurn;
	private JLabel lblError;

	private static final Color lightSquare = new Color(180, 180, 180);
	private static final Color darkSquare = new Color(130, 127, 127);
	
	private ChessBoard chessboard;
	private int selectedRow;
	private int selectedCol;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChessgameWindow frame = new ChessgameWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
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

		JPanel inputPanel = new JPanel(); 
		txtMoveInput = new JTextField(10); 
		txtMoveInput.setFont(new Font("Arial", Font.PLAIN, 18));
		
		btnSendMove = new JButton("Enviar");
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
		
		//Label to show errors
		lblError = new JLabel(" ", SwingConstants.CENTER);
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font("Arial", Font.BOLD, 14));

		
		southContainer.add(inputPanel, BorderLayout.CENTER);
		southContainer.add(lblError, BorderLayout.SOUTH);
		
		
		contentPane.add(southContainer, BorderLayout.SOUTH);

		this.initializeBoard(boardPanel);
		this.updateBoardOnScreen();
		this.updateTurnOnScreen();
		
		
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
						botaoClicado(logicalRow, logicalCol);
					}
				});

				boardButtons[row][col] = btn;
				boardPanel.add(btn);
			}
		}
	}

	private void botaoClicado(int row, int col) {
		System.out.println("Clique no tabuleiro -> Linha: " + row + " | Coluna: " + col);
		if (chessboard.getResult() != null) return;

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
		            //try to move, promotion locked into queen temporarily (do this mechanic later)
		            chessboard.move(selectedRow, selectedCol, row, col, 'Q');
		            
		            lblError.setText(" "); 
		            
		            //update screen
		            this.updateTurnOnScreen();
		            this.updateBoardOnScreen();
		            
		            if(chessboard.getResult() != null) {
		                lblTurn.setText("FIM DE JOGO! - Resultado: " + chessboard.getResult());
		            }
		            
		        } catch (MoveNotationError e) {
		            //Error IlegalMove
		            lblError.setText(e.getWhyIsInvalid());
		        } catch (Exception e) {
		            lblError.setText("Erro crítico: " + e.getMessage());
		        } finally {
		            //reset
		            this.resetSquareColor(selectedRow, selectedCol);
		            selectedRow = -1;
		            selectedCol = -1;
		        }
	    	}
	        
	    }
	}
	
	private void executeTypedMove(String movimento) {
		System.out.println("Movimento digitado: " + movimento);
		
		// Exemplo de como você vai usar as labels na prática com a sua Engine:
		
		// lblError.setText(" "); // Limpa o erro anterior logo que ele tenta um novo lance
		//
		// try {
		//     chessBoard.move(movimento);
		//     
		//     // Se deu certo, atualiza o turno:
		//     if(chessBoard.isWhiteToMove()) {
		//         lblTurn.setText("White to play");
		//     } else {
		//         lblTurn.setText("Black to play");
		//     }
		//
		//     atualizarIconsDoTabuleiro();
		//
		// } catch (MoveNotationError e) {
		//     // Se deu ruim, acende a label de erro vermelha lá embaixo:
		//     lblError.setText(e.getInvalidInput() + " is an invalid move: " + e.getWhyIsInvalid());
		// }
	}
	
	public void resetSquareColor(int row, int col) {
        if ((row + col) % 2 != 0) {
            boardButtons[row][col].setBackground(lightSquare);
        } else {
            boardButtons[row][col].setBackground(darkSquare);
        }
	}
	
	public void updateTurnOnScreen() {
		if(chessboard.isWhiteToMove()) {
            lblTurn.setText("White to play");
            
        } else {
            lblTurn.setText("Black to play");
            
        }
	}
	
	public void updateBoardOnScreen() {
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
	
}

