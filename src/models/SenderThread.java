package models;

import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class SenderThread implements Runnable
{

    private static final int RECORDING_TIME = 35;
    private static final int PORT = 55321;
    private TransmissionType mCurrentTransmissionType;
    private PacketIO mPacketiser;
    private InetAddress mClientIP;
    private boolean mIsInterleave = false;

    private static DatagramSocket mSendingSocket;
    private AudioRecorder mRecorder;
    private String mHostname;

    public SenderThread(String hostname, DatagramType socketType, TransmissionType type, boolean isInterleave)
            throws SocketException
    {
        switch (socketType)
        {
            case DEFAULT:
                mSendingSocket = new DatagramSocket();
                break;
            case SOCKET2:
                mSendingSocket = new DatagramSocket2();
                break;
            case SOCKET3:
                mSendingSocket = new DatagramSocket3();
                break;
            case SOCKET4:
                mSendingSocket = new DatagramSocket4();
                break;
            default:
                throw new InvalidParameterException("Invalid socket type.");
        }

        mPacketiser = new PacketIO();
        mHostname = hostname;
        mCurrentTransmissionType = type;
        mIsInterleave = isInterleave;
    }

    public void start()
    {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run()
    {
        System.out.println("Starting sender...");
        try
        {
            System.out.println("Resolving connection to: "+mHostname);
            mClientIP = InetAddress.getByName(mHostname);
            System.out.println("Connection successful to: "+mClientIP.toString());
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Connection unsuccessful. Could not resolve host: "+mHostname);
            ex.printStackTrace();
            System.exit(1);
            //TODO dangerous
        }

        switch (mCurrentTransmissionType)
        {
            case TEST:
                sendTestTransmission();
                break;
            case VOICE:
                sendVoiceTransmission();
                break;
            default:
                throw new InvalidParameterException("Transmission type was not found.");
        }

        if (!mSendingSocket.isClosed())
        {
            mSendingSocket.close();
        }

        System.out.println("Finished sender.");
    }

    private void sendTestTransmission()
    {
        System.out.println("Running test transmission...");

        int sampleSize = 1000;

        try
        {
            for (int i = 0; i < sampleSize; i++)
            {
                VoicePacket vp = mPacketiser.generatePacket(new byte[512],TransmissionType.TEST);

                DatagramPacket datagram = new DatagramPacket(vp.toByteArray(), mPacketiser.PACKET_SIZE,
                        mClientIP, PORT);

                Thread.sleep(32);

                mSendingSocket.send(datagram);
            }
            System.out.println("Finished test transmission.");
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        catch (InterruptedException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void sendVoiceTransmission()
    {
        System.out.println("Running voice transmission...");

        List<VoicePacket> buffer = new ArrayList<>();

        try
        {
            mRecorder = new AudioRecorder();
        }
        catch (LineUnavailableException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        try
        {
            for (int i = 0; i < Math.ceil(RECORDING_TIME / 0.032); i++)
            {
                VoicePacket vp = mPacketiser.generatePacket(mRecorder.getBlock(),TransmissionType.VOICE);

                buffer.add(vp);

                if(buffer.size() >= 4)
                {
                    if(mIsInterleave)
                    {
                        BlockInterleaver interleaver = new BlockInterleaver();
                        interleaver.setBlock(buffer);
                        buffer = interleaver.rotateLeft();
                    }

                    for (VoicePacket voice : buffer)
                    {
                        DatagramPacket datagram = new DatagramPacket(voice.toByteArray(), mPacketiser.PACKET_SIZE,
                                mClientIP, PORT);
                        mSendingSocket.send(datagram);
                    }
                    buffer.clear();
                }
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        mRecorder.close();
        System.out.println("Finished voice transmission.");
    }
}
