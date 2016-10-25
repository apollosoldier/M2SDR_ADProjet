package messages;

import visidia.simulation.process.messages.Message;

public class TokenMessage extends Message {

	public TokenMessage() {

	}

	@Override
	public Message clone() {
		return new TokenMessage();
	}

	@Override 
	public String toString() {

		String r = "TOKEN";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}

}