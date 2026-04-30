package chessgame;

import chessgame.pieces.*;

public class BoardFactory {
	
	
	public static ChessBoard standartChessBoard() {
		return new ChessBoard(getStandartInitalPos());
	}
	
	static Piece[][] getStandartInitalPos(){
		Piece[][] matrix = new Piece[8][8]; 
		//genareteInitialRooks(matrix);
		genareteInitialPawns(matrix);
		genareteInitialKnights(matrix);
		genareteInitialBishops(matrix);
		genareteInitialQueens(matrix);
		genareteInitialKings(matrix);
		return matrix;
	}
	
	/*
	private static Rook[] generateInitialRooks(Piece[][] matrix, boolean white) {
		int row = 0;
		if(!white) {
			row = 7;
		}
		matrix[row][0] = new Rook(row, 0, white);
		matrix[row][7] = new Rook(row, 7, white);
		
	}
	*/
	
	private static void genareteInitialPawns(Piece[][] matrix) {
		for(int i = 0; i < 8; i++) {
			matrix[1][i] = new Pawn(1, i, true);
			matrix[6][i] = new Pawn(6, i, false);
		}
	}
	
	private static void genareteInitialKnights(Piece[][] matrix) {
		matrix[0][1] = new Knight(0, 1, true);
		matrix[0][6] = new Knight(0, 6, true);
		
		matrix[7][1] = new Knight(7, 1, false);
		matrix[7][6] = new Knight(7, 6, false);
	}
	
	private static void genareteInitialBishops(Piece[][] matrix) {
		matrix[0][2] = new Bishop(0, 2, true);
		matrix[0][5] = new Bishop(0, 5, true);
		
		matrix[7][2] = new Bishop(7, 2, false);
		matrix[7][5] = new Bishop(7, 5, false);
	}
	
	private static void genareteInitialQueens(Piece[][] matrix) {
		matrix[0][3] = new Queen(0, 3, true);
		matrix[7][3] = new Queen(7, 3, false);
	}
	
	private static void genareteInitialKings(Piece[][] matrix) {
		matrix[0][4] = new King(0, 4, true);
		matrix[7][4] = new King(7, 4, false);
	}

	
}
