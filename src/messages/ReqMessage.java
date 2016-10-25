package messages;

import visidia.simulation.process.messages.Message;

public class ReqMessage extends Message {

	int proc;

	public ReqMessage( int p ) {

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