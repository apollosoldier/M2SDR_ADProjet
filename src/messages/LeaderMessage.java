package messages;

import visidia.simulation.process.messages.Message;

public class LeaderMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3352001379692674251L;
	int proc;
	int nbNeighbors;

	public LeaderMessage( int p, int nbNeighbors ) {

		this.proc = p;
		this.nbNeighbors = nbNeighbors;
	}

	public int getMsgProc() {

		return proc;
	}

	public int getMsgNbNeighbors() {
		return nbNeighbors;
	}

	@Override
	public Message clone() {
		return new ReqMessage(proc);
	}

	@Override 
	public String toString() {

		String r = "REQ(" + proc + ")";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}
	
}
