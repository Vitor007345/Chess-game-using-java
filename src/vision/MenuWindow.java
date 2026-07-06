package vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import chessgame.BoardFactory;
import chessgame.ChessBoard;
import chessgame.errors.InvalidFENexception;
import services.GameSaveFileManager;
import services.errors.LoadingException;

public class MenuWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public MenuWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, 550, 650);
        this.setTitle("Java Chess Engine");

        
        this.contentPane = new JPanel();
        this.contentPane.setBackground(new Color(40, 44, 52));
        this.contentPane.setLayout(new BorderLayout());
        this.setContentPane(contentPane);

        
        JLabel lblTitle = new JLabel("JAVA CHESS", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 48));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0)); 
        this.contentPane.add(lblTitle, BorderLayout.NORTH);

        //central panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(40, 44, 52));
        
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        
        boolean hasSavedGame = GameSaveFileManager.hasSavedGame(); 

        JButton btnContinue = createBtnMenu("Continue Game");
        btnContinue.setVisible(hasSavedGame);

        JButton btnNewGame = createBtnMenu("New Game");
        JButton btnImportFen = createBtnMenu("Import Position");
        JButton btnSettings = createBtnMenu("Settings");
        JButton btnQuit = createBtnMenu("Quit");

        
        buttonPanel.add(Box.createVerticalGlue()); 
        
        if (hasSavedGame) {
            buttonPanel.add(btnContinue);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); 
        }
        
        buttonPanel.add(btnNewGame);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnImportFen);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnSettings);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnQuit);
        
        buttonPanel.add(Box.createVerticalGlue()); 

        this.contentPane.add(buttonPanel, BorderLayout.CENTER);

        //action listeners
        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	fbtnNewGame();
            }
        });

        btnImportFen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	fbtnImportFen();
            }
        });

        btnSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	fbtnSettings();
            }
        });

        btnContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 fbtnContinue();
            }
        });
        
        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 fbtnQuit();
            }
        });
    }

    //used to format identical btn
    private JButton createBtnMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        btn.setMaximumSize(new Dimension(250, 50)); 
        btn.setFocusPainted(false);
        return btn;
    }
    
    //btn functions
    private void fbtnNewGame() {
    	//Start the standard game (BoardFactory.standartChessBoard)
        ChessgameWindow game = new ChessgameWindow();
        game.setVisible(true);
        dispose(); // closes menu
    }
    private void fbtnImportFen() {
    	String fen = JOptionPane.showInputDialog(
                MenuWindow.this,
                "Paste the FEN string below:",
                "Import FEN Position",
                JOptionPane.QUESTION_MESSAGE
        );

        if (fen != null && !fen.trim().isEmpty()) {
            try {
                //try to create board from FEN
                ChessBoard boardFromFen = BoardFactory.chessBoardFromFEN(fen);
                
                //start the game
                ChessgameWindow game = new ChessgameWindow(boardFromFen);
                game.setVisible(true);
                dispose(); //closes menu
            } catch (InvalidFENexception e) {
                //if FEN is invalid show a popup with error msm
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), 
                    "Invalid FEN", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void fbtnSettings() {
    	//open settings page
    	SettingsWindow settingsWindow = new SettingsWindow();
    	settingsWindow.setVisible(true);
    	dispose();
    }
    
    private void fbtnContinue() {
    	try {
    		ChessgameWindow game = new ChessgameWindow(GameSaveFileManager.loadGame());
            game.setVisible(true);
            dispose(); // closes menu
    	}catch(LoadingException e) {
    		JOptionPane.showMessageDialog(this,
    				e.getMessage(),
    				"Error while loading", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    private void fbtnQuit() {
    	System.exit(0);
    }
    
}