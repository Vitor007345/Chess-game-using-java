package main;

import chessgame.*;

public class Main {

	public static void main(String[] args) {
		ChessBoard teste = BoardFactory.standartChessBoard();
		
		
		teste.move("e4");
		teste.move("e5");
		teste.move("Ke2");
		teste.move("Ke7");
		
		
		System.out.println(teste);
	}

}


 