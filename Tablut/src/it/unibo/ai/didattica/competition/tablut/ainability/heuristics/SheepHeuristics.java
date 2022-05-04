package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class SheepHeuristics extends Heuristics {
	private State state;
	private int currentNumberOfWhite;
	private int currentNumberOfBlack;
	private int kingPositionRow;
	private int kingPositionColumn;
	
	// ----WEIGHT ----
	private static int NUM_BLACK = 16;	
	private static int WEIGHT_WHITE_PAWNS = 70; 
	private static int WEIGHT_BLACK_PAWNS = 25;
	private static int WEIGHT_THREAT = -20; 
	private static int WEIGHT_WINNING_ROW_COLUMN = 55;
//	private static int WEIGHT_KING_IS_SAFE = 60;
//	private static int WEIGHT_BLACK_NEAR_KING= -10;
	
	
	/**
	 * this method return the heuristic value for the current state
	 */
	@Override
	public int evaluateState() {
		int result = 0;
		// init
		kingPositionAndNumberPawns();
		result += WEIGHT_WHITE_PAWNS * this.currentNumberOfWhite 
				//+ WEIGHT_KING_IS_SAFE * kingSafe()
				+ WEIGHT_WINNING_ROW_COLUMN * winningRowColumn()
				+ WEIGHT_THREAT + this.numberOfPawnsInDanger()
				+ WEIGHT_BLACK_PAWNS * (NUM_BLACK - this.currentNumberOfBlack);
				//+ WEIGHT_BLACK_NEAR_KING* NumberOfBlackNearKing() ;
		return result;
	}


	/**
	 * Constructor
	 * @param state
	 */
	public SheepHeuristics(State state) {
		super(state);
		this.state = state;

	}

	
	@SuppressWarnings("unused")
	private int NumberOfBlackNearKing() {		
		int number = 0;		
		//check north
		if(kingPositionRow-1-1>=0 && 
									(state.getBoard()[kingPositionRow-1-1][kingPositionColumn-1].equalsPawn(State.Pawn.BLACK.toString()) ||
									this.isPositionCitadel(kingPositionRow-1, kingPositionColumn)	)) {
			number++;
		}
		//check south
		if(kingPositionRow-1+1<=8 && (
										state.getBoard()[kingPositionRow-1+1][kingPositionColumn-1].equalsPawn(State.Pawn.BLACK.toString()) ||
										this.isPositionCitadel(kingPositionRow+1, kingPositionColumn))) {
			number++;
		}
		//check east
		if(kingPositionColumn-1+1 <=8 && (state.getBoard()[kingPositionRow-1][kingPositionColumn-1+1].equalsPawn(State.Pawn.BLACK.toString()) ||
											this.isPositionCitadel(kingPositionRow, kingPositionColumn+1))
				) {
			number++;
		}
		//check west
		if(kingPositionColumn-1-1 >=0 && (state.getBoard()[kingPositionRow-1][kingPositionColumn-1-1].equalsPawn(State.Pawn.BLACK.toString()) ||
											this.isPositionCitadel(kingPositionRow, kingPositionColumn-1))
				) {
			number++;
		}		
		return number;
	}
	
	
	/**
	 * this method calculate the number of black pawns that are in danger--> that
	 * can be eaten after one move of opponent
	 * 
	 * @return number of pawns in danger
	 */
	protected int numberOfPawnsInDanger() {

		int number = 0;
		Pawn[][] board = this.state.getBoard();

		for (int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j].equals(State.Pawn.WHITE)) { // MATCH!
					int row = i + 1; // +1 is necessary because we use 1-9 notation
					int column = j + 1; // +1 is necessary because we use 1-9 notation
					// ----------------------------------------------------------------------------------------//
					// Now let's check to adjacent threat already existing
					// ----------------------------------------------------------------------------------------//
					// check north
					if (row - 1 - 1 >= 0 && (board[row - 1 - 1][column - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| this.isPositionCitadel(row - 1 - 1, column - 1)
							|| board[row - 1 - 1][column - 1].equalsPawn(State.Pawn.THRONE.toString()))) {	// we have a
																											// potential
																											// threat on
																											// south
																											// side
																											// because
																											// of bad
																											// cells at
																											// north
						if (row + 1 <= 9 && this.canBlackPawnGoThere(row + 1, column))
							number++;
					}
					// check south
					if (row - 1 + 1 <= 8 && (board[row - 1 + 1][column - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| this.isPositionCitadel(row - 1 + 1, column - 1)
							|| board[row - 1 + 1][column - 1].equalsPawn(State.Pawn.THRONE.toString()))) {// we have a
																											// potential
																											// threat on
																											// north
																											// side
																											// because
																											// of bad
																											// cells at
																											// south
						if (row - 1 >= 1 && this.canBlackPawnGoThere(row - 1, column))
							number++;
					}
					// check west
					if (column - 1 - 1 >= 0 && (board[row - 1][column - 1 - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| this.isPositionCitadel(row - 1, column - 1 - 1)
							|| board[row - 1][column - 1 - 1].equalsPawn(State.Pawn.THRONE.toString()))) {// we have a
																											// potential
																											// threat on
																											// east side
																											// because
																											// of bad
																											// cells at
																											// west
						if (column + 1 <= 9 && this.canBlackPawnGoThere(row, column + 1))
							number++;
					}
					// check east
					if (column - 1 + 1 <= 8 && (board[row - 1][column - 1 + 1].equalsPawn(State.Pawn.BLACK.toString())
							|| this.isPositionCitadel(row - 1, column - 1 + 1)
							|| board[row - 1][column - 1 + 1].equalsPawn(State.Pawn.THRONE.toString()))) {// we have a
																											// potential
																											// threat on
																											// west side
																											// because
																											// of bad
																											// cells at
																											// east
						if (column - 1 >= 1 && this.canBlackPawnGoThere(row, column - 1))
							number++;
					}
				}
			}
		}
		return number;

	}

	protected boolean canBlackPawnGoThere(int posRow, int posColumn) {
		Pawn[][] board = this.state.getBoard();
		// check north side
		for (int i = posRow - 1 - 1; i >= 0; i--) {
			if (!board[i][posColumn - 1].equalsPawn(State.Pawn.EMPTY.toString())) {

				// inside this if only if the cells is not empty and is not a citadel
				if (board[i][posColumn - 1].equalsPawn(State.Pawn.BLACK.toString())) {// the first pawn founded is
																						// white! DANGER
					return true;
				} else
					break;// exit for cycle
			}
		}
		// check south side
		for (int i = posRow - 1 + 1; i < 9; i++) {
			if (!board[i][posColumn - 1].equalsPawn(State.Pawn.EMPTY.toString())) {
				// inside this if only if the cells is not empty and is not a citadel
				if (board[i][posColumn - 1].equalsPawn(State.Pawn.BLACK.toString())) {// the first pawn founded is
																						// white! DANGER
					return true;
				} else
					break;// exit for cycle
			}
		}
		// check west side
		for (int i = posColumn - 1 - 1; i >= 0; i--) {
			if (!board[posRow - 1][i].equalsPawn(State.Pawn.EMPTY.toString())) {
				// inside this if only if the cells is not empty and is not a citadel
				if (board[i][posColumn - 1].equalsPawn(State.Pawn.BLACK.toString())) {// the first pawn founded is
																						// white! DANGER
					return true;
				} else
					break;
			}
		}
		// check east side
		for (int i = posColumn - 1 + 1; i < 9; i++) {
			if (!board[posRow - 1][i].equalsPawn(State.Pawn.EMPTY.toString())) {
				// inside this if only if the cells is not empty and is not a citadel
				if (board[i][posColumn - 1].equalsPawn(State.Pawn.BLACK.toString())) {// the first pawn founded is
																						// white! DANGER
					return true;
				} else
					break;
			}
		}

		return false;
	}

	/**
	 * this method calculate the number of free ways that the king can use to escape
	 * 
	 * @return number of free ways
	 */	
	@SuppressWarnings("unused")
	private int NumberOfKingFreeWays() {
		// Position kingPosition = this.getKingPosition();
		int freeWays = 0;
		// check north
		freeWays++;
		for (int i = kingPositionRow - 1; i > 1; i--) {
			if (!state.getPawn(i - 1, kingPositionColumn - 1).equalsPawn(State.Pawn.EMPTY.toString()) || // match if a
																											// pawn is
																											// found on
																											// the way
																											// to
																											// liberty
					this.isPositionCitadel(i - 1, kingPositionColumn - 1)) { // match if a citadel is found on the way
																				// to liberty
				freeWays--;
				break;
			}
		}
		// check south
		freeWays++;
		for (int i = kingPositionRow + 1; i < 9; i++) {
			if (!state.getPawn(i - 1, kingPositionColumn - 1).equalsPawn(State.Pawn.EMPTY.toString()) || // match if a
																											// pawn is
																											// found on
																											// the way
																											// to
																											// liberty
					this.isPositionCitadel(i - 1, kingPositionColumn - 1)) { // match if a citadel is found on the way
																				// to liberty
				freeWays--;
				break;
			}
		}
		// check west
		freeWays++;
		for (int i = kingPositionColumn - 1; i > 1; i--) {
			if (!state.getPawn(kingPositionRow - 1, i - 1).equalsPawn(State.Pawn.EMPTY.toString()) || // match if a pawn
																										// is found on
																										// the way to
																										// liberty
					this.isPositionCitadel(kingPositionRow - 1, i - 1)) { // match if a citadel is found on the way to
																			// liberty
				freeWays--;
				break;
			}
		}
		// check east
		freeWays++;
		for (int i = kingPositionColumn + 1; i < 9; i++) {
			if (!state.getPawn(kingPositionRow - 1, i - 1).equalsPawn(State.Pawn.EMPTY.toString()) || // match if a pawn
																										// is found on
																										// the way to
																										// liberty
					this.isPositionCitadel(kingPositionRow - 1, i - 1)) { // match if a citadel is found on the way to
																			// liberty
				freeWays--;
				break;
			}
		}

		return freeWays;
	}

	/**
	 * this method set the position of the king in the board and the number of
	 * white/back pawns
	 * 
	 * @param none
	 * 
	 * @return void
	 */
	private void kingPositionAndNumberPawns() {
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
	//DA COMMENTARE E SISTEMARE DA MARCO O LUCA
	@SuppressWarnings("unused")
	private int kingSafe() {

		int result = 0;

		Pawn[][] pawns = state.getBoard();

		if ((kingPositionRow == 5 && kingPositionColumn >= 4 && kingPositionColumn <= 6)
				|| (kingPositionColumn == 5 && kingPositionRow >= 4 && kingPositionRow <= 6)) {
			// il re è sul trono e un bianco lo protegge:
			if (pawns[kingPositionRow - 1 - 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString()) || // sopra
					pawns[kingPositionRow - 1 + 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString()) || // sotto
					pawns[kingPositionRow - 1][kingPositionColumn - 1 + 1].equalsPawn(State.Pawn.WHITE.toString()) || // dx
					pawns[kingPositionRow - 1][kingPositionColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) // sx
				result += 2;
		} else { // ne servono 2 per magiare il re e il re è coperto:

			// non copri la stella oppure nemico sopra e sei coperto sotto:
			if (kingPositionRow - 1 - 1 >= 0 && kingPositionRow - 1 + 1 <= 8 && (kingPositionRow + 1) != 9
					&& (pawns[kingPositionRow - 1 - 1][kingPositionColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow - 1, kingPositionColumn))
					&& pawns[kingPositionRow - 1 + 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sotto e sei coperto sopra:
			if (kingPositionRow - 1 + 1 <= 8 && kingPositionRow - 1 - 1 >= 0 && kingPositionRow - 1 != 1
					&& (pawns[kingPositionRow - 1 + 1][kingPositionColumn - 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow + 1, kingPositionColumn))
					&& pawns[kingPositionRow - 1 - 1][kingPositionColumn - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}

			// non copri la stella oppure nemico dx e sei coperto sx:
			if (kingPositionColumn - 1 + 1 <= 8 && kingPositionColumn - 1 - 1 >= 0 && kingPositionColumn - 1 != 1
					&& (pawns[kingPositionRow - 1][kingPositionColumn - 1 + 1].equalsPawn(State.Pawn.BLACK.toString())
							|| isPositionCitadel(kingPositionRow, kingPositionColumn + 1))
					&& pawns[kingPositionRow - 1][kingPositionColumn - 1 - 1].equalsPawn(State.Pawn.WHITE.toString())) {
				result += 1;
			}
			// non copri la stella oppure nemico sx e sei coperto dx:
			if (kingPositionColumn - 1 - 1 >= 0 && kingPositionColumn - 1 + 1 <= 8 && kingPositionColumn + 1 != 9
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

	    if ((kingPositionColumn-1) < line) {
	      for (i = kingPositionColumn; i < line; i++) {
	        if (!state.getPawn(kingPositionRow-1, i).equalsPawn(State.Pawn.EMPTY.toString())) {
	          return false;
	        }
	      }
	    } else {
	      for (i = line+1; i < kingPositionColumn-1; i++) {
	        if (!state.getPawn(kingPositionRow-1, i).equalsPawn(State.Pawn.EMPTY.toString())) {
	          return false;
	        }
	      }

	    }
	    return true;

	  }
	  
	  
	  private boolean kingArriveRow(int line) {
	    int i;    

	    if ((kingPositionRow-1) < line) {
	      for (i = kingPositionRow; i < line; i++) {
	        if (!state.getPawn(i, kingPositionColumn-1).equalsPawn(State.Pawn.EMPTY.toString())) {
	          return false;
	        }
	      }
	    } else {
	      for (i = line+1; i < kingPositionRow-1; i++) {
	        if (!state.getPawn(i, kingPositionColumn-1).equalsPawn(State.Pawn.EMPTY.toString())) {
	          return false;
	        }
	      }

	    }
	    return true;

	  }
	private int winningRowColumn() {

		int result = 0;
		// noPawnsInRow follow the standard 0-8 and kingPosition follow the standard 1-9
		if ((noPawnsInRow(2) && this.kingPositionRow == 3) || (noPawnsInRow(6) && this.kingPositionRow == 7)
				|| (noPawnsInColumn(2) && this.kingPositionColumn == 3)
				|| (noPawnsInColumn(6) && this.kingPositionColumn == 7)) {
			return 4;
		}

		if (noPawnsInRow(2) && kingArriveRow(2)) {
			result += 1;
		}
		if (noPawnsInRow(6) && kingArriveRow(6)) {
			result += 1;
		}
		if (noPawnsInColumn(2) && kingArriveColumn(2)) {
			result += 1;
		}
		if (noPawnsInColumn(6) && kingArriveColumn(6)) {
			result += 1;
		}

		return result;

	}

}
