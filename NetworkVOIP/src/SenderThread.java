import CMPC3M06.AudioRecorder;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class SenderThread implements Runnable{

    private static DatagramSocket sending_socket;
    private Vector<byte[]> voiceVector = new Vector<byte[]>();
    private static final int RECORDING_TIME = 35;
    private static final int PACKET_SIZE = 512;
    private AudioRecorder recorder;


    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run ()
    {
        recordAudio();

        //***************************************************
        //Port to send to
        int PORT = 55321;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try
        {
            clientIP = InetAddress.getByName("Samuel-MacBook-Pro");  //CHANGE localhost to IP or NAME of client machine
        }
        catch (UnknownHostException e)
        {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        //DatagramSocket sending_socket;
        try
        {
            sending_socket = new DatagramSocket();
        }
        catch (SocketException e)
        {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.
        try
        {
            for (byte[] data : voiceVector)
            {
                DatagramPacket packet = new DatagramPacket(data, PACKET_SIZE, clientIP, PORT);
                sending_socket.send(packet);
            }
        }
        catch (IOException ex)
        {
            //TODO Handle IO Exception
            System.out.println("ERROR: TextSender: Some random IO error occured!");
            ex.printStackTrace();
        }


        if(!sending_socket.isClosed())
        {
            //Close the socket
            sending_socket.close();
        }
    }

    private void recordAudio()
    {
        try
        {
            recorder = new AudioRecorder();
        }
        catch (LineUnavailableException ex)
        {
            //TODO handle LineUnavailable exception
        }

        try
        {
            for (int i = 0; i < Math.ceil(RECORDING_TIME / 0.032); i++) {
                byte[] block = recorder.getBlock();
                voiceVector.add(block);
            }

        }
        catch (IOException ex)
        {
            //TODO handle IO exception
        }

        //Close audio input
        recorder.close();
    }
}
