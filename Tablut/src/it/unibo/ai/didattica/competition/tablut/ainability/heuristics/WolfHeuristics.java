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
	}
}
