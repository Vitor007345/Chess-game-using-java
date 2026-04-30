package chessgame;

import chessgame.pieces.*;
import java.util.ArrayList;

public class ChessBoard {
	//atributes
	private Piece[][] board;
	
	private ArrayList<Rook> whiteRooks = new ArrayList<>();
    private ArrayList<Rook> blackRooks = new ArrayList<>();

    private ArrayList<Pawn> whitePawns = new ArrayList<>();
    private ArrayList<Pawn> blackPawns = new ArrayList<>();

    private ArrayList<Knight> whiteKnights = new ArrayList<>();
    private ArrayList<Knight> blackKnights = new ArrayList<>();

    private ArrayList<Bishop> whiteBishops = new ArrayList<>();
    private ArrayList<Bishop> blackBishops = new ArrayList<>();

    private ArrayList<Queen> whiteQueens = new ArrayList<>();
    private ArrayList<Queen> blackQueens = new ArrayList<>();

    private King whiteKing;
    private King blackKing;
    
    //constructors
    
	ChessBoard(Piece[][] board, ArrayList<Rook> whiteRooks, ArrayList<Rook> blackRooks,
			ArrayList<Pawn> whitePawns, ArrayList<Pawn> blackPawns, ArrayList<Knight> whiteKnights,
			ArrayList<Knight> blackKnights, ArrayList<Bishop> whiteBishops, ArrayList<Bishop> blackBishops,
			ArrayList<Queen> whiteQueens, ArrayList<Queen> blackQueens, King whiteKing, King blackKing) {
		this.board = board;
		this.whiteRooks = whiteRooks;
		this.blackRooks = blackRooks;
		this.whitePawns = whitePawns;
		this.blackPawns = blackPawns;
		this.whiteKnights = whiteKnights;
		this.blackKnights = blackKnights;
		this.whiteBishops = whiteBishops;
		this.blackBishops = blackBishops;
		this.whiteQueens = whiteQueens;
		this.blackQueens = blackQueens;
		this.whiteKing = whiteKing;
		this.blackKing = blackKing;
	}
    
	
    
    
    
	
	
	
	
	
	
	
	
	
}
