package main;

import chessgame.*;

public class Main {

	public static void main(String[] args) {
		ChessBoard teste = BoardFactory.standartChessBoard();
		
		
		teste.move("c4");
		teste.move("d5");
		teste.move("cxd5");
		teste.undoMove();
		teste.move("c5");
		teste.move("b5");
		teste.move("cxb6");
		//teste.undoMove();
		//teste.move("a3");
		//teste.move("h6");
		//teste.move("cxb6");
		
		
		System.out.println(teste);
	}

}


 