package messages;

import visidia.simulation.process.messages.Message;

public class LeaderMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3352001379692674251L;
	int proc;
	int senderProc;

	public int getSenderProc() {
		return senderProc;
	}

	public LeaderMessage( int p , int senderProc) {
		this.proc = p;
		this.senderProc = senderProc;
	}

	public int getMsgProc() {

		return proc;
	}


	@Override
	public Message clone() {
		return new LeaderMessage( proc, senderProc );
	}

	@Override 
	public String toString() {

		String r = "LEADER(" + proc + ")";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}
	
}
