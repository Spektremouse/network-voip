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



        SplicingStrategy splice = new SplicingStrategy();
        FillingStrategy fill = new FillingStrategy();
        RepititionStrategy repeat = new RepititionStrategy();

        ReceiverThread receiver = new ReceiverThread(repeat);
        SenderThread sender = new SenderThread("169.254.124.240", DatagramType.SOCKET2, TransmissionType.VOICE);

        receiver.start();
        sender.start();

    }

}
