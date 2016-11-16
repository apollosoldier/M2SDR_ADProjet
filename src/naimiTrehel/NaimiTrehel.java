package naimiTrehel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import frame.DisplayFrame;
import messages.HeyMessage;
import messages.LeaderMessage;
import messages.ReqMessage;
import messages.TokenMessage;
//Visidia imports
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;

@SuppressWarnings("serial")
public class NaimiTrehel extends Algorithm {
	
	private final Integer[] initiators = {0,2,5};

	// Higher speed means lower simulation speed
	int speed = 4;

	// All nodes data
	int procId;
	int nbNeighbors;
	HashMap<Integer, Integer> neighborDoors = new HashMap<Integer, Integer>(nbNeighbors*3/2);

	int father = -1;
	int activeElec = -1;

	/** identifiant du site cense posseder le jeton ou qui est capable de remonter au jeton downer en owner**/
	int owner = -1;

	/**identifiant du site a qui envoyer le jeton**/
	int next = -1;
	
	/** nombre de messages HEY(value, sender) recus, tels que value=activeElec, initialise à 0 */
	int heyReceived = 0;

	/** nombre de messages LEADER(leader, sender) recus, initialise à 0 */
	int leaderReceived = 0;
	
	/** True si le proc a le jeton */
	boolean AJ = false;

	/** true si le proc attend la SC */
	boolean waitForCritical = false;

	/** True si le proc souhaite entreer ou est en SC */
	boolean SC = false;

	/** la porte a prendre etant donne une valeur entiere target pour remonter jusqua lorigine dune requete */
	HashMap<Integer, Integer> tokenDirections = new HashMap<Integer, Integer>();

	// Reception thread
	ReceptionRules rr = null;
	// State display frame
	DisplayFrame df; 	
	
	public String getDescription() {

		return ("Naimi-Tréhel Token Algorithm for Mutual Exclusion");
	}

	@Override
	public Object clone() {
		return new NaimiTrehel();
	}

	//
	// Nodes' code
	//
	@Override
	public void init() {

		// Initialization part
		procId = getId();
		Random rand = new Random( procId );
		nbNeighbors = getArity();
		System.out.println("Process " + procId + " has " + nbNeighbors 
				+ " neighbors");	

		ReceptionRules rr = new ReceptionRules( this );
		rr.start();

		// Set initial token
		if ( Arrays.asList(initiators).contains(procId) ) {
			activeElec = procId;
			for (int i = 0; i<nbNeighbors; i++) {
				HeyMessage hm = new HeyMessage(procId, procId);
				sendTo(i, hm);
			}
		}

		// Display initial state
		//while (leaderReceived < nbNeighbors) {
		try { Thread.sleep( 15000 ); } catch( InterruptedException ie ) {}
		//}
		if (owner == -1) {
			AJ = true;
		}
		df = new DisplayFrame( procId );
		displayState();

		//		VisidiaAck va = new VisidiaAck(Comm)

		while( true ) {

			// Wait for some time before simulation
			int time = ( 3 + rand.nextInt(10)) * speed * 1000;
			System.out.println("Process " + procId + " wait for " + time);
			try {
				Thread.sleep( time );
			} catch( InterruptedException ie ) {}

			// Try to access critical section
			System.out.println("Process " + procId + " ask for SC " );
			waitForCritical = true;
			askForCritical();
			SC = true;
			waitForCritical = false;

			displayState();

			// Simulate critical resource use
			time = (1 + rand.nextInt(2)) * 1000;
			System.out.println("Process " + procId + " enter SC " + time);
			try {
				Thread.sleep( time );
			} catch( InterruptedException ie ) {}
			System.out.println("Process " + procId + " exit SC ");

			// Release critical use
			SC = false;
			endCriticalUse();
		}
	}

	//--------------------
	// Rules
	//-------------------

	// Election rules
	synchronized void receiveHEY(int value, int sender, int door) {
		//les messages HEY visent a decider qui parmis les processus initiateurs a le plus grand id 
		System.out.println("Process " + procId + " reveiced HEY(" + value + ") from " + sender);
		neighborDoors.put(sender, door);
		if (activeElec < value || activeElec == -1) {//aveu de faiblesse
			activeElec = value;
			father = neighborDoors.containsKey(value)?value:sender;
			heyReceived = 1;
			if (nbNeighbors == 1) {//cas feuille
				HeyMessage hey = new HeyMessage(value, procId);
				sendTo(door, hey);
			} else {//cas noeud
				for (int i = 0; i<nbNeighbors; i++) {
					if (i != door) {
						HeyMessage hey = new HeyMessage(activeElec, procId);
						sendTo(i, hey);
					}
				}
			}
		} else if (value == activeElec) { 
			heyReceived++;
			if (heyReceived == nbNeighbors) {
				if (activeElec == procId) {//victoire
					for (int i = 0; i<nbNeighbors; i++) {
						LeaderMessage lm = new LeaderMessage(procId, procId);
						sendTo(i, lm);
					}
				} else {//aveu de faiblesse
					HeyMessage hey = new HeyMessage(activeElec, procId);
					sendTo(neighborDoors.get(father), hey);
				}
			}

		}
	}

	synchronized public void receiveLeader(int leader, int sender, int door) {
		System.out.println("Process " + procId + " reveiced LEADER(" + leader + ") from " + sender);
		neighborDoors.put(sender, door);
		boolean isNeighbour = neighborDoors.containsKey(leader);
		
		if (leaderReceived == 0) {
			owner = isNeighbour ? leader : sender;
			for (Integer processNb: neighborDoors.keySet()) {
				if (processNb != leader) {
					LeaderMessage lm = new LeaderMessage(leader, procId);
					sendTo(neighborDoors.get(processNb), lm);
				}
			}
		}
		if (isNeighbour) {
			owner = leader;
		}
		leaderReceived++;
	}

	// Rule 2 : ask for critical section
	synchronized void askForCritical() {

		if ( owner != -1 ) {

			ReqMessage rm = new ReqMessage(procId, procId);

			System.out.println("Process " + procId + " send REQ(" + procId + ") to " + owner);
			sendTo( neighborDoors.get(owner), rm );

			owner = -1;

			displayState();

			while ( AJ == false ) {
				try { this.wait(); } catch( InterruptedException ie) {}
			}
		}
	}

	// Rule 3 : receive REQ( H )
	synchronized void receiveREQ( int requester, int sender, int d){

		System.out.println("Process " + procId + " reveiced REQ(" + requester + ") from " + requester);

		boolean isNeighbor = neighborDoors.containsKey(requester);

		if (owner == -1) {
			if (SC == true || waitForCritical == true) {
				tokenDirections.put(requester, d);
				next = requester;
			} else {
				AJ = false;
				TokenMessage tm = new TokenMessage(requester);
				System.out.println("Process " + procId + " send TOKEN(" + requester +")  to " + (isNeighbor ? requester : sender));
				sendTo(neighborDoors.get(
						isNeighbor ? requester : sender), tm);
			}
		} else {
			tokenDirections.put(requester, d);
			ReqMessage rm = new ReqMessage(requester, procId);
			sendTo( neighborDoors.get(owner), rm );
		}
		owner = isNeighbor ? requester : sender;

		displayState();

	}

	// Rule 4 : receive TOKEN
	synchronized void receiveTOKEN(int target){
		System.out.println("Process " + procId + " reveiced TOKEN(" + target + ") ");
		if (target==procId) {
			AJ = true;
			displayState();
			this.notify();
		} else {
			int tokenDirection = neighborDoors.containsKey(target) ? neighborDoors.get(target) : tokenDirections.get(target);
			TokenMessage tm = new TokenMessage(target);
			sendTo(tokenDirection, tm);
			tokenDirections.remove(target);//clean memory of p
		}


	}

	// Rule 5 : exit critical section
	void endCriticalUse() {

		SC = false;

		if (next != -1) {
			AJ = false;
			TokenMessage tm = new TokenMessage(next);
			System.out.println("Process " + procId + " send TOKEN to " + next);
			sendTo(neighborDoors.containsKey(next)?
					neighborDoors.get(next):
						tokenDirections.get(next), tm);
			next = -1;

		}

		displayState();
	}

	// Access to receive function
	public Message recoit ( Door d ) {

		Message m = (Message)receive( d );
		return m;
	}

	// Display state
	void displayState() {

		String state = new String("Father = " + father + "\n");
		state = state + new String("Owner = " + owner + "\n");
		state = state + new String("Next = " + next + "\n");
		state = state + new String("AJeton = " + AJ + "\n");

		state = state + "--------------------------------------\n";
		if ( SC ) 
			state = state + "** ACCESS CRITICAL **";
		else if ( waitForCritical )
			state = state + "* WAIT FOR *";
		else
			state = state + "-- SLEEPING --";

		df.display( state );
	}

}
