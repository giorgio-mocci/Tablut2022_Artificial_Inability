package it.unibo.ai.didattica.competition.tablut.ainability.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public abstract class Heuristics {
	protected State state;
	
	public Heuristics(State state) {
		this.state=state;
	}

	public double evaluateState() {
		return 0;
	}


}
