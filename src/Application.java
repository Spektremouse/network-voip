import models.*;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import java.net.DatagramSocket;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class Application {

    public static void main(String[] args) throws Exception {

        //192.168.0.21
        //192.168.0.11

        SplicingStrategy splice = new SplicingStrategy();
        FillingStrategy fill = new FillingStrategy();
        RepititionStrategy repeat = new RepititionStrategy();
        GenericStrategy generic = new GenericStrategy();

        ReceiverThread receiver = new ReceiverThread(splice);
        SenderThread sender = new SenderThread("192.168.0.21", DatagramType.SOCKET2, TransmissionType.VOICE);

        receiver.start();
        sender.start();

    }

}
