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
    private final static int HEADER_SIZE = 3;
    public static final int PACKET_SIZE = 515;

    public PacketIO()
    {
        mChecksum = 0;
    }

    public byte [] generatePacket(byte [] payload, TransmissionType type) throws IOException {
        if (mChecksum < 65535) {
            mChecksum++;
        } else {
            mChecksum = 0;
        }

        if(payload.length != 512)
        {
            throw new IllegalArgumentException("Invalid payload size");
        }

        byte [] header = intToBytes(mChecksum);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(header);
        outputStream.write(type.getCode());
        outputStream.write(payload);

        byte [] data = outputStream.toByteArray();

        return  data;
    }

    public VoicePacket unpackPacket(byte [] data)
    {
        byte [] header = new byte[HEADER_SIZE];
        //checksum
        header[0] = data[0];
        header[1] = data[1];
        //packet type
        int type = data[2];

        byte [] payload = Arrays.copyOfRange(data, HEADER_SIZE, data.length);

        return new VoicePacket(bytesToInt(header), payload, TransmissionType.get(type));
    }

    //packaging a packet
    private byte [] intToBytes(int i)
    {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putInt(i);
        return  bb.array();
    }

    //unpacking a packet
    private int bytesToInt(byte [] a)
    {
        ByteBuffer wrapped = ByteBuffer.wrap(a);
        return wrapped.getInt();
    }
}
