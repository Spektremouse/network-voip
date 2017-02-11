package models;

import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.security.InvalidParameterException;

public class SenderThread implements Runnable {

    private static final int RECORDING_TIME = 35;
    private static final int PORT = 55321;
    private TransmissionType mCurrentTansmissionType;
    private PacketIO mPacketiser;
    private InetAddress mClientIP;

    private static DatagramSocket mSendingSocket;
    private AudioRecorder mRecorder;
    private String mHostname;

    public SenderThread(String hostname, DatagramType socketType, TransmissionType type) throws SocketException
    {
        mCurrentTansmissionType = type;
        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.
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
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        System.out.println("Sending...");

        //IP ADDRESS to send to
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
        //***************************************************
        //Main loop.
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
            //Close the socket
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
                byte [] data = mPacketiser.generatePacket(new byte[512],TransmissionType.TEST);

                DatagramPacket datagram = new DatagramPacket(data, mPacketiser.getPacketSize(), mClientIP, PORT);

                mSendingSocket.send(datagram);
            }
        }
        catch (IOException ex)
        {
            //TODO handle IO exception
        }
    }

    private void sendVoiceTransmission()
    {
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
                byte [] data = mPacketiser.generatePacket(mRecorder.getBlock(),TransmissionType.VOICE);

                DatagramPacket datagram = new DatagramPacket(data, mPacketiser.getPacketSize(), mClientIP, PORT);

                mSendingSocket.send(datagram);
            }
        }
        catch (IOException ex)
        {
            //TODO handle IO exception
        }

        //Close audio input
        mRecorder.close();
    }
}
