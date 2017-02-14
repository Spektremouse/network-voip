import models.*;

public class Application {

    public static void main(String[] args) throws Exception
    {
        //192.168.0.21
        //192.168.0.11

        SplicingStrategy splice = new SplicingStrategy();
        FillingStrategy fill = new FillingStrategy();
        RepetitionStrategy repeat = new RepetitionStrategy();
        GenericStrategy generic = new GenericStrategy();

        ReceiverThread receiver = new ReceiverThread(splice);
        SenderThread sender = new SenderThread("localhost", DatagramType.SOCKET2,
                TransmissionType.VOICE, true);

        receiver.start();
        sender.start();
    }

}
