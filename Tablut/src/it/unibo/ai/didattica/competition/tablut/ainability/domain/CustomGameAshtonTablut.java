package it.unibo.ai.didattica.competition.tablut.ainability.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.ainability.heuristics.Heuristics;
import it.unibo.ai.didattica.competition.tablut.ainability.heuristics.SheepHeuristics;
import it.unibo.ai.didattica.competition.tablut.ainability.heuristics.WolfHeuristics;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class CustomGameAshtonTablut extends GameAshtonTablut
		implements aima.core.search.adversarial.Game<State, Action, State.Turn> {

	public CustomGameAshtonTablut(int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName,
			String blackName) {
		super(repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
	}

	/**
	 * @param state current state
	 * @return a list of possibleActions
	 */
	public List<Action> getActions(State state) {
		State.Turn turn = state.getTurn();
		List<Action> possibleActions = new ArrayList<Action>();
		int i, j, k;
		String from, to;
		Action action;

		// for each cell on tablut:
		for (i = 0; i < state.getBoard().length; i++) {
			for (j = 0; j < state.getBoard().length; j++) {

				// if pawn color is equal of turn color
				if (state.getPawn(i, j).toString().equals(turn.toString())
						|| (state.getPawn(i, j).equals(State.Pawn.KING) && turn.equals(State.Turn.WHITE))) {

					// check cells above the current one
					for (k = i - 1; k >= 0; k--) {

						// break if you are going up to the citadels after exiting
						if ((!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j)))
								|| (citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j))
										&& (i - k) > 3)) {
							break;
						}

						else if (state.getPawn(k, j).equalsPawn(State.Pawn.EMPTY.toString())) {

							from = state.getBox(i, j);
							to = state.getBox(k, j);

							action = null;
							try {
								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							// if action is possible add it to possibleActions
							try {
								if (state.getPawn(i, j).equals(State.Pawn.KING)) {
									possibleActions.add(0, action);
								} else
									possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column
							break;
						}
					}

					// check cells under the current one
					for (k = i + 1; k < state.getBoard().length; k++) {

						// break if you are going up to the citadels after exiting
						if ((!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j)))
								|| (citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j))
										&& (k - i) > 3)) {
							break;
						}

						else if (state.getPawn(k, j).equalsPawn(State.Pawn.EMPTY.toString())) {

							from = state.getBox(i, j);
							to = state.getBox(k, j);

							action = null;
							try {
								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							// if action is possible add it to possibleActions
							try {

								if (state.getPawn(i, j).equals(State.Pawn.KING)) {
									possibleActions.add(0, action);
								} else
									possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column
							break;
						}
					}

					// check cells to the left of the current one
					for (k = j - 1; k >= 0; k--) {

						// break if you are going up to the citadels after exiting
						if ((!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k)))
								|| (citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k))
										&& (j - k) > 3)) {
							break;
						}

						// check if we are moving on a empty cell
						else if (state.getPawn(i, k).equalsPawn(State.Pawn.EMPTY.toString())) {

							from = state.getBox(i, j);
							to = state.getBox(i, k);

							action = null;
							try {
								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							// if action is possible add it to possibleActions
							try {

								if (state.getPawn(i, j).equals(State.Pawn.KING)) {
									possibleActions.add(0, action);
								} else
									possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column
							break;
						}
					}

					// check cells on the right of the current one
					for (k = j + 1; k < state.getBoard().length; k++) {
						// if(i==5 && j==0 && k==4)System.out.println("sto controllando a6 to e6");

						// break if you are going up to the citadels after exiting
						if ((!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k)))
								|| (citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k))
										&& (k - j) > 3)) {
							// if(i==5 && j==0 && k==4)System.out.println("ho fatto il break");
							break;
						}

						// check if we are moving on a empty cell
						else if (state.getPawn(i, k).equalsPawn(State.Pawn.EMPTY.toString())) {

							from = state.getBox(i, j);
							to = state.getBox(i, k);

							action = null;
							try {

								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							// if action is possible add it to possibleActions
							try {

								if (state.getPawn(i, j).equals(State.Pawn.KING)) {
									possibleActions.add(0, action);
								} else
									possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column
							break;
						}
					}
				}

			}
		}
		return possibleActions;
	}

	/**
	 * @param state  current state
	 * @param action action done
	 * @return cloned state after pawn has been moved and checked if other pawns has
	 *         been captured
	 */
	@Override
	public State getResult(State state, Action action) {

		// move pawn
		state = this.movePawn(state.clone(), action);
		// check the state for any capture
		if (state.getTurn().equalsTurn("B")) {
			state = this.checkCaptureWhite(state, action);
		} else if (state.getTurn().equalsTurn("W")) {
			state = this.checkCaptureBlack(state, action);
		}
		return state;
	}

	@Override
	public double getUtility(State state, State.Turn turn) {

		// if it is a terminal state

		if ((turn.equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.BLACKWIN))
				|| (turn.equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.WHITEWIN)))
			return Double.POSITIVE_INFINITY;
		else if ((turn.equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.WHITEWIN))
				|| (turn.equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.BLACKWIN)))
			return Double.NEGATIVE_INFINITY;

		// if it isn't a terminal state
		Heuristics heuristics = null;
		if (turn.equals(State.Turn.WHITE)) {
			heuristics = new SheepHeuristics(state);
		} else {

			heuristics = new WolfHeuristics(state, turn);
		}
		return (double) heuristics.evaluateState();
	}

	@Override
	public boolean isTerminal(State state) {
		if (state.getTurn().equals(State.Turn.BLACKWIN) || state.getTurn().equals(State.Turn.WHITEWIN)
				|| state.getTurn().equals(State.Turn.DRAW))
			return true;
		return false;
	}

	@Override
	public State getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Turn getPlayer(State state) {

		return state.getTurn();
	}

	@Override
	public Turn[] getPlayers() {
		return null;
	}

}
