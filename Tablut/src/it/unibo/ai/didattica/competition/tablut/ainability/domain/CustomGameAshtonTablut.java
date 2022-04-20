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
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;



public class CustomGameAshtonTablut extends GameAshtonTablut implements aima.core.search.adversarial.Game<State, Action, State.Turn>{



	public CustomGameAshtonTablut(int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName,
			String blackName) {
		super(repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
	}

	/**
	 * 
	 * 
	 * @param state current state
	 * @param a action to check
	 * @return if the action is possible return true else an exception is throws
	 *
	 * @throws BoardException
	 * @throws ActionException
	 * @throws StopException
	 * @throws PawnException
	 * @throws DiagonalException
	 * @throws ClimbingException
	 * @throws ThroneException
	 * @throws OccupitedException
	 * @throws ClimbingCitadelException
	 * @throws CitadelException
	 */
	public boolean isPossibleMove(State state, Action a)
			throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException,
			ThroneException, OccupitedException, ClimbingCitadelException, CitadelException {
		this.loggGame.fine(a.toString());
// controllo la mossa
		if (a.getTo().length() != 2 || a.getFrom().length() != 2) {
			this.loggGame.warning("Formato mossa errato");
			throw new ActionException(a);
		}
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();

// controllo se sono fuori dal tabellone
		if (columnFrom > state.getBoard().length - 1 || rowFrom > state.getBoard().length - 1
				|| rowTo > state.getBoard().length - 1 || columnTo > state.getBoard().length - 1 || columnFrom < 0
				|| rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			this.loggGame.warning("Mossa fuori tabellone");
			
			throw new BoardException(a);
		}

// controllo che non vada sul trono
		if (state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString())) {
			this.loggGame.warning("Mossa sul trono");
			throw new ThroneException(a);
		}

// controllo la casella di arrivo
		if (!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString())) {
			this.loggGame.warning("Mossa sopra una casella occupata");
			throw new OccupitedException(a);
		}
		if (this.citadels.contains(state.getBox(rowTo, columnTo))
				&& !this.citadels.contains(state.getBox(rowFrom, columnFrom))) {
			this.loggGame.warning("Mossa che arriva sopra una citadel");
			throw new CitadelException(a);
		}
		if (this.citadels.contains(state.getBox(rowTo, columnTo))
				&& this.citadels.contains(state.getBox(rowFrom, columnFrom))) {
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(a);
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(a);
				}
			}

		}

// controllo se cerco di stare fermo
		if (rowFrom == rowTo && columnFrom == columnTo) {
			this.loggGame.warning("Nessuna mossa");
			throw new StopException(a);
		}

// controllo se sto muovendo una pedina giusta
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("W")
					&& !state.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
				this.loggGame.warning("Giocatore " + a.getTurn() + " cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}
		if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
				this.loggGame.warning("Giocatore " + a.getTurn() + " cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}

// controllo di non muovere in diagonale
		if (rowFrom != rowTo && columnFrom != columnTo) {
			this.loggGame.warning("Mossa in diagonale");
			throw new DiagonalException(a);
		}

// controllo di non scavalcare pedine
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if (this.citadels.contains(state.getBox(rowFrom, i))
							&& !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if (this.citadels.contains(state.getBox(rowFrom, i))
							&& !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if (this.citadels.contains(state.getBox(i, columnFrom))
							&& !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if (this.citadels.contains(state.getBox(i, columnFrom))
							&& !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param state current state 
	 * @return a list of p
	 */
	

	public List<Action> getActions(State state) {
		State.Turn turn = state.getTurn();

		List<Action> possibleActions = new ArrayList<Action>();

		int i,j,k;
		String from, to;
		Action action;
		
		//for each cell on tablut:
		for ( i = 0; i < state.getBoard().length; i++) {
			for ( j = 0; j < state.getBoard().length; j++) {

				// if pawn color is equal of turn color
				if (state.getPawn(i, j).toString().equals(turn.toString()) ||
						(state.getPawn(i, j).equals(State.Pawn.KING) && turn.equals(State.Turn.WHITE)) ) {

					// check cells above the current one
					for ( k=i-1; k>=0; k--) {

						// break if you are going up to the citadels after exiting
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j))) {
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

							//if action is possible add it to possibleActions
							try {
								isPossibleMove(state.clone(), action);
								possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column 
							break;
						}
					}

					// check cells under the current one
					for ( k=i+1; k<state.getBoard().length; k++) {

						// break if you are going up to the citadels after exiting
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(k, j))) {
							break;
						}

						
						else if (state.getPawn(k, j).equalsPawn(State.Pawn.EMPTY.toString())){

							 from = state.getBox(i, j);
							 to = state.getBox(k, j);

							 action = null;
							try {
								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							//if action is possible add it to possibleActions
							try {
								isPossibleMove(state.clone(), action);
								possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column 
							break;
						}
					}

					// check cells to the left of the current one
					for ( k=j-1; k>=0; k--) {


						// break if you are going up to the citadels after exiting
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k))) {
							break;
						}

						// check if we are moving on a empty cell
						else if (state.getPawn(i, k).equalsPawn(State.Pawn.EMPTY.toString())){

							 from = state.getBox(i, j);
							 to = state.getBox(i, k);

							 action = null;
							try {
								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							//if action is possible add it to possibleActions
							try {
								isPossibleMove(state.clone(), action);
								possibleActions.add(action);

							} catch (Exception e) {

							}
						} else {
							// there is a pawn in a cell of the column 
							break;
						}
					}

					// check cells on the right of the current one
					for ( k=j+1; k<state.getBoard().length; k++) {


						// break if you are going up to the citadels after exiting
						if (!citadels.contains(state.getBox(i, j)) && citadels.contains(state.getBox(i, k))) {
							break;
						}

						// check if we are moving on a empty cell
						else if (state.getPawn(i, k).equalsPawn(State.Pawn.EMPTY.toString())){

							 from = state.getBox(i, j);
							 to = state.getBox(i, k);

							 action = null;
							try {
								action = new Action(from, to, turn);
							} catch (IOException e) {
								e.printStackTrace();
							}

							//if action is possible add it to possibleActions
							try {
								isPossibleMove(state.clone(), action);
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

	
	
	@Override
	public State getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	@Override
	public Turn getPlayer(State arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Turn[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param state current state
	 * @param action action done
	 * @return cloned state after pawn has been moved and checked if other pawns has been captured
	 */
	@Override
	public State getResult(State state, Action action) {

		// move pawn
		state = this.movePawn(state.clone(), action);

		// check the state for any capture
		if (state.getTurn().equalsTurn("B")) {
			state = this.checkCaptureWhite(state, action);
		}else if (state.getTurn().equalsTurn("W")) {
			state = this.checkCaptureBlack(state, action);
		}
		return state;
	}

	@Override
	public double getUtility(State state, State.Turn turn) {
		
		turn = state.getTurn();
		//System.out.println("******** "+turn+" ********");
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
			heuristics = new WolfHeuristics(state);
		}
		return  heuristics.evaluateState();
	}

	@Override
	public boolean isTerminal(State state) {
		if(state.getTurn().equals(State.Turn.BLACKWIN) || state.getTurn().equals(State.Turn.WHITEWIN) || state.getTurn().equals(State.Turn.DRAW)) return true;
		return false;
	}


	
}
