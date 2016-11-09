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
	HashMap<Integer, Integer> neighborDoors = new HashMap<Integer, Integer>(nbNeighbors+1, 1);

	int father = -1;
	int activeElec = -1;

	/** identifiant du site cense posseder le jeton**/
	int owner = -1;

	/**identifiant du site a qui envoyer le jeton**/
	int next = -1;

	/** True si le proc a le jeton */
	boolean AJ = false;

	/** true si le proc attend la SC */
	boolean waitForCritical = false;

	/** True si le proc souhaite entreer ou est en SC */
	boolean SC = false;

	// Reception thread
	ReceptionRules rr = null;
	// State display frame
	DisplayFrame df;

	int leaderReceived = 0;
	int heyReceived = 0;

	HashMap<Integer, Integer> tokenDirections = new HashMap<Integer, Integer>();

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
	synchronized void receiveHEY(int msgProc, int senderProc, int door) {
		System.out.println("Process " + procId + " reveiced HEY from " + msgProc);
		neighborDoors.put(senderProc, door);
		if (activeElec < msgProc || activeElec == -1) {//aveu de faiblesse
			activeElec = msgProc;
			father = neighborDoors.containsKey(msgProc)?msgProc:senderProc;
			heyReceived = 1;
			if (nbNeighbors == 1) {//cas feuille
				System.out.println("coucou2 " + heyReceived);
				HeyMessage hey = new HeyMessage(msgProc, procId);
				sendTo(door, hey);
			} else {//cas noeud
				for (int i = 0; i<nbNeighbors; i++) {
					if (i != door) {
						System.out.println("coucou3 " + heyReceived);
						HeyMessage hey = new HeyMessage(activeElec, procId);
						sendTo(i, hey);
					}
				}
			}
		} else if (msgProc == activeElec) { 
			System.out.println("coucou4 " + heyReceived);
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

	synchronized public void receiveLeader(int msgProc, int senderProc, int door) {
		System.out.println("Process " + procId + " reveiced LEADER from " + msgProc);
		neighborDoors.put(senderProc, door);
		boolean isNeighbour = neighborDoors.containsKey(msgProc);
		
		if (leaderReceived == 0) {
			owner = isNeighbour ? msgProc : senderProc;
			for (Integer processNb: neighborDoors.keySet()) {
				if (processNb != msgProc) {
					LeaderMessage lm = new LeaderMessage(msgProc, procId);
					sendTo(neighborDoors.get(processNb), lm);
				}
			}
		}
		if (isNeighbour) {
			owner = msgProc;
		}
		leaderReceived++;
	}

	// Rule 2 : ask for critical section
	synchronized void askForCritical() {

		if ( owner != -1 ) {

			ReqMessage rm = new ReqMessage(procId, procId);

			System.out.println("Process " + procId + " send REQ to " + owner);
			sendTo( neighborDoors.get(owner), rm );

			owner = -1;

			displayState();

			while ( AJ == false ) {
				try { this.wait(); } catch( InterruptedException ie) {}
			}
		}
	}

	// Rule 3 : receive REQ( H )
	synchronized void receiveREQ( int p, int senderProc, int d){

		System.out.println("Process " + procId + " reveiced REQ from " + p);

		boolean isNeighbor = neighborDoors.containsKey(p);

		if (owner == -1) {
			if (SC == true || waitForCritical == true) {
				tokenDirections.put(p, d);
				next = p;
			} else {
				AJ = false;
				TokenMessage tm = new TokenMessage(p);
				System.out.println("Process " + procId + " send TOKEN to " + p);
				sendTo(neighborDoors.get(
						isNeighbor ? p : senderProc), tm);
			}
		} else {
			tokenDirections.put(p, d);
			ReqMessage rm = new ReqMessage(p, procId);
			System.out.println("Process " + procId + " send REQ(" + p + ") to " + owner);
			sendTo( neighborDoors.get(owner), rm );
		}
		owner = isNeighbor ? p : senderProc;

		displayState();

	}

	// Rule 4 : receive TOKEN
	synchronized void receiveTOKEN(int p){
		System.out.println("Process " + procId + " reveiced TOKEN ");
		if (p==procId) {
			AJ = true;
			displayState();
			this.notify();
		} else {
			int tokenDirection = neighborDoors.containsKey(p) ? neighborDoors.get(p) : tokenDirections.get(p);
			TokenMessage tm = new TokenMessage(p);
			sendTo(tokenDirection, tm);
			tokenDirections.remove(p);//clean memory of p
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
