package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.ainability.domain.CustomGameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class WolfHeuristics extends Heuristics{
	private State state;
	//------------------------------------------------------------------------------------------------------
	//Setting the custom weight variable : for blackPlayer lower weight (negative) is better
	//------------------------------------------------------------------------------------------------------
	
	private static double WEIGHT_RHOMBUS = 0;  
	private static double WEIGHT_ROW_COL_COVER = 0;
	private static double WEIGHT_VICTORY = Double.NEGATIVE_INFINITY;
	private static double WEIGHT_WHITE_PAWNS =0;
	private static double WEIGHT_BLACK_PAWNS =0;
	private static double WEIGHT_OPEN_WAYS = 0;
	private static double WEIGHT_BLACK_NEAR_KING =0;
	private static double WEIGHT_PAWN_TO_EAT_KING=0;
	private static double WEIGHT_THREAT = 0; //Threat of black pawn to be eaten after move
	
	CustomGameAshtonTablut game = new CustomGameAshtonTablut(0, 0, null, null, null);


    
	
	
	
	public WolfHeuristics(State state) {
		super(state);
<<<<<<< Updated upstream
=======
		this.state = state;
	}

	@Override
	public  double evaluateState() {
		double result = 0.0;
		if(state.getTurn().equalsTurn(State.Turn.BLACKWIN.toString())) {
			result += WEIGHT_VICTORY;
			return result;
		}	
		
		int numberOfBlackToEatKing = this.getBlackNumberToCaptureKing();//Change the weight based on dynamic calculation
		WEIGHT_PAWN_TO_EAT_KING = WEIGHT_PAWN_TO_EAT_KING / numberOfBlackToEatKing;
		//the value of numberOfBlackToEatKing is inversely proportional (lower is better)
		
		result += WEIGHT_RHOMBUS * this.numberOfPawnsInRhombus() + 
				  WEIGHT_ROW_COL_COVER * this.NumberOfKingRowColFree() + 
				  WEIGHT_WHITE_PAWNS * this.getWhitePawns() + 
				  WEIGHT_BLACK_PAWNS * this.getBlackPawns() + 
				  WEIGHT_OPEN_WAYS * this.NumberOfKingFreeWays() +
				  WEIGHT_BLACK_NEAR_KING * this.NumberOfBlackNearKing() + 
				  WEIGHT_PAWN_TO_EAT_KING + //no needs of multiplication, we have changed this value on line 44
				  WEIGHT_THREAT * this.numberOfPawnsInDanger();
				  
		return result;
	}
	
	
	private int getWhitePawns() {

		return state.getNumberOf(Pawn.WHITE);
	}
	
	private int getBlackPawns() {

		return state.getNumberOf(Pawn.BLACK);
	}

	/*
	 * this method return the position of the king in the board
	 * @param none
	 * @return King position
	 */
	
	public Position getKingPosition() {
		Position Pos = null;
		for (int i = 0; i < this.state.getBoard()[0].length; i++) {
			for (int j = 0; j < this.state.getBoard()[0].length; j++) {
				if (this.state.getBoard()[i][j].equals(State.Pawn.KING)) { // MATCH!
					int row = i+1; 		//+1 is necessary because we use 1-9 notation
					int column = j+1;	//+1 is necessary because we use 1-9 notation
					Pos = new Position(i, j);
				}
			}
		}
		return Pos;
	}
	/*
	 * This method check if a given position is a citadel or not
	 * @param pos position to check
	 * @return boolean value that state if the position is a citadel or not
	 * 
	 */	
	
	public boolean isPositionCitadel(Position pos) {	
		int rowToCheck = pos.getRow();
		int columnToCheck = pos.getColumn();
		//Check northen citadels
		if (rowToCheck == 1 && columnToCheck == 4)return true;
		if (rowToCheck == 1 && columnToCheck == 5)return true;
		if (rowToCheck == 1 && columnToCheck == 6)return true;
		if (rowToCheck == 2 && columnToCheck == 5)return true;
		
		//Check eastern citadels
		if (rowToCheck == 4 && columnToCheck == 1)return true;
		if (rowToCheck == 5 && columnToCheck == 1)return true;
		if (rowToCheck == 6 && columnToCheck == 1)return true;
		if (rowToCheck == 5 && columnToCheck == 2)return true;
		
		//Check western citadels
		if (rowToCheck == 4 && columnToCheck == 9)return true;
		if (rowToCheck == 5 && columnToCheck == 9)return true;
		if (rowToCheck == 6 && columnToCheck == 9)return true;
		if (rowToCheck == 5 && columnToCheck == 8)return true;
		
		//Check southern citadels 
		if (rowToCheck == 9 && columnToCheck == 4)return true;
		if (rowToCheck == 9 && columnToCheck == 5)return true;
		if (rowToCheck == 9 && columnToCheck == 6)return true;
		if (rowToCheck == 8 && columnToCheck == 5)return true;		
		
		return false;
	}
	
	private boolean isPawnNearThrone(Position position){			
		if(position.getColumn() == 4 && position.getRow() == 5)return true; //pawn to the left of throne
		if(position.getColumn() == 6 && position.getRow() == 5)return true; //pawn to the right of throne
		if(position.getColumn() == 5 && position.getRow() == 6)return true; //pawn to the sud of throne
		if(position.getColumn() == 5 && position.getRow() == 4)return true; //pawn to the north of throne		
		return false;		
	}
	
	private boolean isPawnNearCitadel(Position position) {
		//check north side
		if(position.getRow() > 1 && this.isPositionCitadel(new Position(position.getColumn(),position.getRow()-1)))return true;
		//check south side
		if(position.getRow() < 9 && this.isPositionCitadel(new Position(position.getColumn(),position.getRow()+1)))return true;
		//check east side
		if(position.getColumn() < 9 && this.isPositionCitadel(new Position(position.getColumn()+1,position.getRow())))return true;
		//check west side
		if(position.getColumn() > 1 && this.isPositionCitadel(new Position(position.getColumn()-1,position.getRow())))return true;		
		return false;
	}
	
	/**this method return the number of black pawns necessary to kill the king
	 * @param none
	 * @return return number of black pawns
	 */
	private int getBlackNumberToCaptureKing() {
		
		Position kingPosition = this.getKingPosition();
		
		//if king is on throne
		if (kingPosition.getColumn() == 5 && kingPosition.getRow() == 5) return 4;
		//if king is adjacent throne
		if(isPawnNearThrone(kingPosition))return 3;
		//if king is adjacent citadel
		if(isPawnNearCitadel(kingPosition))return 1;		
		return 2;
	}
	
	/**
	 * this method calculate the number of free Row/column that the king can use to move 
	 * @return number of free row/column
	 */
	//DA TESTARE PER BENE CON DELLE STAMPE DURANTE IL TEST!!!!
	private int NumberOfKingRowColFree() {
		Position kingPosition = this.getKingPosition();
		int freeLine =0;
		//check north
		freeLine++;
		for(int i = kingPosition.getRow()-1;i>1;i--) {
			if(! state.getPawn(i-1, kingPosition.getColumn()-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		//check south
		freeLine++;
		for(int i = kingPosition.getRow()+1;i<9;i++) {
			if(! state.getPawn(i-1, kingPosition.getColumn()-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		//check west
		freeLine++;
		for(int i = kingPosition.getColumn()-1;i>1;i--) {
			if(! state.getPawn(kingPosition.getRow()-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		//check east
		freeLine++;
		for(int i = kingPosition.getColumn()+1;i<9;i++) {
			if(! state.getPawn(kingPosition.getRow()-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		
		return freeLine;	
	}
	
	/**
	 * this method calculate the number of free ways that the king can use to escape 
	 * @return number of free ways
	 */
	//DA TESTARE PER BENE CON DELLE STAMPE DURANTE IL TEST!!!!
	private int NumberOfKingFreeWays() {
		Position kingPosition = this.getKingPosition();
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
>>>>>>> Stashed changes
	}
}
