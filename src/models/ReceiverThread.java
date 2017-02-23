package models;

import interfaces.IStrategy;
import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.security.InvalidParameterException;
import java.util.Collections;

/**
 *
 */
public class ReceiverThread implements Runnable
{

    private static final int TIMEOUT = 32;
    private static final int PORT = 55321;
    private PacketIO mPacketiser;

    private static DatagramSocket mReceivingSocket;
    private AudioPlayer mPlayer;
    private IStrategy mStrategy;

    public ReceiverThread(IStrategy strategy, DatagramType socketType) throws SocketException
    {
        switch (socketType)
        {
            case DEFAULT:
                mReceivingSocket = new DatagramSocket(PORT);
                break;
            case SOCKET2:
                mReceivingSocket = new DatagramSocket2(PORT);
                break;
            case SOCKET3:
                mReceivingSocket = new DatagramSocket3(PORT);
                break;
            case SOCKET4:
                mReceivingSocket = new DatagramSocket4(PORT);
                break;
            default:
                throw new InvalidParameterException("Invalid socket type.");
        }

        mReceivingSocket.setSoTimeout(TIMEOUT);
        mPacketiser = new PacketIO();
        mStrategy = strategy;
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run ()
    {
        System.out.println("Starting receiver...");

        System.out.println("Filling initial buffer...");
        while(mStrategy.getVoiceVector().size() < 4)
        {
            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                if(vp != null)
                {
                    mStrategy.getVoiceVector().add(vp);
                }
            }
            catch (SocketTimeoutException ex)
            {
                System.out.println("A timeout occurred while waiting for the buffer to fill.");
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        System.out.println("Initial buffer filled.");

        switch (mStrategy.getVoiceVector().get(0).getCurrentType())
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

        if(!mReceivingSocket.isClosed())
        {
            mReceivingSocket.close();
        }
        System.out.println("Finished receiver.");
    }

    public void receiveTestTransmission()
    {
        System.out.println("Running test receiver...");
        boolean receiving = true;
        int sampleSize = 200;

        try
        {
            mReceivingSocket.setSoTimeout(3000);
        }
        catch (SocketException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        while(receiving)
        {
            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                if(vp != null)
                {
                    mStrategy.getVoiceVector().add(vp);
                }
            }
            catch (SocketTimeoutException ex)
            {
                receiving = false;
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }

        int burstSize = 0;
        int burstCount = 0;
        int largestBurst = 0;

        int sequence = 1;

        for (int i = 0; i < mStrategy.getVoiceVector().size(); i++)
        {
            System.out.println(mStrategy.getVoiceVector().get(i).toString());
            if(sequence == mStrategy.getVoiceVector().get(i).getSequenceId())
            {
                sequence++;
            }
            else
            {
                burstCount++;
                while(mStrategy.getVoiceVector().get(i).getSequenceId() != sequence)
                {
                    burstSize++;
                    sequence++;
                }

                if(burstSize > largestBurst)
                {
                    largestBurst = burstSize;
                }
                burstSize = 0;
                sequence++;
            }
        }

        System.out.println("Total number of bursts: "+burstCount);
        System.out.println("Largest burst size: "+largestBurst);
        System.out.println("Packets Received: "+mStrategy.getVoiceVector().size()+"/"+sampleSize);
        System.out.println("Total packets lost: "+(sampleSize-mStrategy.getVoiceVector().size()));
        System.out.println("Finished test receiver.");
    }

    public void receiveVoiceTransmission()
    {
        System.out.println("Running voice receiver...");

        try
        {
            mPlayer = new AudioPlayer();
        }
        catch (LineUnavailableException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        boolean running = true;
        int currentPlace = 0;
        boolean isPlayable = true;

        while (running)
        {
            try
            {
                Collections.sort(mStrategy.getVoiceVector(),VoicePacket.COMPARE_BY_SEQUENCE);

                if(isPlayable)
                {
                    mPlayer.playBlock(mStrategy.getVoiceVector().get(currentPlace).getPayload());
                    currentPlace++;
                }

                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                isPlayable = true;

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                if(vp != null)
                {
                    mStrategy.getVoiceVector().add(vp);
                }
                else
                {
                    mStrategy.handlePacketLoss();
                }

                if(currentPlace == 938)
                {
                    running = false;
                }
            }
            catch (SocketTimeoutException e)
            {
                isPlayable = mStrategy.handlePacketLoss();
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        mPlayer.close();
        System.out.println("Finished voice receiver.");
    }
}


