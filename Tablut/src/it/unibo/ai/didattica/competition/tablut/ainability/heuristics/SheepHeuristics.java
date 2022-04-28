package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.ainability.domain.Position;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class SheepHeuristics extends Heuristics {
	private State state;

	//private static double NUM_BLACK = 16;
	//private static double NUM_WHITE = 9;
	
	//WEIGHT:
	private static double WEIGHT_WHITE_PAWNS = 50;
	private static double WEIGHT_BLACK_PAWNS = -5;
	private static double WEIGHT_VICTORY = Double.POSITIVE_INFINITY;
	private static double WEIGHT_KING_IS_SAFE = 100;
	//private static double WEIGHT_KING_ON_THRONE = 0;
	//private static double WEIGHT_KING_NEAR_THRONE = 0;
	// private static double WEIGHT_KING_NEAR_CITADEL=0;//Put this or consider the
	// citadel as a black pawn?
	private static double WEIGHT_WINNING_ROW_COLUMN = 60;// row or colomn that take the king to win
	private static double WEIGHT_KING_WAY_TO_ESCAPE = 40;// Decidere se fare proporzionale quindi 1 via *1 , 2 vie *2
														// oppure dare un peso
	// diverso in base alla singola via libera o a 2 o più vie libere
	//private static double WEIGHT_BLACK_EATEN = 30;

	private int currentNumberOfWhite;
	private int currentNumberOfBlack;
	private Position kingPosition;

	public SheepHeuristics(State state) {
		super(state);
		this.state = state;
		currentNumberOfWhite = 0;
		currentNumberOfBlack = 0;
		kingPosition = new Position(5, 5);
	}

	// In un'unica funzione troviamo la posizione del re, delle pedine bianche e
	// delle nere.
	/*
	 * this method set the position of the king in the board and the number of
	 * white/back pawns
	 * 
	 * @param none
	 * 
	 * @return void
	 */
	public void kingPositionAndNumberPawns() {
		boolean foundKing = false;
		for (int i = 0; i < this.state.getBoard()[0].length; i++) {
			for (int j = 0; j < this.state.getBoard()[0].length; j++) {
				if (!foundKing && this.state.getBoard()[i][j].equals(State.Pawn.KING)) { 
																							
				
					int row = i + 1; // +1 is necessary because we use 1-9 notation
					int column = j + 1; // +1 is necessary because we use 1-9 notation
					kingPosition = new Position(row, column);
					foundKing = true;
				}
				if (this.state.getBoard()[i][j].equals(State.Pawn.BLACK)) {
					currentNumberOfBlack++;
				} else if (this.state.getBoard()[i][j].equals(State.Pawn.WHITE)) {
					currentNumberOfWhite++;
				}
			}
		}
	}



	
	/**
	 * this function checks if the king is protected in case he is surrounded by
	 * black pawns
	 * 
	 * @return
	 */
	private double kingSafe() {

		int result = 0;

		int kingRow = kingPosition.getRow();
		int kingColumn = kingPosition.getColumn();

		if ((kingRow == 5 && kingColumn >= 4 && kingColumn <= 6) || (kingColumn == 5 && kingRow >= 4 && kingRow <= 6)) // nella
																														// croce
																														// con
																														// centro
																														// trono
																														// e
																														// lunga
																														// 3x3
		{
			// il re è sul trono e un bianco lo protegge:
			if (state.getBoard()[kingRow - 1 - 1][kingColumn - 1].equalsPawn(State.Pawn.WHITE.toString()) || // sopra
					state.getBoard()[kingRow - 1 + 1][kingColumn - 1].equalsPawn(State.Pawn.WHITE.toString()) || // sotto
					state.getBoard()[kingRow - 1][kingColumn - 1 + 1].equalsPawn(State.Pawn.WHITE.toString()) || // dx
					state.getBoard()[kingRow - 1][kingColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) // sx
				result += 0.5; // lontano dall'obbiettivo
		} else { // ne servono 2 per magiare il re e il re è coperto:
			Pawn[][] pawns = state.getBoard();
			// non copri la stella oppure nemico sopra e sei coperto sotto:
			if (kingRow - 1 - 1 >= 0 && kingRow - 1 + 1 <=8 && (kingRow + 1) != 9
					&& (pawns[kingRow - 1 - 1][kingColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow - 1, kingColumn))
					&& pawns[kingRow - 1 + 1][kingColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sotto e sei coperto sopra:
			if (kingRow - 1 + 1 <=8 && kingRow - 1 - 1 >=0 && kingRow - 1 != 1
					&& (pawns[kingRow - 1 + 1][kingColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow + 1, kingColumn))
					&& pawns[kingRow - 1 - 1][kingColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}

			// non copri la stella oppure nemico dx e sei coperto sx:
			if (kingColumn - 1 + 1 <=8 && kingColumn - 1 - 1 >=0 && kingColumn - 1 != 1
					&& (pawns[kingRow - 1][kingColumn - 1 + 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow, kingColumn + 1))
					&& pawns[kingRow - 1][kingColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sx e sei coperto dx:
			if (kingColumn - 1 - 1 >=0 && kingColumn - 1 + 1 <=8 && kingColumn + 1 != 9
					&& (pawns[kingRow - 1][kingColumn - 1 - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow, kingColumn - 1))
					&& pawns[kingRow - 1][kingColumn - 1 + 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}

		}
		return result;

	}

	private boolean noPawnsInRow(int line) {

		int i;
		for (i = 0; i < 9; i++) {
			if (!state.getPawn(line, i).equalsPawn(State.Pawn.EMPTY.toString())) {
				return false;
			}
		}
		return true;

	}

	private boolean noPawnsInColumn(int line) {

		int i;
		for (i = 0; i < 9; i++) {
			if (!state.getPawn(i, line).equalsPawn(State.Pawn.EMPTY.toString())) {
				return false;
			}
		}
		return true;

	}

	private boolean kingArriveColumn(int line) {

		int i;

		int kingRow = kingPosition.getRow();
		int kingColumn = kingPosition.getColumn();

		if (kingColumn < line) {
			for (i = kingColumn; i < line; i++) {
				if (!state.getPawn(kingRow, i).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}
		} else {
			for (i = line+1; i < kingColumn-1; i++) {
				if (!state.getPawn(kingRow, i).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}

		}
		return true;

	}
	
	
	private boolean kingArriveRow(int line) {

		int i;

		int kingRow = kingPosition.getRow();
		int kingColumn = kingPosition.getColumn();

		if (kingRow < line) {
			for (i = kingRow; i < line; i++) {
				if (!state.getPawn(i, kingColumn).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}
		} else {
			for (i = line+1; i < kingRow-1; i++) {
				if (!state.getPawn(i, kingColumn).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}

		}
		return true;

	}

	private int winningRowColumn() {

		int result = 0;

		// noPawnsInRow follow the standard 0-8 and kingPosition follow the standard 1-9
		if ((noPawnsInRow(2) && this.kingPosition.getRow() ==3 ) || (noPawnsInRow(6)&& this.kingPosition.getRow()==7) ||
			(noPawnsInColumn(2) && this.kingPosition.getColumn()==3) || (noPawnsInColumn(6) && this.kingPosition.getColumn()==7) 
				) {
			return 10;
		}
		
	


		if (noPawnsInRow(2) && kingArriveRow(2)) {
			result+=1;
		}
		if (noPawnsInRow(6)&& kingArriveRow(6)) {
			result+=1;
		}
		if (noPawnsInColumn(2) && kingArriveColumn(2)) {
			result+=1;
		}
		if (noPawnsInColumn(6) && kingArriveColumn(6)) {
			result+=1;
		}

		return result;

	}
	
	

	@Override
	public double evaluateState() {
		
		
		double result = 0.0;
		if (state.getTurn().equalsTurn(State.Turn.WHITEWIN.toString())) {
			result += WEIGHT_VICTORY;
			return result;
		}
		// init
		kingPositionAndNumberPawns();
		
		
		result += WEIGHT_WHITE_PAWNS * this.currentNumberOfWhite 
				+ WEIGHT_KING_IS_SAFE * kingSafe() + WEIGHT_KING_WAY_TO_ESCAPE + NumberOfKingRowColFree(this.kingPosition) 
				+ WEIGHT_WINNING_ROW_COLUMN* winningRowColumn()+ WEIGHT_BLACK_PAWNS* this.currentNumberOfBlack ;

		/**
		 * !!!!!!!!!!!!!!!!!!!!!!!!
		 *  AGGIUNGERE FUNZIONE CHE MANGIA NERI E BESTPOSITION? !!!!!
		 *  
		 */
		
		System.out.println("\n!!!!!!!!!!!!!SHeep.... "+result);
		return result;
	}

}
