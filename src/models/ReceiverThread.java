package models;

import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import interfaces.IStrategy;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class ReceiverThread implements Runnable {

    private static final int TIMEOUT = 32;
    private static final int PORT = 55321;
    private static final int RECORDING_TIME = 35;
    private int mCurrentRecordingTime =0;

    private static DatagramSocket2 mReceivingSocket;
    private AudioPlayer mPlayer;
    private IStrategy mStrategy;

    public ReceiverThread(IStrategy strategy)
    {
        mStrategy = strategy;
    }

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
        //Open a socket to receive from on port PORT
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
                byte[] data = new byte[512];
                DatagramPacket packet = new DatagramPacket(data, 0, 512);

                if(mCurrentRecordingTime < Math.ceil(RECORDING_TIME / 0.032))
                {
                    mCurrentRecordingTime++;
                    System.out.println(""+mCurrentRecordingTime);
                }
                else
                {
                    mPlayer.close();
                    running = false;
                }

                mReceivingSocket.receive(packet);

                mStrategy.addPacket(packet.getData());

            }
            catch (SocketTimeoutException e)
            {
                System.out.println("Timeout.");
                mStrategy.handlePacketLoss();
            }
            catch (IOException e)
            {
                System.out.println("ERROR: TextReceiver: Some random IO error occurred!");
                e.printStackTrace();
            }

            Iterator<byte[]> voiceItr = mStrategy.getVoiceVector().iterator();
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
            mStrategy.getVoiceVector().clear();
        }

        //Close the socket
        if(!mReceivingSocket.isClosed())
        {
            mReceivingSocket.close();
        }
    }
}


