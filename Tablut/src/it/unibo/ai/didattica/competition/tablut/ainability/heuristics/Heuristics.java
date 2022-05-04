package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.ainability.domain.Position;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public abstract class Heuristics {
	protected State state;
	
	//costructor
	public Heuristics(State state) {
		this.state=state;
	}

	//to be overrided
	public int evaluateState() {
		return 0;
	}

	


	/**
	 * this method calculate the number of free Row/column that the king can use to move 
	 * @param kingPosition king position
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

	/**
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
	/**
	 * This method check if a given position is a citadel or not
	 * @param pos position to check
	 * @return boolean value that state if the position is a citadel or not
	 * 
	 */	
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
