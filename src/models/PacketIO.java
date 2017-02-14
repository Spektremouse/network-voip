package models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by thomaspachico on 10/02/2017.
 */
public class PacketIO
{
    private int mChecksum;
    private final static int HEADER_SIZE = 5;
    public static final int PACKET_SIZE = 517;

    public PacketIO()
    {
        mChecksum = 0;
    }

    public VoicePacket generatePacket(byte [] payload, TransmissionType type) throws IOException
    {
        if (mChecksum < 65535) {
            mChecksum++;
        } else {
            mChecksum = 0;
        }

        if(payload.length != 512)
        {
            throw new IllegalArgumentException("Invalid payload size");
        }

        return new VoicePacket(mChecksum, payload, type);
    }

    public VoicePacket unpackPacket(byte [] data)
    {
        byte [] header = new byte[HEADER_SIZE];
        //checksum
        header[0] = data[0];
        header[1] = data[1];
        header[2] = data[2];
        header[3] = data[3];
        //packet type
        int type = data[4];

        byte [] payload = Arrays.copyOfRange(data, HEADER_SIZE, data.length);

        return new VoicePacket(bytesToInt(header), payload, TransmissionType.get(type));
    }

    //unpacking a packet
    private int bytesToInt(byte [] a)
    {
        ByteBuffer wrapped = ByteBuffer.wrap(a);
        return wrapped.getInt();
    }
}
