package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.ainability.domain.CustomGameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.ainability.domain.Position;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class WolfHeuristics extends Heuristics {
	private State state;
	
	// ------------------------------------------------------------------------------------------------------
	// Setting the custom weight variable : for blackPlayer lower weight (negative)
	// is better
	// ------------------------------------------------------------------------------------------------------

	//TO DO IF WE HAVE TIME
	//private static double WEIGHT_FREE_WAYS_TO_RHOMBUS = 0;
	
	
	private static double WEIGHT_RHOMBUS = 30;
	private static double WEIGHT_ROW_COL_COVER = 10;
	private static double WEIGHT_VICTORY = Double.NEGATIVE_INFINITY;
	private static double WEIGHT_WHITE_PAWNS = -50; //Has to be a negative value
	private static double WEIGHT_BLACK_PAWNS = 40;
	private static double WEIGHT_OPEN_WAYS = 25;
	private static double WEIGHT_BLACK_NEAR_KING = 60;
	private static double WEIGHT_PAWN_TO_EAT_KING = 60;
	private static double WEIGHT_THREAT = -25; // Threat of black pawn to be eaten 

	public WolfHeuristics(State state) {
		super(state);
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
	}
	
	private int NumberOfBlackNearKing() {
		//System.out.println("----Cerco la posizione del re ----");
		int number = 0;
		Position kingPosition = this.getKingPosition();
		if (kingPosition.getRow() == 1 || kingPosition.getColumn() == 1)return 0;
		//System.out.println("----il re sta in posizione "+ kingPosition.getRow() + " "+ kingPosition.getColumn() +" ----");
		//check north
		if(state.getBoard()[kingPosition.getRow()-1-1][kingPosition.getColumn()-1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		//check south
		if(state.getBoard()[kingPosition.getRow()-1+1][kingPosition.getColumn()-1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		//check east
		if(state.getBoard()[kingPosition.getRow()-1][kingPosition.getColumn()-1+1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		//check west
		if(state.getBoard()[kingPosition.getRow()-1][kingPosition.getColumn()-1-1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}		
		return number;
	}
	
	/**
	 * this method check if a pawn of the oppenent's team can go in a certain given position
	 * @param pos position to check for potential move of opponents pawn
	 * @return boolean value 
	 */
	private boolean canPawnGoThere(Position pos) {
		Pawn [][] board = this.state.getBoard();
		//check north side
		for(int i=pos.getRow()-1-1;i>1;i--) {
			if(! board[i][pos.getColumn()-1].equalsPawn(State.Pawn.EMPTY.toString()) && ! this.isPositionCitadel(new Position(i+1,pos.getColumn())) ) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][pos.getColumn()-1].equalsPawn(State.Pawn.WHITE.toString())){//the first pawn founded is white! DANGER
					return true;
				}
			}
		}
		//check south side
		for(int i=pos.getRow()-1+1;i<9;i++) {
			if(! board[i][pos.getColumn()-1].equalsPawn(State.Pawn.EMPTY.toString()) && ! this.isPositionCitadel(new Position(i+1,pos.getColumn())) ) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][pos.getColumn()-1].equalsPawn(State.Pawn.WHITE.toString())){//the first pawn founded is white! DANGER
					return true;
				}
			}
		}
		//check west side
		for(int i=pos.getColumn()-1-1;i>1;i--) {
			if(! board[pos.getRow()-1][i].equalsPawn(State.Pawn.EMPTY.toString()) && ! this.isPositionCitadel(new Position(pos.getRow(),i+1)) ) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[pos.getRow()-1][i].equalsPawn(State.Pawn.WHITE.toString())){//the first pawn founded is white! DANGER
					return true;
				}
			}
		}
		//check east side
		for(int i=pos.getColumn()-1+1;i<9;i++) {
			if(! board[pos.getRow()-1][i].equalsPawn(State.Pawn.EMPTY.toString()) && ! this.isPositionCitadel(new Position(pos.getRow(),i+1)) ) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[pos.getRow()-1][i].equalsPawn(State.Pawn.WHITE.toString())){//the first pawn founded is white! DANGER
					return true;
				}
			}
		}
		
		
		
		return false;
	}
	
	/**
	 * this method calculate the number of black pawns that are in danger--> that can be eaten after one move of opponent
	 * @return number of pawns in danger
	 */
	private int numberOfPawnsInDanger() {
		return 0;
		/*
		int number = 0;
		Pawn [][] board = this.state.getBoard();
		
		for (int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j].equals(State.Pawn.BLACK)) { // MATCH!
					int row = i+1; 		//+1 is necessary because we use 1-9 notation
					int column = j+1;	//+1 is necessary because we use 1-9 notation
					//----------------------------------------------------------------------------------------//
					//Now let's check to adjacent threat already existing
					//----------------------------------------------------------------------------------------//
					//check north
					if(board[row-1-1][column-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1-1][column-1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(new Position(row-1-1,column-1)) || board[row-1-1][column-1].equalsPawn(State.Pawn.THRONE.toString())  )
					{//we have a potential threat on south side because of bad cells at north
						if (this.canPawnGoThere(new Position(row+1,column ) ))number++ ;	
					}
					//check south	
					if(board[row-1+1][column-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1+1][column-1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(new Position(row-1+1,column-1)) || board[row-1+1][column-1].equalsPawn(State.Pawn.THRONE.toString())  )
					{//we have a potential threat on north side because of bad cells at south
						if (this.canPawnGoThere(new Position(row-1,column ) ))number++ ;	
					}	
					//check west
					if(board[row-1][column-1-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1][column-1-1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(new Position(row-1,column-1-1)) || board[row-1][column-1-1].equalsPawn(State.Pawn.THRONE.toString())  )
					{//we have a potential threat on east side because of bad cells at west
						if (this.canPawnGoThere(new Position(row,column +1  ) ))number++ ;	
					}	
					//check east
					if(board[row-1][column-1+1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1][column-1+1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(new Position(row-1,column-1+1)) || board[row-1][column-1+1].equalsPawn(State.Pawn.THRONE.toString())  )
					{//we have a potential threat on west side because of bad cells at east
						if (this.canPawnGoThere(new Position(row,column -1  ) ))number++ ;	
					}						
				}
			}
		}		
		return number;
		*/
	}
	
	/**
	 * This method calculate the number of black pawn that are already in rhombus cells	 * 
	 * @return number of pawn that are in rhombus
	 */
	private int numberOfPawnsInRhombus() {
		int number=0;
		Pawn board[][]= state.getBoard();
		int Rposition[][] = {
			  {1,2},       {1,6},
        {2,3},                   {2,7},

        {6,1},                   {6,7},
              {7,2},       {7,6}
		};
		for (int i=0; i<9;i++) {
			if(board[i][i].equalsPawn(State.Pawn.BLACK.toString() )) number++;
			//increase only if there is a black pawn on rhombus cells			
		}	
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}