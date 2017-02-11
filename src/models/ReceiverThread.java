package models;

import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import interfaces.IStrategy;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class ReceiverThread implements Runnable {

    private static final int TIMEOUT = 32;
    private static final int PORT = 55321;
    private static final int RECORDING_TIME = 35;
    private int mCurrentRecordingTime =0;
    private PacketIO mPacketiser;

    private Vector<VoicePacket> mQueue;

    private static DatagramSocket2 mReceivingSocket;
    private AudioPlayer mPlayer;
    private IStrategy mStrategy;

    public ReceiverThread(IStrategy strategy)
    {
        mPacketiser = new PacketIO();
        mStrategy = strategy;
        mQueue = new Vector<VoicePacket>();
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run ()
    {
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

        while(mQueue.size() < 4)
        {
            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                mQueue.add(vp);
                System.out.println("Unknown packet added to queue!");
            }
            catch (SocketTimeoutException ex)
            {
                //ex.printStackTrace();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        switch (mQueue.elementAt(0).getCurrentType())
        {
            case VOICE:
                break;
            case TEST:
                receiveTestTransmission();
                break;
            default:
                break;
        }

        for (VoicePacket vp : mQueue)
        {
            System.out.println(vp.toString());
        }

        /*
        while (running)
        {
            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                mQueue.add(vp);
            }
            catch (Exception ex)
            {

            }

            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
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
        */

        //Close the socket
        if(!mReceivingSocket.isClosed())
        {
            mReceivingSocket.close();
        }
    }

    public void receiveTestTransmission()
    {
        boolean receiving = true;

        try
        {
            mReceivingSocket.setSoTimeout(3000);
        }
        catch (SocketException ex)
        {
            System.out.println("RECEIVING TEST ERROR");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        while(receiving)
        {
            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                if(vp.getCurrentType() != null)
                {
                    mQueue.add(vp);
                    System.out.println("Test packet added to queue!");
                }
            }
            catch (SocketTimeoutException ex)
            {
                receiving = false;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        System.out.println("Packets Received: "+mQueue.size()+"/2000");
        System.out.println("Total packets lost: "+(2000-mQueue.size()));
    }

    public void receiveVoiceTransmission()
    {
        try
        {
            mPlayer = new AudioPlayer();
        }
        catch (LineUnavailableException ex)
        {
            //TODO handle LineUnavailable exception
        }
    }
}


