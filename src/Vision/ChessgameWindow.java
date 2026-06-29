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

public class ChessgameWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JButton[][] boardButtons = new JButton[8][8];

	private JTextField txtMoveInput;
	private JButton btnSendMove;
	
	// Nossas novas Labels
	private JLabel lblTurn;
	private JLabel lblError;

	private Color lightSquare = new Color(180, 180, 180);
	private Color darkSquare = new Color(130, 127, 127);

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 650); // Aumentei um pouco mais a altura pras labels caberem com folga
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5)); 
		setContentPane(contentPane);

		// --- 1. TOPO (Label do Turno) ---
		lblTurn = new JLabel("White to play", SwingConstants.CENTER); // Já nasce centralizada
		lblTurn.setFont(new Font("Arial", Font.BOLD, 22));
		contentPane.add(lblTurn, BorderLayout.NORTH);

		// --- 2. CENTRO (Tabuleiro) ---
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(8, 8, 0, 0));
		contentPane.add(boardPanel, BorderLayout.CENTER);

		// --- 3. EMBAIXO (Input + Label de Erro) ---
		// Criei um "container" para a área sul para podermos empilhar o input e o erro
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
				String movimentoDigitado = txtMoveInput.getText().trim();
				if (!movimentoDigitado.isEmpty()) {
					movimentoTextoEnviado(movimentoDigitado);
					txtMoveInput.setText(""); 
				}
			}
		};
		
		btnSendMove.addActionListener(sendAction);
		txtMoveInput.addActionListener(sendAction); 
		
		inputPanel.add(txtMoveInput);
		inputPanel.add(btnSendMove);
		
		// Criando a Label de Erro (começa com um espaço vazio para não estragar o layout)
		lblError = new JLabel(" ", SwingConstants.CENTER);
		lblError.setForeground(Color.RED); // Cor vermelha para dar destaque
		lblError.setFont(new Font("Arial", Font.BOLD, 14));

		// Montando o bloco do Sul (Input em cima, Erro embaixo)
		southContainer.add(inputPanel, BorderLayout.CENTER);
		southContainer.add(lblError, BorderLayout.SOUTH);
		
		// Adiciona o container completo na parte de baixo da janela principal
		contentPane.add(southContainer, BorderLayout.SOUTH);

		// Inicializa os botões
		initializeBoard(boardPanel);
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
	}
	
	private void movimentoTextoEnviado(String movimento) {
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
}