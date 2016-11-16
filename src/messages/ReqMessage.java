package messages;

import visidia.simulation.process.messages.Message;

@SuppressWarnings("serial")
public class ReqMessage extends Message {
	
	int requester;
	int sender;

	public ReqMessage( int requester , int sender) {

		this.requester = requester;
		this.sender = sender;
	}

	public int getRequester() {

		return requester;
	}

	public int getSender() {
		return sender;
	}

	@Override
	public Message clone() {
		return new ReqMessage(requester, sender);
	}

	@Override 
	public String toString() {

		String r = "REQ(" + requester + ")";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}

}