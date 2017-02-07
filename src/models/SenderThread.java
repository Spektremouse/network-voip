package models;

import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;

/**
 * Created by thomaspachico on 07/02/2017.
 */

public class SenderThread implements Runnable {

    private static final int RECORDING_TIME = 35;
    private static final int PACKET_SIZE = 512;
    private static final int PORT = 55321;

    private static DatagramSocket2 mSendingSocket;
    private AudioRecorder mRecorder;
    private String mHostname;

    public SenderThread(String hostname) {
        mHostname = hostname;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        System.out.println("Sending...");

        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try
        {
            clientIP = InetAddress.getByName(mHostname);
            System.out.println(clientIP.toString());
        }
        catch (UnknownHostException e)
        {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        //DatagramSocket sending_socket;
        try
        {
            mSendingSocket = new DatagramSocket2();
        }
        catch (SocketException e)
        {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

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
                byte[] data = mRecorder.getBlock();
                DatagramPacket packet = new DatagramPacket(data, PACKET_SIZE, clientIP, PORT);
                mSendingSocket.send(packet);
            }

        }
        catch (IOException ex)
        {
            //TODO handle IO exception
        }

        //Close audio input
        mRecorder.close();

        if (!mSendingSocket.isClosed())
        {
            //Close the socket
            mSendingSocket.close();
        }
        System.out.println("Finished.");
    }
}
