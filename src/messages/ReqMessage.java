package messages;

import visidia.simulation.process.messages.Message;

public class ReqMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1056961464496603947L;
	
	int proc;

	public ReqMessage( int p ) {

		this.proc = p;
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