package messages;

import visidia.simulation.process.messages.Message;

public class HeyMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1946016157175197162L;
	int proc;

	public HeyMessage( int p ) {

		proc = p;
	}

	public int getMsgProc() {

		return proc;
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