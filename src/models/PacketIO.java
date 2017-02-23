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
    private final static int HEADER_SIZE = 6;
    public static final int PACKET_SIZE = 518;

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

        VoicePacket packet = new VoicePacket(mSequenceId, payload, type);

        generateChecksum(packet);

        return packet;
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
        int checksum = data[5];

        byte [] payload = Arrays.copyOfRange(data, HEADER_SIZE, data.length);

        VoicePacket packet = new VoicePacket(bytesToInt(header), payload, TransmissionType.get(type));

        packet.setChecksum(checksum);

        return packet;
    }

    //Converts a byte [] into a 32 bit integer.
    private int bytesToInt(byte [] a)
    {
        ByteBuffer wrapped = ByteBuffer.wrap(a);
        return wrapped.getInt();
    }

    private void generateChecksum(VoicePacket packet)
    {
        int divisor = 128;
        int packetTotal = 0;

        for (byte a : packet.getPayload())
        {
            packetTotal += a;
        }

        packetTotal += packet.getSequenceId();
        packetTotal += packet.getCurrentType().getCode();

        packet.setChecksum(packetTotal % divisor);

        System.out.println("Packer #"+packet.getSequenceId()+" checksum equals:"+packet.getChecksum());
    }
}
