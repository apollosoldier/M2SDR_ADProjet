package naimiTrehel;

import messages.HeyMessage;
import messages.ReqMessage;
import messages.TokenMessage;
import visidia.simulation.process.messages.Door;
import visidia.simulation.process.messages.Message;

//Reception thread
public class ReceptionRules extends Thread {

	NaimiTrehel algo;

	public ReceptionRules( NaimiTrehel a ) {

		algo = a;
	}

	public void run() {

		Door d = new Door();

		while( true ) {

			Message m = algo.recoit(d);
			int door = d.getNum();

			if ( m instanceof HeyMessage) { 
				HeyMessage hm = (HeyMessage) m;
				algo.receiveHEY( hm.getMsgProc(), door );
			} else if ( m instanceof ReqMessage ) {
				ReqMessage rm = (ReqMessage) m;
				algo.receiveREQ( rm.getMsgProc(), door );
			} else if ( m instanceof TokenMessage ) {
				algo.receiveTOKEN(  );
			} else {
				System.out.println("Error message type");
			}
		}
	}
}
