package it.unibo.ai.didattica.competition.tablut.ainability.minmax;

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import aima.core.search.adversarial.Game;

public class CustomIterativeDeepeningAlphaBetaSearch extends IterativeDeepeningAlphaBetaSearch<State, Action, State.Turn>{
	
	public CustomIterativeDeepeningAlphaBetaSearch(Game<State, Action, State.Turn> game, double min, double max, int time) {
		super(game, min, max, time);
	}
	
	@Override
	protected double eval(State state, State.Turn player) {
		super.eval(state, player); //
		return game.getUtility(state, player); //return heuristic value based on a state
	}
	
	@Override
	public Action makeDecision(State state) {
		Action a = super.makeDecision(state);
		System.out.println("Explored a total of "+getMetrics().get(METRICS_NODES_EXPANDED)+" nodes, reaching a dept limit of "+getMetrics().get(METRICS_MAX_DEPTH));
		return a;
	}
	

}
