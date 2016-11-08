package messages;

import visidia.simulation.process.messages.Message;

@SuppressWarnings("serial")
public class TokenMessage extends Message {

	int target;
	
	public TokenMessage(int target) {
		this.target = target;
	}

	@Override
	public Message clone() {
		return new TokenMessage(target);
	}

	public int getTarget() {
		return target;
	}

	@Override 
	public String toString() {

		String r = "TOKEN(" + target + ")";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}

}