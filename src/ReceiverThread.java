import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class ReceiverThread implements Runnable{

    private static DatagramSocket receiving_socket;
    private AudioPlayer player;
    private Vector<byte[]> voiceVector = new Vector<byte[]>();

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run (){

        try
        {
             player = new AudioPlayer();
        }
        catch (LineUnavailableException ex)
        {
            //TODO handle LineUnavailable exception
        }

        //***************************************************
        //Port to open socket on
        int PORT = 55321;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try
        {
            receiving_socket = new DatagramSocket(PORT);
            receiving_socket.setSoTimeout(3000);
        }
        catch (SocketException e)
        {
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;

        while (running){

            try
            {
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] data = new byte[512];
                DatagramPacket packet = new DatagramPacket(data, 0, 512);

                receiving_socket.receive(packet);

                voiceVector.add(packet.getData());
            }
            catch (SocketTimeoutException e)
            {
                System.out.println("Timeout.");
                running = false;
            }
            catch (IOException e)
            {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }

        //Close the socket
        if(!receiving_socket.isClosed())
        {
            receiving_socket.close();
        }
        //***************************************************

        playAudio();
    }

    public void playAudio()
    {
        Iterator<byte[]> voiceItr = voiceVector.iterator();
        try
        {
            while (voiceItr.hasNext()) {
                player.playBlock(voiceItr.next());
            }
        }
        catch (IOException ex)
        {
            //TODO Handled IO exception
        }

    }
}


