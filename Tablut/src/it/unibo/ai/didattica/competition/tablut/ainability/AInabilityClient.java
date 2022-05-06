package it.unibo.ai.didattica.competition.tablut.ainability;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.ainability.domain.CustomGameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.ainability.minmax.CustomIterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class AInabilityClient extends TablutClient {	
		private int turn =1;
	    private int game;
	    private boolean debug;
	    private ArrayList<String> SheepAphorism;
	    private ArrayList<String> WolfAphorism;
	    private Random rand = new Random();

	    public AInabilityClient(String player, String name, int timeout, String ipAddress, int game, boolean debug) throws UnknownHostException, IOException {
	        super(player, name, timeout, ipAddress);
	        this.game = game;
	        AInabilityAphorism aphorism = new AInabilityAphorism();
	        this.debug = debug;
	        this.SheepAphorism = aphorism.getSheepAphorism();
	        this.WolfAphorism = aphorism.getWolfAphorism();
	    }

	    public static void main(String[] args) throws IOException {
	        int gameType = 4;
	        String role = "";
	        String name = "ArtificialInability";
	        String ipAddress = "localhost";
	        int timeout = 60;
	        boolean debug = false;

	        if (args.length < 1) {
	        	System.out.println("Insert player mode! (WHITE or BLACK)");
	            System.out.println("USAGE: ./runmyplayer <BLACK|WHITE> <timeout-in-seconds> <server-ip> <debug(optional)>");
	            System.exit(-1);
	        } else {
	            role = (args[0]);
	        }
	        if (args.length == 2) {
	            try {
	                timeout = Integer.parseInt(args[1]);
	            } catch (NumberFormatException e){
	                System.out.println("Timeout must be an integer representing seconds");
	                System.out.println("USAGE: ./runmyplayer <BLACK|WHITE> <timeout-in-seconds> <server-ip> <debug(optional)>");
	                System.exit(-1);
	            }
	        }
	        if (args.length == 3) {
	            try {
	                timeout = Integer.parseInt(args[1]);
	            } catch (NumberFormatException e){
	                System.out.println("Timeout must be an integer representing seconds");
	                System.out.println("USAGE: ./runmyplayer <BLACK|WHITE> <timeout-in-seconds> <server-ip> <debug(optional)>");
	                System.exit(-1);
	            }
	            ipAddress = args[2];
	        }

	        if (args.length == 4) {
	            try {
	                timeout = Integer.parseInt(args[1]);
	            } catch (NumberFormatException e){
	                System.out.println("Timeout must be an integer representing seconds");
	                System.out.println("USAGE: ./runmyplayer <BLACK|WHITE> <timeout-in-seconds> <server-ip> <debug(optional)>");
	                System.exit(-1);
	            }
	            ipAddress = args[2];
	            if(args[3].equals("debug")) {
	                debug = true;
	            } else {
	                System.out.println("The last argument can be only 'debug'");
	                System.out.println("USAGE: ./runmyplayer <BLACK|WHITE> <timeout-in-seconds> <server-ip> <debug(optional)>");
	                System.exit(-1);
	            }
	        }

	        AInabilityClient client = new AInabilityClient(role, name, timeout, ipAddress, gameType, debug);
	        client.run();
	    }

	    @Override
	    public void run() {

	        // send name of your group to the server saved in variable "name"
	        try {
	            this.declareName();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        // set type of state and WHITE must do the first player
	        State state = new StateTablut();	      
	        
	        CustomGameAshtonTablut tablutGame = new CustomGameAshtonTablut(0, -1, "logs", "white_ai", "black_ai");


	        // attributes depends to parameters passed to main
	        System.out.println("\r\n"
	        		+ "                _   _  __ _      _       _ \r\n"
	        		+ "     /\\        | | (_)/ _(_)    (_)     | |\r\n"
	        		+ "    /  \\   _ __| |_ _| |_ _  ___ _  __ _| |\r\n"
	        		+ "   / /\\ \\ | '__| __| |  _| |/ __| |/ _` | |\r\n"
	        		+ "  / ____ \\| |  | |_| | | | | (__| | (_| | |\r\n"
	        		+ " /_/___ \\_\\_|   \\__|_|_| |_|\\___|_|\\__,_|_|\r\n"
	        		+ " |_   _|           | |   (_) (_) |         \r\n"
	        		+ "   | |  _ __   __ _| |__  _| |_| |_ _   _  \r\n"
	        		+ "   | | | '_ \\ / _` | '_ \\| | | | __| | | | \r\n"
	        		+ "  _| |_| | | | (_| | |_) | | | | |_| |_| | \r\n"
	        		+ " |_____|_| |_|\\__,_|_.__/|_|_|_|\\__|\\__, | \r\n"
	        		+ "                                     __/ | \r\n"
	        		+ "                                    |___/  \r\n"
	        		+ "");
	        System.out.println("AInabilityClient: " + (this.getPlayer().equals(State.Turn.BLACK) ? "BLACK" : "WHITE" ));
	        System.out.println("Timeout: " + this.timeout +" s");
	        System.out.println("Server: " + this.serverIp);
	        System.out.println("Debug mode: " + this.debug+"\n");


	        // still alive until you are playing
	        while (true) {

	            // read the current state from the server
	            try {
	                this.read();
	            } catch (ClassNotFoundException | IOException e1) {
	                e1.printStackTrace();
	                System.exit(1);
	            }

	            // print current state
	           
	            state = this.getCurrentState();	           
	           



	            // if player == WHITE
	            if (this.getPlayer().equals(State.Turn.WHITE)) {
	            	
	                // if is my turn (WHITE)
	                if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
	                	 Action a=null;
	                	System.out.println("TURNO: "+ this.turn);
		                System.out.println("     Picking a magical move from the hat... ");
	                    System.out.println("     "+this.SheepAphorism.get(this.rand.nextInt(this.SheepAphorism.size()))+ " \n");
	                    
	                    
	                    
	                    
	                    // search the best move in search tree
	                    long startTime = System.nanoTime();
	                    //opening
	                   
	                    a = findBestMove(tablutGame, state);                  
	                  
	                    
                        long stopTime = System.nanoTime();
                        //System.out.println("Time elapsed: " + (double)(stopTime - startTime)/1000000000+ " seconds");
                        System.out.println("     A sheep moved " + a.toString());

	                    try {
	                        this.write(a);
	                    } catch (ClassNotFoundException | IOException e) {
	                        e.printStackTrace();
	                    }
	                    this.turn++;
	                }

	                // if is turn of oppenent (BLACK)
	                else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
	                	this.turn++;
	                  //  System.out.println("Waiting for your opponent move...\n");
	                }
	                // if I WIN
	                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
	                    System.out.println("YOU WIN!");
	                    System.exit(0);
	                }
	                // if I LOSE
	                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
	                    System.out.println("YOU LOSE!");
	                    System.exit(0);
	                }
	                // if DRAW
	                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
	                    System.out.println("DRAW!");
	                    System.exit(0);
	                }

	            }
	            // if i'm BLACK
	            else {

	                // if is my turn (BLACK)
	                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {

	                	System.out.println("TURNO: "+ this.turn);
	                    System.out.println("     Picking a magical move from the hat... ");
	                    System.out.println("     "+this.WolfAphorism.get(this.rand.nextInt(this.WolfAphorism.size()))+" \n");
	                    // search the best move in search tree
	                    long startTime = System.nanoTime();
	                    Action a = findBestMove(tablutGame, state);
	                    
                        long stopTime = System.nanoTime();
                       // System.out.println("Time elapsed: " + (double)(stopTime - startTime)/1000000000+ " seconds");
                        System.out.println("     A wolf moved " + a.toString());

	                    try {
	                        this.write(a);
	                    } catch (ClassNotFoundException | IOException e) {
	                        e.printStackTrace();
	                    }
	                    this.turn++;
	                }

	                // if is turn of opponent (WHITE)
	                else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
	           //         System.out.println("Waiting for your opponent move...\n");
	                	  this.turn++;
	                }

	                // if I LOSE
	                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
	                    System.out.println("YOU LOSE!");
	                    System.exit(0);
	                }

	                // if I WIN
	                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
	                    System.out.println("YOU WIN!");
	                    System.exit(0);
	                }

	                // if DRAW
	                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
	                    System.out.println("DRAW!");
	                    System.exit(0);
	                }
	            }
	        }
	    }

	    
	    private boolean NoBlackInUpperQuadrant(State state){
	    	for(int i=0;i<9;i++)
	    	{
	    		if(state.getPawn(2, i).equals(State.Pawn.BLACK))return false;
	    	}
	    	return true;
	    }

	    /**
	     * Method that find a suitable moves searching in game tree
	     * @param tablutGame Current game
	     * @param state Current state
	     * @return Action that is been evaluated as best
	     */
	    private Action findBestMove(CustomGameAshtonTablut tablutGame, State state) {

	    	CustomIterativeDeepeningAlphaBetaSearch search = new CustomIterativeDeepeningAlphaBetaSearch(tablutGame, Double.MIN_VALUE, Double.MAX_VALUE, this.timeout - 2 );
	        search.setLogEnabled(debug);
	        return search.makeDecision(state);
	    }
	
}