package it.unibo.ai.didattica.competition.tablut.ainability;

import java.io.IOException;
import java.net.UnknownHostException;

public class AInabilityWhiteClient {


	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
	       String[] array = new String[]{"WHITE", "20", "localhost", "debug"};
	       if (args.length>0){
	            array = new String[]{"WHITE", args[0]};
	       }
	       AInabilityClient.main(array);
	   }
}
