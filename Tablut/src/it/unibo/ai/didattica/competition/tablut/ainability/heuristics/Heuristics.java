package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.ainability.domain.Position;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public abstract class Heuristics {
	protected State state;
	
	public Heuristics(State state) {
		this.state=state;
	}

	
	public int evaluateState() {
		return 0;
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
	
	
	

	/**
	 * this method calculate the number of free Row/column that the king can use to move 
	 * @return number of free row/column
	 */
	
	protected int NumberOfKingRowColFree(Position kingPosition ) {

		int freeLine =0;
		//check north
		freeLine++;
		for(int i = kingPosition.getRow()-1;i>1;i--) {
			if(i-1>=0 && ! state.getPawn(i-1, kingPosition.getColumn()-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		//check south
		freeLine++;
		for(int i = kingPosition.getRow()+1;i<9;i++) {
			if(i-1>=0 && ! state.getPawn(i-1, kingPosition.getColumn()-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		//check west
		freeLine++;
		for(int i = kingPosition.getColumn()-1;i>1;i--) {
			if(i-1>=0 && ! state.getPawn(kingPosition.getRow()-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		//check east
		freeLine++;
		for(int i = kingPosition.getColumn()+1;i<9;i++) {
			if(i-1>=0 && ! state.getPawn(kingPosition.getRow()-1, i-1).equalsPawn(State.Pawn.EMPTY.toString())) { //match se trovo una posizione con una pedina
				freeLine --;
				break;
			}
		}
		
		return freeLine;	
	}

	/*
	 * This method check if a given position is a citadel or not
	 * @param pos position to check
	 * @return boolean value that state if the position is a citadel or not
	 * 
	 */	
	
	protected boolean isPositionCitadel(Position pos) {	
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
	
	protected boolean isPositionCitadel(int rowToCheck, int columnToCheck) {

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

}
