package messages;

import visidia.simulation.process.messages.Message;

@SuppressWarnings("serial")
public class ReqMessage extends Message {
	
	int proc;
	int sender;

	public ReqMessage( int p , int sender) {

		this.proc = p;
		this.sender = sender;
	}

	public int getMsgProc() {

		return proc;
	}

	public int getSender() {
		return sender;
	}

	@Override
	public Message clone() {
		return new ReqMessage(proc, sender);
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