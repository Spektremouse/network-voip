/**
 * Created by thomaspachico on 07/02/2017.
 */

public class Application {

    public static void main(String[] args) throws Exception {

        //ReceiverThread receiver = new ReceiverThread();
        SenderThread sender = new SenderThread();

        //receiver.start();
        sender.start();
    }

}
