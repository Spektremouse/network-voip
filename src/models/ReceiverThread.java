package models;

import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import interfaces.IStrategy;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class ReceiverThread implements Runnable {

    private static final int TIMEOUT = 32;
    private static final int PORT = 55321;
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
                receiveVoiceTransmission();
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
            ex.printStackTrace();
            System.exit(1);
            //TODO handle LineUnavailable exception
        }

        boolean running = true;
        int currentPlace = 0;

        while (running)
        {
            try
            {
                mPlayer.playBlock(mQueue.elementAt(currentPlace).getPayload());
                currentPlace++;

                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                if(vp.getCurrentType() != null)
                {
                    mQueue.add(vp);
                    System.out.println("Voice packet added to queue!");
                }

                if(currentPlace == mQueue.size())
                {
                    running = false;
                }

            }
            catch (SocketTimeoutException e)
            {
                currentPlace--;
                System.out.println("Timeout.");
                mStrategy.handlePacketLoss();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}


