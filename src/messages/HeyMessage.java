package messages;

import visidia.simulation.process.messages.Message;

@SuppressWarnings("serial")
public class HeyMessage extends Message {
	
	int value;
	int senderProc;

	public int getSender() {
		return senderProc;
	}

	public HeyMessage( int value , int senderProc) {

		this.value = value;
		this.senderProc = senderProc;
	}

	public int getValue() {

		return value;
	}


	@Override
	public Message clone() {
		return new HeyMessage(value, senderProc);
	}

	@Override 
	public String toString() {

		String r = "HEY(" + value + ")";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}

}