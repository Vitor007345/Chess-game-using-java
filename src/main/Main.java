package main;
/*
import chessgame.*;
import chessgame.moves.MoveNotationError;
*/
import java.awt.EventQueue;
import java.util.Scanner;

import Vision.ChessgameWindow;


public class Main {
	
	public static final Scanner sc = new Scanner(System.in);

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
		
		/*
		ChessBoard teste = BoardFactory.standartChessBoard();
		
		while(teste.getResult() == null) {
			System.out.println(teste);
			try {
				System.out.println(teste.isWhiteToMove()? "White to play" : "Black to play");
				System.out.print("Type your movement: ");
				String moveStr = sc.nextLine();
				moveStr = moveStr.replace("\n", "");
				if(moveStr.equals("undo")) {
					teste.undoMove();
				}else {
					teste.move(moveStr);
				}
				
				
				
			}catch(MoveNotationError e){
				System.out.println(e.getInvalidInput() + " is an invalid move. " + e.getWhyIsInvalid());
			}
		}
		
		System.out.println(teste); 
	    
	    System.out.println("Fim de jogo! Resultado: " + teste.getResult());
		
		
		*/
		
		
	}

}


 