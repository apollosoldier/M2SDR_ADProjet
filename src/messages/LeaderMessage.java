package messages;

import visidia.simulation.process.messages.Message;

@SuppressWarnings("serial")
public class LeaderMessage extends Message {
	
	int leader;
	int senderProc;

	public int getSender() {
		return senderProc;
	}

	public LeaderMessage( int leader , int senderProc) {
		this.leader = leader;
		this.senderProc = senderProc;
	}

	public int getLeader() {

		return leader;
	}


	@Override
	public Message clone() {
		return new LeaderMessage( leader, senderProc );
	}

	@Override 
	public String toString() {

		String r = "LEADER(" + leader + ")";
		return r;
	}

	@Override 
	public String getData() {

		return this.toString();
	}
	
}
