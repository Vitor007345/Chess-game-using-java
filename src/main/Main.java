package main;

import chessgame.*;

public class Main {

	public static void main(String[] args) {
		ChessBoard teste = BoardFactory.standartChessBoard();
		System.out.println(teste);
		teste.move("c4");
		teste.move("e5");
		teste.move("Nf3");
		teste.move("d5");
		teste.move("Nxe5");
		teste.undoMove();
		teste.move("a3");
		teste.move("e4");
		
		System.out.println(teste);
		

	}

}
