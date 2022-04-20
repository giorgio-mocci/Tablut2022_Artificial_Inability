package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

<<<<<<< Updated upstream
=======
import it.unibo.ai.didattica.competition.tablut.ainability.domain.Position;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
>>>>>>> Stashed changes
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class SheepHeuristics extends Heuristics{
	private State state;

<<<<<<< Updated upstream
	public SheepHeuristics(State state) {
		super(state);
=======
	private static double NUM_BLACK = 16;
	private static double NUM_WHITE = 9;
	
	private static double WEIGHT_WHITE_PAWNS = 0;
	private static double WEIGHT_BLACK_PAWNS = 0;
	private static double WEIGHT_VICTORY = Double.POSITIVE_INFINITY;
	private static double WEIGHT_KING_IS_SAFE = 0;
	private static double WEIGHT_KING_ON_THRONE = 0;
	private static double WEIGHT_KING_NEAR_THRONE = 0;
	// private static double WEIGHT_KING_NEAR_CITADEL=0;//Put this or consider the
	// citadel as a black pawn?
	private static double WEIGHT_WINNING_ROW_COLUMN = 0;// row or colomn that take the king to win
	private static double WEIGHT_KING_WAY_TO_ESCAPE = 0;// Decidere se fare proporzionale quindi 1 via *1 , 2 vie *2
														// oppure dare un peso
	// diverso in base alla singola via libera o a 2 o più vie libere
	private static double WEIGHT_BLACK_EATEN = 0;

	private int currentNumberOfWhite;
	private int currentNumberOfBlack;
	private Position kingPosition;

	public SheepHeuristics(State state) {
		super(state);
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
		int row,column;
		for (int i = 0; i < this.state.getBoard()[0].length; i++) {
			for (int j = 0; j < this.state.getBoard()[0].length; j++) {
				if (!foundKing && this.state.getBoard()[i][j].equals(State.Pawn.KING)) { // MATCH! usiamo la found per
																							// saltare il controllo
																							// sulla board
					// nel caso il re sia già stato trovato
					 row = i + 1; // +1 is necessary because we use 1-9 notation
					 column = j + 1; // +1 is necessary because we use 1-9 notation
					kingPosition = new Position(i, j);
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
	 * this method calculate the number of free Row/column that the king can use to
	 * move
	 * 
	 * @return number of free row/column
	 */
	// DA TESTARE PER BENE CON DELLE STAMPE DURANTE IL TEST!!!!
	private int NumberOfKingRowColFree() {

		int freeLine = 0;
		// check north
		freeLine++;
		for (int i = kingPosition.getRow() - 1; i > 1; i--) {
			if (!state.getPawn(i - 1, kingPosition.getColumn() - 1).equalsPawn(State.Pawn.EMPTY.toString())) { // match
																												// se
																												// trovo
																												// una
																												// posizione
																												// con
																												// una
																												// pedina
				freeLine--;
				break;
			}
		}
		// check south
		freeLine++;
		for (int i = kingPosition.getRow() + 1; i < 9; i++) {
			if (!state.getPawn(i - 1, kingPosition.getColumn() - 1).equalsPawn(State.Pawn.EMPTY.toString())) { // match
																												// se
																												// trovo
																												// una
																												// posizione
																												// con
																												// una
																												// pedina
				freeLine--;
				break;
			}
		}
		// check west
		freeLine++;
		for (int i = kingPosition.getColumn() - 1; i > 1; i--) {
			if (!state.getPawn(kingPosition.getRow() - 1, i - 1).equalsPawn(State.Pawn.EMPTY.toString())) { // match se
																											// trovo una
																											// posizione
																											// con una
																											// pedina
				freeLine--;
				break;
			}
		}
		// check east
		freeLine++;
		for (int i = kingPosition.getColumn() + 1; i < 9; i++) {
			if (!state.getPawn(kingPosition.getRow() - 1, i - 1).equalsPawn(State.Pawn.EMPTY.toString())) { // match se
																											// trovo una
																											// posizione
																											// con una
																											// pedina
				freeLine--;
				break;
			}
		}

		return freeLine;
	}

	private int NumberOfBlackNearKing() {
		int number = 0;

		// check north
		if (state.getBoard()[kingPosition.getRow() - 1 - 1][kingPosition.getColumn() - 1]
				.equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		// check south
		if (state.getBoard()[kingPosition.getRow() - 1 + 1][kingPosition.getColumn() - 1]
				.equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		// check east
		if (state.getBoard()[kingPosition.getRow() - 1][kingPosition.getColumn() - 1 + 1]
				.equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		// check west
		if (state.getBoard()[kingPosition.getRow() - 1][kingPosition.getColumn() - 1 - 1]
				.equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		return number;
	}

	/*
	 * This method check if a given position is a citadel or not
	 * 
	 * @param pos position to check
	 * 
	 * @return boolean value that state if the position is a citadel or not
	 * 
	 */

	public boolean isPositionCitadel(Position pos) {
		int rowToCheck = pos.getRow();
		int columnToCheck = pos.getColumn();
		// Check northen citadels
		if (rowToCheck == 1 && columnToCheck == 4)
			return true;
		if (rowToCheck == 1 && columnToCheck == 5)
			return true;
		if (rowToCheck == 1 && columnToCheck == 6)
			return true;
		if (rowToCheck == 2 && columnToCheck == 5)
			return true;

		// Check eastern citadels
		if (rowToCheck == 4 && columnToCheck == 1)
			return true;
		if (rowToCheck == 5 && columnToCheck == 1)
			return true;
		if (rowToCheck == 6 && columnToCheck == 1)
			return true;
		if (rowToCheck == 5 && columnToCheck == 2)
			return true;

		// Check western citadels
		if (rowToCheck == 4 && columnToCheck == 9)
			return true;
		if (rowToCheck == 5 && columnToCheck == 9)
			return true;
		if (rowToCheck == 6 && columnToCheck == 9)
			return true;
		if (rowToCheck == 5 && columnToCheck == 8)
			return true;

		// Check southern citadels
		if (rowToCheck == 9 && columnToCheck == 4)
			return true;
		if (rowToCheck == 9 && columnToCheck == 5)
			return true;
		if (rowToCheck == 9 && columnToCheck == 6)
			return true;
		if (rowToCheck == 8 && columnToCheck == 5)
			return true;

		return false;
	}

	public boolean isPositionCitadel(int rowToCheck, int columnToCheck) {

		// Check northen citadels
		if (rowToCheck == 1 && columnToCheck == 4)
			return true;
		if (rowToCheck == 1 && columnToCheck == 5)
			return true;
		if (rowToCheck == 1 && columnToCheck == 6)
			return true;
		if (rowToCheck == 2 && columnToCheck == 5)
			return true;

		// Check eastern citadels
		if (rowToCheck == 4 && columnToCheck == 1)
			return true;
		if (rowToCheck == 5 && columnToCheck == 1)
			return true;
		if (rowToCheck == 6 && columnToCheck == 1)
			return true;
		if (rowToCheck == 5 && columnToCheck == 2)
			return true;

		// Check western citadels
		if (rowToCheck == 4 && columnToCheck == 9)
			return true;
		if (rowToCheck == 5 && columnToCheck == 9)
			return true;
		if (rowToCheck == 6 && columnToCheck == 9)
			return true;
		if (rowToCheck == 5 && columnToCheck == 8)
			return true;

		// Check southern citadels
		if (rowToCheck == 9 && columnToCheck == 4)
			return true;
		if (rowToCheck == 9 && columnToCheck == 5)
			return true;
		if (rowToCheck == 9 && columnToCheck == 6)
			return true;
		if (rowToCheck == 8 && columnToCheck == 5)
			return true;

		return false;
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
			Pawn[][] powns = state.getBoard();
			// non copri la stella oppure nemico sopra e sei coperto sotto:
			if ((kingRow + 1) != 9
					&& (powns[kingRow - 1 - 1][kingColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow - 1, kingColumn))
					&& powns[kingRow - 1 + 1][kingColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sotto e sei coperto sopra:
			if (kingRow - 1 != 1
					&& (powns[kingRow - 1 + 1][kingColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow + 1, kingColumn))
					&& powns[kingRow - 1 - 1][kingColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}

			// non copri la stella oppure nemico dx e sei coperto sx:
			if (kingColumn - 1 != 1
					&& (powns[kingRow - 1][kingColumn - 1 + 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow, kingColumn + 1))
					&& powns[kingRow - 1][kingColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sx e sei coperto dx:
			if (kingColumn + 1 != 9
					&& (powns[kingRow - 1][kingColumn - 1 - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingRow, kingColumn - 1))
					&& powns[kingRow - 1][kingColumn - 1 + 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}

		}
		return result;

	}

	private boolean noPownsInRow(int line) {

		int i;
		for (i = 0; i < 9; i++) {
			if (!state.getPawn(line, i).equalsPawn(State.Pawn.EMPTY.toString())) {
				return false;
			}
		}
		return true;

	}

	private boolean noPownsInColumn(int line) {

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

		

		if (noPownsInRow(2) && kingArriveRow(2)) {
			result+=1;
		}
		if (noPownsInRow(6)&& kingArriveRow(6)) {
			result+=1;
		}
		if (noPownsInColumn(2) && kingArriveColumn(2)) {
			result+=1;
		}
		if (noPownsInColumn(6) && kingArriveColumn(6)) {
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
		double numberOfBlackEaten = (double)(this.NUM_BLACK - this.currentNumberOfBlack) / this.NUM_BLACK;
		// init
		kingPositionAndNumberPawns();
		result += WEIGHT_WHITE_PAWNS * this.currentNumberOfWhite + WEIGHT_BLACK_PAWNS * this.currentNumberOfBlack
				+ WEIGHT_KING_IS_SAFE * kingSafe() + WEIGHT_KING_WAY_TO_ESCAPE + NumberOfKingRowColFree() 
				+ WEIGHT_WINNING_ROW_COLUMN* winningRowColumn() + WEIGHT_BLACK_EATEN * numberOfBlackEaten ;

		/**
		 * !!!!!!!!!!!!!!!!!!!!!!!!
		 *  AGGIUNGERE BESTPOSITION? !!!!!
		 */
		
		return result;
>>>>>>> Stashed changes
	}

}
