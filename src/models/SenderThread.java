package models;

import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.security.InvalidParameterException;
import java.util.Vector;

public class SenderThread implements Runnable {

    private static final int RECORDING_TIME = 35;
    private static final int PORT = 55321;
    private TransmissionType mCurrentTansmissionType;
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
        mCurrentTansmissionType = type;
        mIsInterleave = isInterleave;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        System.out.println("Sending...");

        try
        {
            mClientIP = InetAddress.getByName(mHostname);
            System.out.println(mClientIP.toString());
        }
        catch (UnknownHostException e)
        {
            System.out.println("ERROR: SenderThread: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        switch (mCurrentTansmissionType)
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
        System.out.println("Finished.");
    }

    private void sendTestTransmission()
    {
        try
        {
            for (int i = 0; i < 2000; i++)
            {
                VoicePacket vp = mPacketiser.generatePacket(new byte[512],TransmissionType.TEST);

                DatagramPacket datagram = new DatagramPacket(vp.toByteArray(), mPacketiser.PACKET_SIZE,
                        mClientIP, PORT);

                Thread.sleep(32);

                mSendingSocket.send(datagram);
            }
        }
        catch (IOException ex)
        {
            //TODO handle IO exception
        }

        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }

    }

    private void sendVoiceTransmission()
    {
        Vector<VoicePacket> buffer = new Vector<>();

        System.out.println("Recording...");
        try
        {
            mRecorder = new AudioRecorder();
        } catch (LineUnavailableException ex)
        {
            //TODO handle LineUnavailable exception
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
                        Interleaver interleaver = new Interleaver();
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
            ex.printStackTrace();
        }

        mRecorder.close();
    }
}
