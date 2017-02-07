import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class ReceiverThread implements Runnable {

    private static final int TIMEOUT = 320;
    private static final int PORT = 55321;

    private static DatagramSocket2 mReceivingSocket;
    private AudioPlayer mPlayer;
    private IStrategy mStrategy;

    Vector<byte[]> mVoiceVector = new Vector<byte[]>();


    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run ()
    {
        try
        {
             mPlayer = new AudioPlayer();
        }
        catch (LineUnavailableException ex)
        {
            //TODO handle LineUnavailable exception
        }

        //***************************************************
        //Port to open socket on
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try
        {
            mReceivingSocket = new DatagramSocket2(PORT);
            mReceivingSocket.setSoTimeout(TIMEOUT);
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

        while (running)
        {
            try
            {
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] data = new byte[512];
                DatagramPacket packet = new DatagramPacket(data, 0, 512);

                mReceivingSocket.receive(packet);

                mVoiceVector.add(packet.getData());
            }
            catch (SocketTimeoutException e)
            {
                System.out.println("Timeout.");
            }
            catch (IOException e)
            {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }

            if(mVoiceVector.size() > 9)
            {
                Iterator<byte[]> voiceItr = mVoiceVector.iterator();
                try
                {
                    while (voiceItr.hasNext())
                    {
                        mPlayer.playBlock(voiceItr.next());
                    }
                }
                catch (IOException ex)
                {

                }
                mVoiceVector.clear();
            }

        }

        //Close the socket
        if(!mReceivingSocket.isClosed())
        {
            mReceivingSocket.close();
        }
        //***************************************************
    }
}


