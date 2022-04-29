package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;


import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class SheepHeuristics extends Heuristics {
	private State state;

	private static int NUM_BLACK = 16;
	//private static int NUM_WHITE = 9;
	
	//WEIGHT:
	/*private static int WEIGHT_WHITE_PAWNS = 70;
	private static int WEIGHT_BLACK_PAWNS = 30;
	private static int WEIGHT_WINNING_ROW_COLUMN = 20;// row or column that take the king to win
	private static int WEIGHT_KING_IS_SAFE = 100;
	*/
	//WEIGHT: luca
	/*private static int WEIGHT_WHITE_PAWNS = 70;
	private static int WEIGHT_BLACK_PAWNS = 30;
	private static int WEIGHT_WINNING_ROW_COLUMN = 60;// row or column that take the king to win
	private static int WEIGHT_KING_IS_SAFE = 50;*/
	//WEIGHT:peppe
		private static int WEIGHT_WHITE_PAWNS = 75;
		private static int WEIGHT_BLACK_PAWNS = 20;
		private static int WEIGHT_WINNING_ROW_COLUMN = 50;// row or column that take the king to win
		private static int WEIGHT_KING_IS_SAFE = 40;
	
	
	//private static int WEIGHT_KING_WAY_TO_ESCAPE = 10;// Decidere se fare proporzionale quindi 1 via *1 , 2 vie *2
	// oppure dare un peso

	//private static int WEIGHT_KING_ON_THRONE = 0;
	//private static int WEIGHT_KING_NEAR_THRONE = 0;
	// private static int WEIGHT_KING_NEAR_CITADEL=0;//Put this or consider the
	// citadel as a black pawn?
	// diverso in base alla singola via libera o a 2 o più vie libere
	//private static int WEIGHT_BLACK_EATEN = 30;


	@Override
	public int evaluateState() {
		
		
		int result = 0;
		
		// init
		kingPositionAndNumberPawns();
		
		result += WEIGHT_WHITE_PAWNS * this.currentNumberOfWhite 
				+ WEIGHT_KING_IS_SAFE * kingSafe()
				+ WEIGHT_WINNING_ROW_COLUMN* winningRowColumn()
				+ WEIGHT_BLACK_PAWNS* (NUM_BLACK-this.currentNumberOfBlack);

		
		
		return result;
	}

	 
	private int currentNumberOfWhite;
	private int currentNumberOfBlack;
	private int kingPositionRow;
	private int kingPositionColumn;

	public SheepHeuristics(State state) {
		super(state);
		this.state = state;
		
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
																							
				
					kingPositionRow = i + 1; // +1 is necessary because we use 1-9 notation
					kingPositionColumn = j + 1; // +1 is necessary because we use 1-9 notation
					
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
	private int kingSafe() {

		int result = 0;

	

		Pawn[][] pawns = state.getBoard();
		
		if ((kingPositionRow == 5 && kingPositionColumn >= 4 && kingPositionColumn <= 6) || (kingPositionColumn == 5 && kingPositionRow >= 4 && kingPositionRow <= 6)) 	
		{
			// il re è sul trono e un bianco lo protegge:
			if (pawns[kingPositionRow - 1 - 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString()) || // sopra
					pawns[kingPositionRow - 1 + 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString()) || // sotto
					pawns[kingPositionRow - 1][kingPositionColumn - 1 + 1].equalsPawn(State.Pawn.WHITE.toString()) || // dx
					pawns[kingPositionRow - 1][kingPositionColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) // sx
				result += 1; 
		} else { // ne servono 2 per magiare il re e il re è coperto:
			
			// non copri la stella oppure nemico sopra e sei coperto sotto:
			if (kingPositionRow - 1 - 1 >= 0 && kingPositionRow - 1 + 1 <=8 && (kingPositionRow + 1) != 9
					&& (pawns[kingPositionRow - 1 - 1][kingPositionColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow - 1, kingPositionColumn))
					&& pawns[kingPositionRow - 1 + 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sotto e sei coperto sopra:
			if (kingPositionRow - 1 + 1 <=8 && kingPositionRow - 1 - 1 >=0 && kingPositionRow - 1 != 1
					&& (pawns[kingPositionRow - 1 + 1][kingPositionColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow + 1, kingPositionColumn))
					&& pawns[kingPositionRow - 1 - 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}

			// non copri la stella oppure nemico dx e sei coperto sx:
			if (kingPositionColumn - 1 + 1 <=8 && kingPositionColumn - 1 - 1 >=0 && kingPositionColumn - 1 != 1
					&& (pawns[kingPositionRow - 1][kingPositionColumn - 1 + 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow, kingPositionColumn + 1))
					&& pawns[kingPositionRow - 1][kingPositionColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sx e sei coperto dx:
			if (kingPositionColumn - 1 - 1 >=0 && kingPositionColumn - 1 + 1 <=8 && kingPositionColumn + 1 != 9
					&& (pawns[kingPositionRow - 1][kingPositionColumn - 1 - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow, kingPositionColumn - 1))
					&& pawns[kingPositionRow - 1][kingPositionColumn - 1 + 1].equalsPawn(State.Pawn.WHITE.toString())) {
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

		

		if (kingPositionColumn < line) {
			for (i = kingPositionColumn; i < line; i++) {
				if (!state.getPawn(kingPositionRow, i).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}
		} else {
			for (i = line+1; i < kingPositionColumn-1; i++) {
				if (!state.getPawn(kingPositionRow, i).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}

		}
		return true;

	}
	
	
	private boolean kingArriveRow(int line) {

		int i;

		

		if (kingPositionRow < line) {
			for (i = kingPositionRow; i < line; i++) {
				if (!state.getPawn(i, kingPositionColumn).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}
		} else {
			for (i = line+1; i < kingPositionRow-1; i++) {
				if (!state.getPawn(i, kingPositionColumn).equalsPawn(State.Pawn.EMPTY.toString())) {
					return false;
				}
			}

		}
		return true;

	}

	private int winningRowColumn() {

		int result = 0;

		// noPawnsInRow follow the standard 0-8 and kingPosition follow the standard 1-9
		if ((noPawnsInRow(2) && this.kingPositionRow ==3 ) || (noPawnsInRow(6)&& this.kingPositionRow==7) ||
			(noPawnsInColumn(2) && this.kingPositionColumn==3) || (noPawnsInColumn(6) && this.kingPositionColumn==7) 
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
	
	/**
	 * this method calculate the number of free ways that the king can use to escape 
	 * @return number of free ways
	 */

	/*private int NumberOfKingFreeWays() {
		//Position kingPosition = this.getKingPosition();
		int freeWays =0;
		//check north
		freeWays++;		
		for(int i = kingPosition.getRow()-1;i>1;i--) {
			if(! state.getPawn(i-1, kingPosition.getColumn()-1).equalsPawn(State.Pawn.EMPTY.toString()) || //match if a pawn is found on the way to liberty
				this.isPositionCitadel(new Position(i-1,kingPosition.getColumn()-1)) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		//check south
		freeWays++;
		for(int i = kingPosition.getRow()+1;i<9;i++) {
			if(! state.getPawn(i-1, kingPosition.getColumn()-1).equalsPawn(State.Pawn.EMPTY.toString())|| //match if a pawn is found on the way to liberty
					this.isPositionCitadel(new Position(i-1,kingPosition.getColumn()-1)) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		//check west
		freeWays++;
		for(int i = kingPosition.getColumn()-1;i>1;i--) {
			if(! state.getPawn(kingPosition.getRow()-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())|| //match if a pawn is found on the way to liberty
					this.isPositionCitadel(new Position(kingPosition.getRow()-1,i-1)) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		//check east
		freeWays++;
		for(int i = kingPosition.getColumn()+1;i<9;i++) {
			if(! state.getPawn(kingPosition.getRow()-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())|| //match if a pawn is found on the way to liberty
					this.isPositionCitadel(new Position(kingPosition.getRow()-1,i-1)) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		
		return freeWays;	
	}
	*/

}
