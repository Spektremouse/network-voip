package models;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Creates sequenced VoicePacket instances that are ready for transmission to a Datagram socket.
 * Reads a Datagram sockets payload and converts into a VoicePacket instance.
 */
public class PacketIO
{
    private int mSequenceId;
    private final static int HEADER_SIZE = 5;
    public static final int PACKET_SIZE = 517;

    /**
     * Creates a new instance of the PacketIO object with the sequence identifier starting at 0.
     */
    public PacketIO()
    {
        mSequenceId = 0;
    }

    /**
     *
     * @param payload
     * @param type
     * @return
     * @throws IllegalArgumentException
     */
    public VoicePacket generatePacket(byte [] payload, TransmissionType type) throws IllegalArgumentException
    {
        if (mSequenceId < 65535) {
            mSequenceId++;
        } else {
            mSequenceId = 0;
        }

        if(payload.length != 512)
        {
            throw new IllegalArgumentException("Invalid payload size");
        }

        return new VoicePacket(mSequenceId, payload, type);
    }

    /**
     *
     * @param data
     * @return
     */
    public VoicePacket unpackPacket(byte [] data)
    {
        byte [] header = new byte[HEADER_SIZE];
        //Sequence ID for a packet found in first 4 bytes of the header.
        header[0] = data[0];
        header[1] = data[1];
        header[2] = data[2];
        header[3] = data[3];
        //Single byte used to identify the transmission type.
        int type = data[4];

        byte [] payload = Arrays.copyOfRange(data, HEADER_SIZE, data.length);

        return new VoicePacket(bytesToInt(header), payload, TransmissionType.get(type));
    }

    //Converts a byte [] into a 32 bit integer.
    private int bytesToInt(byte [] a)
    {
        ByteBuffer wrapped = ByteBuffer.wrap(a);
        return wrapped.getInt();
    }
}
