package chessgame;

import chessgame.pieces.*;

class MoveFactory {
	/*
	static Move createMove(String moveStr, ChessBoard board) {
		//logic for castle
		switch(moveStr) {
			case "o-o-o":
			case "o-o":
				break;
			default:{
				int lastIndex = moveStr.length() - 1;
				char[] moveStrchar = moveStr.toCharArray();
				char lastChar = moveStrchar[lastIndex];
				boolean checkOrCheckmate = false;
				if(lastChar == '+' || lastChar == '#') {
					checkOrCheckmate = true;
					lastIndex -= 1;
				}
				byte row = getRow(moveStrchar[lastIndex -1], moveStr);
				byte col = getCol(moveStrchar[lastIndex], moveStr);
				lastIndex -= 1;
				boolean capture = false;
				if(moveStrchar[lastIndex] == 'x') {
					lastIndex -= 1;
					capture = true;
				}
				
				
				
				
				
			}
		}
	}
	
	//position getters
	private static byte getRow(char rowChar, String moveStr) {
		int row = (int)(rowChar - 'a');
		if(row < 0 || row > 7)throw new MoveNotationError(moveStr, "invalid row");
		return (byte)row;
	}
	
	private static byte getCol(char colChar, String moveStr) {
		int col = (int)(colChar - '1');
		if(col < 0 || col > 7)throw new MoveNotationError(moveStr, "invalid col");
		return (byte)col;
	}
	
	
	
	
	private static boolean isValid(Piece piece, byte targetRow, byte targetCol, Piece[][] board) {
		
	}
	
	
	private static boolean pawnLogic(Pawn pawn, int targetRow, int targetCol, Piece[][] board) {
		
	}
	*/
	
}
