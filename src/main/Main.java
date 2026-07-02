package main;

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
		
		
		
		
	}

}


 