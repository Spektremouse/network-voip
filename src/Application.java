import models.*;

public class Application {

    public static void main(String[] args) throws Exception
    {
        //192.168.0.21
        //192.168.0.11

        FillingStrategy fill = new FillingStrategy();
        RepetitionStrategy repeat = new RepetitionStrategy();
        GenericStrategy generic = new GenericStrategy();

        ReceiverThread receiver = new ReceiverThread(repeat, DatagramType.SOCKET4);
        SenderThread sender = new SenderThread("192.168.0.21", DatagramType.SOCKET4,
                TransmissionType.VOICE, true);

        receiver.start();
        sender.start();
    }

}
