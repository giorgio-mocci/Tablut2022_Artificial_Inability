package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;


import it.unibo.ai.didattica.competition.tablut.ainability.domain.Position;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class WolfHeuristics extends Heuristics {
	private State state;
	private Turn turn;
	private static int NUM_WHITE = 8;
	// ------------------------------------------------------------------------------------------------------
	// Setting the custom weight variable : for blackPlayer lower weight (negative)
	// is better
	// ------------------------------------------------------------------------------------------------------

	//TO DO IF WE HAVE TIME
	//private static int WEIGHT_FREE_WAYS_TO_RHOMBUS = 0;
	
	//max heuristics = 2000
	
	
	private static int WEIGHT_ROW_COL_FREE = 0;
	
	private static int WEIGHT_RHOMBUS = 5; 
	private static int WEIGHT_WHITE_PAWNS = 55; 
	private static int WEIGHT_BLACK_PAWNS = 30;
	private static int WEIGHT_OPEN_WAYS = -200;	
	private  int WEIGHT_BLACK_NEAR_KING = 10;
	private static int WEIGHT_THREAT = -10; // Threat of black pawn to be eaten 
	
	private static int WEIGHT_PAWN_TO_EAT_KING = 0;
	
	private static int WEIGHT_ON_THE_HUNT= 50;

	private int currentNumberOfWhite;
	private int currentNumberOfBlack;
	//private Position kingPosition;
	
	private int kingPositionRow;
	private int kingPositionColumn;
	
	public WolfHeuristics(State state,Turn turn) {
		super(state);
		this.state = state;
		this.turn = turn;
	}

	@Override
	public  int evaluateState() {		
		int result = 0;
		
		// init
	    kingPositionAndNumberPawns();
		
		
		
		
	
		//the value of numberOfBlackToEatKing is inversely proportional (lower is better)
	    /*
	    result += WEIGHT_RHOMBUS * this.numberOfPawnsInRhombus() + 
				  WEIGHT_ROW_COL_FREE * super.NumberOfKingRowColFree(kingPosition) + 
				  WEIGHT_WHITE_PAWNS * (8-this.currentNumberOfWhite) + 
				  WEIGHT_BLACK_PAWNS * this.currentNumberOfBlack + 
				  WEIGHT_OPEN_WAYS * this.NumberOfKingFreeWays() +
				  WEIGHT_BLACK_NEAR_KING * this.NumberOfBlackNearKing() + 
				  WEIGHT_THREAT * this.numberOfPawnsInDanger();
		*/
	    
	
	    
	    result = WEIGHT_WHITE_PAWNS * (NUM_WHITE-this.currentNumberOfWhite) + 
				 WEIGHT_BLACK_PAWNS * this.currentNumberOfBlack +
	    		 WEIGHT_OPEN_WAYS * this.NumberOfKingFreeWays() +
	    		 WEIGHT_BLACK_NEAR_KING * this.NumberOfBlackNearKing() + 
	    		 WEIGHT_THREAT + this.numberOfPawnsInDanger() +
	    		WEIGHT_RHOMBUS + this.numberOfPawnsInRhombus();
		return result;
	}
	
	/**
	 * This method return the number of BlackPawn in good position
	 * GoodPosition = a pawn that is not in danger and can eat a white Pawn in the next move
	 * @return number of pawn in good position
	 */
	private int NumberOfBlackPawnInGoodPosition() {
		int number =0;
		Pawn [][] board = this.state.getBoard();
		for (int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				//Check if the pow in current position is in danger
				if (!this.isThisPawInDanger(i+1,j+1)) {//+1 because we use stupid notation
					//check if there is a white pawn above my pawn
					if( i-1 >=1 && board[i-1][j].equalsPawn(State.Pawn.WHITE.toString())  ) {
						//there is a white pawn above
						//now let's check if a black pawn can go above it
						if(this.canPawnGoThere(i-2+1,j+1)) {
					//		System.out.println("Pawn in position "+i+ " "+j +" is in good position");
							number++;
						}
					}
					//check if there is a white pawn under
					if( i+1 <=7 && board[i+1][j].equalsPawn(State.Pawn.WHITE.toString())  ) {
						//there is a white pawn under my pawn
						//now let's check if a black pawn can go under it
						if(this.canPawnGoThere(i+2+1,j+1)) {
							//System.out.println("Pawn in position "+i+ " "+j +" is in good position");
							number++;
						}
					}
					//check if there is a white pawn on the left
					if( j-1 >=1 && board[i][j-1].equalsPawn(State.Pawn.WHITE.toString())  ) {
						//there is a white pawn on the left
						//now let's check if a black pawn can go left to it
						if(this.canPawnGoThere(i+1,j-2+1)) {
							//System.out.println("Pawn in position "+i+ " "+j +" is in good position");
							number++;
						}
					}
					//check if there is a white pawn on the right
					if( j+1 <=7 && board[i][j+1].equalsPawn(State.Pawn.WHITE.toString())  ) {
						//there is a white pawn on the right
						//now let's check if a black pawn can go right to it
						if(this.canPawnGoThere(i+1,j+2+1)) {
							//System.out.println("Pawn in position "+i+ " "+j +" is in good position");
							number++;
						}
					}
					
					
					
					
				}//end if of danger check
				
					
					
				}
			}
		
		return number;
	}
	
	
	/**
	 * this method calculate the number of black pawns that are in danger--> that can be eaten after one move of opponent
	 * @return number of pawns in danger
	 */
	protected int numberOfPawnsInDanger() {
		

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
					if(row-1-1>=0 && ( board[row-1-1][column-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1-1][column-1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(row-1-1,column-1) || board[row-1-1][column-1].equalsPawn(State.Pawn.THRONE.toString()))  )
					{//we have a potential threat on south side because of bad cells at north
						if (row+1<=9 && this.canPawnGoThere(row+1,column   ))number++ ;	
					}
					//check south	
					if(row-1+1<=8 && ( board[row-1+1][column-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1+1][column-1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(row-1+1,column-1) || board[row-1+1][column-1].equalsPawn(State.Pawn.THRONE.toString())  ))
					{//we have a potential threat on north side because of bad cells at south
						if (row-1>=1 && this.canPawnGoThere(row-1,column  ))number++ ;	
					}	
					//check west
					if(column-1-1>=0 && (board[row-1][column-1-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1][column-1-1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(row-1,column-1-1) || board[row-1][column-1-1].equalsPawn(State.Pawn.THRONE.toString())  ))
					{//we have a potential threat on east side because of bad cells at west
						if (column +1<=9 && this.canPawnGoThere(row,column +1   ))number++ ;	
					}	
					//check east
					if(column-1+1 <=8 && ( board[row-1][column-1+1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1][column-1+1].equalsPawn(State.Pawn.KING.toString()) ||
							this.isPositionCitadel(row-1,column-1+1) || board[row-1][column-1+1].equalsPawn(State.Pawn.THRONE.toString())  ))
					{//we have a potential threat on west side because of bad cells at east
						if (column -1 >= 1 && this.canPawnGoThere(row,column -1  ))number++ ;	
					}						
				}
			}
		}		
		return number;
		
	}
	
	

	protected boolean canPawnGoThere(int posRow,int posColumn) {
		Pawn [][] board = this.state.getBoard();
		//check north side
		for(int i=posRow-1-1;i>=0;i--) {
			if(! board[i][posColumn-1].equalsPawn(State.Pawn.EMPTY.toString())  ) {
					
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][posColumn-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][posColumn-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;//exit for cycle
			}
		}
		//check south side
		for(int i=posRow-1+1;i<9;i++) {
			if(! board[i][posColumn-1].equalsPawn(State.Pawn.EMPTY.toString())  ) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][posColumn-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][posColumn-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;//exit for cycle
			}
		}
		//check west side
		for(int i=posColumn-1-1;i>=0;i--) {
			if(! board[posRow-1][i].equalsPawn(State.Pawn.EMPTY.toString())) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][posColumn-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][posColumn-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;
			}
		}
		//check east side
		for(int i=posColumn-1+1;i<9;i++) {
			if(! board[posRow-1][i].equalsPawn(State.Pawn.EMPTY.toString())) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][posColumn-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][posColumn-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;
			}
		}
		
		
		
		return false;
	}
	
	
	
	
	
	
	/*
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
					kingPositionColumn= j + 1; // +1 is necessary because we use 1-9 notation
					
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
	
	/*private int getWhitePawns() {

		return state.getNumberOf(Pawn.WHITE);
	}*/
	
/*	private int getBlackPawns() {

		return state.getNumberOf(Pawn.BLACK);
	}*/

	/*
	 * this method return the position of the king in the board
	 * @param none
	 * @return King position
	 */
	
	/*public Position getKingPosition() {
		Position Pos = null;
		for (int i = 0; i < this.state.getBoard()[0].length; i++) {
			for (int j = 0; j < this.state.getBoard()[0].length; j++) {
				if (this.state.getBoard()[i][j].equals(State.Pawn.KING)) { // MATCH!
					int row = i+1; 		//+1 is necessary because we use 1-9 notation
					int column = j+1;	//+1 is necessary because we use 1-9 notation
					Pos = new Position(row, column);
				}
			}
		}
		return Pos;
	}*/
	
	private boolean isPawnNearThrone(int row,int column){
		
		if(column == 4 && row == 5)return true; //pawn to the left of throne
		if(column == 6 && row == 5)return true; //pawn to the right of throne
		if(column == 5 && row == 6)return true; //pawn to the sud of throne
		if(column == 5 && row == 4)return true; //pawn to the north of throne		
		return false;		
	}
	
	private boolean isPawnNearCitadel(int row,int column) {
		//check north side
		if(row > 1 && this.isPositionCitadel(column,row-1))return true;
		//check south side
		if(row < 9 && this.isPositionCitadel(column,row+1))return true;
		//check east side
		if(column < 9 && this.isPositionCitadel(column+1,row))return true;
		//check west side
		if(column > 1 && this.isPositionCitadel(column-1,row))return true;		
		return false;
	}
	
	/**this method return the number of black pawns necessary to kill the king
	 * @param none
	 * @return return number of black pawns
	 */
	private int getBlackNumberToCaptureKing() {
		
		//Position kingPosition = this.getKingPosition();
		
		//if king is on throne
		if (kingPositionColumn == 5 && kingPositionRow == 5) return 4;
		//if king is adjacent throne
		if(isPawnNearThrone(kingPositionRow,kingPositionColumn))return 3;
		//if king is adjacent citadel
		if(isPawnNearCitadel(kingPositionRow,kingPositionColumn))return 1;		
		return 2;
	}
	
	
	/**
	 * this method calculate the number of free ways that the king can use to escape 
	 * @return number of free ways
	 */
	//DA TESTARE PER BENE CON DELLE STAMPE DURANTE IL TEST!!!!
	private int NumberOfKingFreeWays() {
		//Position kingPosition = this.getKingPosition();
		int freeWays =0;
		//check north
		freeWays++;		
		for(int i = kingPositionRow-1;i>1;i--) {
			if(! state.getPawn(i-1, kingPositionColumn-1).equalsPawn(State.Pawn.EMPTY.toString()) || //match if a pawn is found on the way to liberty
				this.isPositionCitadel(i-1,kingPositionColumn-1) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		//check south
		freeWays++;
		for(int i = kingPositionRow+1;i<9;i++) {
			if(! state.getPawn(i-1, kingPositionColumn-1).equalsPawn(State.Pawn.EMPTY.toString())|| //match if a pawn is found on the way to liberty
					this.isPositionCitadel(i-1,kingPositionColumn-1) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		//check west
		freeWays++;
		for(int i = kingPositionColumn-1;i>1;i--) {
			if(! state.getPawn(kingPositionRow-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())|| //match if a pawn is found on the way to liberty
					this.isPositionCitadel(kingPositionRow-1,i-1) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		//check east
		freeWays++;
		for(int i = kingPositionColumn+1;i<9;i++) {
			if(! state.getPawn(kingPositionRow-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())|| //match if a pawn is found on the way to liberty
					this.isPositionCitadel(kingPositionRow-1,i-1) ) { //match if a citadel is found on the way to liberty
				freeWays --;
				break;
			}
		}
		
		return freeWays;	
	}
	
	private int NumberOfBlackNearKing() {
		//System.out.println("----Cerco la posizione del re ----");
		int number = 0;
		//System.out.println("----il re sta in posizione "+ kingPositionRow + " "+ kingPositionColumn +" ----");
		//check north
		if(kingPositionRow-1-1>=0 && state.getBoard()[kingPositionRow-1-1][kingPositionColumn-1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		//check south
		if(kingPositionRow-1+1<=8 && state.getBoard()[kingPositionRow-1+1][kingPositionColumn-1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		//check east
		if(kingPositionColumn-1+1 <=8 && state.getBoard()[kingPositionRow-1][kingPositionColumn-1+1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}
		//check west
		if(kingPositionColumn-1-1 >=0 && state.getBoard()[kingPositionRow-1][kingPositionColumn-1-1].equalsPawn(State.Pawn.BLACK.toString())) {
			number++;
		}		
		return number;
	}
	
	/**
	 * this method check if a pawn of the the given colour  can go in a certain given position
	 * @param pos position to check for potential move of opponents pawn
	 * @param PawnColour to check
	 * @return boolean value 
	 */
	

	
	private boolean canPawnGoThere(Position pos) {
		Pawn [][] board = this.state.getBoard();
		//check north side
		for(int i=pos.getRow()-1-1;i>=0;i--) {
			if(! board[i][pos.getColumn()-1].equalsPawn(State.Pawn.EMPTY.toString())  ) {
					
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][pos.getColumn()-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][pos.getColumn()-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;//exit for cycle
			}
		}
		//check south side
		for(int i=pos.getRow()-1+1;i<9;i++) {
			if(! board[i][pos.getColumn()-1].equalsPawn(State.Pawn.EMPTY.toString())  ) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][pos.getColumn()-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][pos.getColumn()-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;//exit for cycle
			}
		}
		//check west side
		for(int i=pos.getColumn()-1-1;i>=0;i--) {
			if(! board[pos.getRow()-1][i].equalsPawn(State.Pawn.EMPTY.toString())) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][pos.getColumn()-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][pos.getColumn()-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;
			}
		}
		//check east side
		for(int i=pos.getColumn()-1+1;i<9;i++) {
			if(! board[pos.getRow()-1][i].equalsPawn(State.Pawn.EMPTY.toString())) {
				//inside this if only if the cells is not empty and is not a citadel
				if(board[i][pos.getColumn()-1].equalsPawn(State.Pawn.WHITE.toString()) || board[i][pos.getColumn()-1].equalsPawn(State.Pawn.KING.toString())){//the first pawn founded is white! DANGER
					return true;
				}else break;
			}
		}
		
		
		
		return false;
	}
	
	/**
	 * 
	 * @param pos position to check
	 * @return if the pawn in the position "pos" is in danger
	 */
	private boolean isThisPawInDanger(int row, int column){
		Pawn [][] board = this.state.getBoard();
		
		//----------------------------------------------------------------------------------------//
		//Now let's check to adjacent threat already existing
		//----------------------------------------------------------------------------------------//
		//check north
		//check north
		if(row-1-1>=0 && ( board[row-1-1][column-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1-1][column-1].equalsPawn(State.Pawn.KING.toString()) ||
				this.isPositionCitadel(row-1-1,column-1) || board[row-1-1][column-1].equalsPawn(State.Pawn.THRONE.toString()))  )
		{//we have a potential threat on south side because of bad cells at north
			if (row+1<=9 && this.canPawnGoThere(row+1,column ))return true ;	
		}
		//check south	
		if(row-1+1<=8 && ( board[row-1+1][column-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1+1][column-1].equalsPawn(State.Pawn.KING.toString()) ||
				this.isPositionCitadel(row-1+1,column-1) || board[row-1+1][column-1].equalsPawn(State.Pawn.THRONE.toString())  ))
		{//we have a potential threat on north side because of bad cells at south
			if (row-1>=1 && this.canPawnGoThere(row-1,column ) )return true ;	
		}	
		//check west
		if(column-1-1>=0 && (board[row-1][column-1-1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1][column-1-1].equalsPawn(State.Pawn.KING.toString()) ||
				this.isPositionCitadel(row-1,column-1-1) || board[row-1][column-1-1].equalsPawn(State.Pawn.THRONE.toString())  ))
		{//we have a potential threat on east side because of bad cells at west
			if (column +1<=9 && this.canPawnGoThere(row,column +1   ))return true ;	
		}	
		//check east
		if(column-1+1 <=8 && ( board[row-1][column-1+1].equalsPawn(State.Pawn.WHITE.toString()) ||board[row-1][column-1+1].equalsPawn(State.Pawn.KING.toString()) ||
				this.isPositionCitadel(row-1,column-1+1) || board[row-1][column-1+1].equalsPawn(State.Pawn.THRONE.toString())  ))
		{//we have a potential threat on west side because of bad cells at east
			if (column -1 >= 1 && this.canPawnGoThere(row,column -1  ))return true ;	
		}				
		
		return false;
	}

	/**
	 * This method calculate the number of black pawn that are already in rhombus cells	 * 
	 * @return number of pawn that are in rhombus
	 */
	private int numberOfPawnsInRhombus() {
		
	
		if (this.currentNumberOfBlack<10 || this.currentNumberOfWhite<4) return 0;
		int number=0;
		
		int Rposition[][] = {
			  {1,2},       {1,6},
        {2,1},                   {2,7},

        {6,1},                   {6,7},
              {7,2},       {7,6}
		};
		
		
		 
	      for(int[] position : Rposition) {
	            if(this.state.getPawn(position[0], position[1]).equals(State.Pawn.BLACK))
	            	number++;
	        }
	       
		
		return number;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}