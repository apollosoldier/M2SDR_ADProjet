package naimiTrehel;

import messages.HeyMessage;
import messages.LeaderMessage;
import messages.ReqMessage;
import messages.TokenMessage;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;

//Reception thread
public class ReceptionRules extends Thread {

	NaimiTrehel algo;

	public ReceptionRules( NaimiTrehel a ) {

		this.algo = a;
	}
	

	public void run() {

		Door d = new Door();

		while( true ) {

			Message m = algo.recoit(d);
			int door = d.getNum();

			if ( m instanceof LeaderMessage) { 
				LeaderMessage lm = (LeaderMessage) m;
				algo.receiveLeader( lm.getLeader(), lm.getSender(), door);
			} else if ( m instanceof HeyMessage) { 
				HeyMessage hm = (HeyMessage) m;
				algo.receiveHEY( hm.getValue(), hm.getSender(), door);//TODO sender?
			} else if ( m instanceof ReqMessage ) {
				ReqMessage rm = (ReqMessage) m;
				algo.receiveREQ( rm.getRequester(), rm.getSender(), door );
			} else if ( m instanceof TokenMessage ) {
				TokenMessage t = (TokenMessage) m;
				algo.receiveTOKEN( t.getTarget() );
			} else {
				System.out.println("Error message type");
			}
		}
	}
}
