package models;

import interfaces.IStrategy;
import CMPC3M06.AudioPlayer;
import interfaces.IThreadCallback;
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
    public StringBuilder mBuilder;

    private static final int TIMEOUT = 32;
    private static final int PORT = 55321;
    private PacketIO mPacketiser;

    private static DatagramSocket mReceivingSocket;
    private AudioPlayer mPlayer;
    private IStrategy mStrategy;

    private IThreadCallback mCallback;

    public ReceiverThread(IStrategy strategy, DatagramType socketType, IThreadCallback callback) throws SocketException
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

        mCallback = callback;
        mReceivingSocket.setSoTimeout(TIMEOUT);
        mPacketiser = new PacketIO();
        mStrategy = strategy;
        mBuilder = new StringBuilder();
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
        mCallback.onComplete();
    }

    private void receiveTestTransmission()
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

                mStrategy.getVoiceVector().add(vp);
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

        analysePacketCorruption();
        analysePacketOrder();
        Collections.sort(mStrategy.getVoiceVector(),VoicePacket.COMPARE_BY_SEQUENCE);
        analysePacketLoss(sampleSize);

        System.out.println("Finished test receiver.");
    }

    private void analysePacketLoss(int sampleSize)
    {
        int burstSize = 0;
        int burstCount = 0;
        int largestBurst = 0;

        int sequence = 1;

        for (int i = 0; i < mStrategy.getVoiceVector().size(); i++)
        {
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

        mBuilder.append("Total number of bursts: " + burstCount).append("\n")
                .append("Largest burst size: " + largestBurst).append("\n")
                .append("Packets Received: " + mStrategy.getVoiceVector().size() + "/" + sampleSize).append("\n")
                .append("Total packets lost: " + (sampleSize - mStrategy.getVoiceVector().size())).append("\n");

        System.out.println("Total number of bursts: " + burstCount);
        System.out.println("Largest burst size: " + largestBurst);
        System.out.println("Packets Received: " + mStrategy.getVoiceVector().size() + "/" + sampleSize);
        System.out.println("Total packets lost: " + (sampleSize - mStrategy.getVoiceVector().size()));
    }

    private void analysePacketOrder()
    {
        int orderCount = 0;
        for (int i = 0; i < mStrategy.getVoiceVector().size() - 1; i++)
        {
            if (mStrategy.getVoiceVector().get(i).getSequenceId() >
                    mStrategy.getVoiceVector().get(i + 1).getSequenceId())
            {
                orderCount++;
            }
        }

        mBuilder.append("Total packets out of order: " + orderCount).append("\n");

        System.out.println("Total packets out of order: " + orderCount);
    }

    private void analysePacketCorruption()
    {
        int corruptCount = 0;

        for (int i = 0; i < mStrategy.getVoiceVector().size(); i++)
        {
            if(mStrategy.getVoiceVector().get(i) == null)
            {
                corruptCount++;
            }
        }

        mStrategy.getVoiceVector().removeAll(Collections.singleton(null));

        mBuilder.append("Total packets corrupted: " + corruptCount).append("\n");

        System.out.println("Total packets corrupted: " + corruptCount);
    }

    private void receiveVoiceTransmission()
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


