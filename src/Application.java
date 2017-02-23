import models.*;
import views.MainForm;

import javax.swing.*;

public class Application {


    public static void main(String[] args) throws Exception
    {
        /*
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        */

        //192.168.0.21 - Sam
        //192.168.0.11 - Tom

        FillingStrategy fill = new FillingStrategy();
        RepetitionStrategy repeat = new RepetitionStrategy();
        GenericStrategy generic = new GenericStrategy();

        ReceiverThread receiver = new ReceiverThread(repeat, DatagramType.SOCKET4);
        SenderThread sender = new SenderThread("localhost", DatagramType.SOCKET4,
                TransmissionType.VOICE,false);

        receiver.start();
        sender.start();

    }


}
