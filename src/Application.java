import models.*;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class Application {

    public static void main(String[] args) throws Exception {

        SplicingStrategy splice = new SplicingStrategy();
        FillingStrategy fill = new FillingStrategy();
        RepititionStrategy repeat = new RepititionStrategy();

        ReceiverThread receiver = new ReceiverThread(splice);
        SenderThread sender = new SenderThread("192.168.0.21");

        receiver.start();
        sender.start();
    }

}
