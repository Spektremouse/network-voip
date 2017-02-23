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
        //Buffer
        while(mStrategy.getVoiceVector().size() < 4)
        {
            try
            {
                byte[] data = new byte[mPacketiser.PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, 0, mPacketiser.PACKET_SIZE);

                mReceivingSocket.receive(packet);

                VoicePacket vp = mPacketiser.unpackPacket(packet.getData());

                mStrategy.getVoiceVector().add(vp);
                //System.out.println("Unknown packet added to queue!");
            }
            catch (SocketTimeoutException ex)
            {
                //ex.printStackTrace();
                //TODO Handle exception
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(1);
                //TODO Handle exception
            }
        }

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

        for (VoicePacket vp : mStrategy.getVoiceVector())
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

                if(vp != null)
                {
                    mStrategy.getVoiceVector().add(vp);
                    System.out.println("Test packet added to queue!");
                }
            }
            catch (SocketTimeoutException ex)
            {
                receiving = false;
                //TODO Handle exception
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                //TODO Handle exception
            }
        }

        System.out.println("Packets Received: "+mStrategy.getVoiceVector().size()+"/2000");
        System.out.println("Total packets lost: "+(2000-mStrategy.getVoiceVector().size()));
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
                //System.out.println("Timeout.");
                //TODO Handle exception
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                //TODO Handle exception
            }
        }
        mPlayer.close();
    }
}


