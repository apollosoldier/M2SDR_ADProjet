package naimiTrehel;


import java.util.Random;

import frame.DisplayFrame;
import messages.HeyMessage;
import messages.ReqMessage;
import messages.TokenMessage;
//Visidia imports
import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;

public class NaimiTrehel extends Algorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5664520777140308746L;

	// Higher speed means lower simulation speed
	int speed = 4;

	// All nodes data
	int procId;
	int nbNeighbors;
	int[] neighborDoors;
	
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
		System.out.println("Process " + procId + " as " + nbNeighbors 
				+ " neighbors");	

		rr = new ReceptionRules( this );
		rr.start();

		neighborDoors = new int[nbNeighbors+1];

		// Set initial token
		if ( procId == 0 ) {
			AJ = true;
			owner = -1;
		} else {
			owner = 0;
		}
		
		for (int i = 0; i<nbNeighbors; i++) {
				HeyMessage hm = new HeyMessage(procId);
				sendTo(i, hm);
		}

		// Display initial state
		df = new DisplayFrame( procId );
		displayState();
		try { Thread.sleep( 15000 ); } catch( InterruptedException ie ) {}

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

	// Rule 1 : init
	synchronized void receiveHEY(int p, int d) {
		System.out.println("Process " + procId + " reveiced HEY from " + p);
		neighborDoors[p] = d;
	}

	// Rule 2 : ask for critical section
	synchronized void askForCritical() {
		
		if ( owner != -1 ) {

			ReqMessage rm = new ReqMessage(procId);
			
			System.out.println("Process " + procId + " send REQ to " + owner);
			sendTo( neighborDoors[owner], rm );
			
			owner = -1;
			
			displayState();

			while ( AJ == false ) {
				try { this.wait(); } catch( InterruptedException ie) {}
			}
		}
	}

	// Rule 3 : receive REQ( H )
	synchronized void receiveREQ( int p, int d){

		System.out.println("Process " + procId + " reveiced REQ from " + p);
		
		if (owner == -1) {
			if (SC == true || waitForCritical == true) {
				next = p;
			} else {
				AJ = false;
				TokenMessage tm = new TokenMessage();
				System.out.println("Process " + procId + " send TOKEN to " + p);
				sendTo(neighborDoors[p], tm);
			}
		} else {
			ReqMessage rm = new ReqMessage(p);
			System.out.println("Process " + procId + " send REQ to " + owner);
			sendTo( neighborDoors[owner], rm );
		}
		owner = p;

		displayState();

	}

	// Rule 4 : receive TOKEN
	synchronized void receiveTOKEN(){

		System.out.println("Process " + procId + " reveiced TOKEN ");
		AJ = true;
		displayState();

		this.notify();
	}

	// Rule 5 : exit critical section
	void endCriticalUse() {

		SC = false;
		
		if (next != -1) {
			AJ = false;
			TokenMessage tm = new TokenMessage();
			System.out.println("Process " + procId + " send TOKEN to " + next);
			sendTo(neighborDoors[next], tm);
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

		String state = new String("Owner = " + owner + "\n");
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