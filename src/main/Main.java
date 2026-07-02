package main;

import java.awt.EventQueue;
import java.util.Scanner;

import Vision.ChessgameWindow;
import chessgame.BoardFactory;


public class Main {
	
	public static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChessgameWindow frame = new ChessgameWindow(/*BoardFactory.chessBoardFromFEN("r1bqkbnr/pp1p1ppp/2n1p3/2p5/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 2 4")*/);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		
	}

}


 