package main;

import chessgame.*;

public class Main {

	public static void main(String[] args) {
		ChessBoard teste = BoardFactory.standartChessBoard();
		
		teste.move("c4");
		teste.move("c5");
		teste.move("g3");
		teste.move("Nc6");
		teste.move("Bg2");
		teste.move("g6");
		teste.move("Nc3");
		teste.move("Bg7");
		teste.move("d3");
		teste.move("Nf6");
		teste.move("Nf3");
		teste.undoMove();
		teste.undoMove();
		teste.undoMove();
		teste.undoMove();
		teste.undoMove();
		teste.move("Bxc6");
		teste.undoMove();
		teste.move("Nc3");
		teste.move("Na5");
		
		
		
		System.out.println(teste);
	}

}


 