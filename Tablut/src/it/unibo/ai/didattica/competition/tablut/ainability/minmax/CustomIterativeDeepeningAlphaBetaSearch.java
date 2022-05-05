package it.unibo.ai.didattica.competition.tablut.ainability.minmax;


import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;

public class CustomIterativeDeepeningAlphaBetaSearch extends IterativeDeepeningAlphaBetaSearch<State, Action, State.Turn>{
	
	public CustomIterativeDeepeningAlphaBetaSearch(Game<State, Action, State.Turn> game, double min, double max, int time) {
		super(game, min, max, time);
		 
	}
	
	/**
	 * This method produces value for all states.
	 * If the state is terminal this implementation returns POSITIVE.INFINITY or NEGATIVE.INFINITY.
	 * Otherwise it returns the AInability's heuristic value.
	 * 
	 * @param state current state
	 * @param player current player
	 * @return evaluation of the state based on AInability's player's heuristic.
	 */
	@Override
	protected double eval(State state, State.Turn player) {			
	    super.eval(state, player); //
		return game.getUtility(state, player); //return heuristic value based on a state
	}
	
	 /**
     * Method controlling the search. It is based on iterative
     * deepening and tries to make to a good decision in limited time.     
     * @param state current state.
	 * @return best action.
     */
	
	@Override
	public Action makeDecision(State state) {
		
		Action a = super.makeDecision(state);		
		System.out.println("     Explored a total of "+getMetrics().get(METRICS_NODES_EXPANDED)+" nodes, reaching a dept limit of "+getMetrics().get(METRICS_MAX_DEPTH));
		return a;
	}
	

}
