package messages;

import visidia.simulation.process.messages.Message;

public class TokenMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8144379619118742474L;

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