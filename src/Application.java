/**
 * Created by thomaspachico on 07/02/2017.
 */

public class Application {

    public static void main(String[] args) throws Exception {

        //ReceiverThread receiver = new ReceiverThread();
        SenderThread sender = new SenderThread("Samuels-MacBook-Pro");

        //receiver.start();
        sender.start();
    }

}
