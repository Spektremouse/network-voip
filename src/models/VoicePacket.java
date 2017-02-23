package models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class VoicePacket implements Comparable<VoicePacket>
{
    private TransmissionType mCurrentType;
    private int mChecksum = 0;
    private byte [] mPayload;
    private int mSequenceId;

    /**
     * Constructor for a VoicePacket object.
     * @param sequenceId The order number for the packet.
     * @param payload The data to be transmitted, as opposed to automatically generated metadata.
     * @param type Identifies the type of packet to be generated.
     */
    public VoicePacket(int sequenceId, byte [] payload, TransmissionType type)
    {
        mCurrentType = type;
        mSequenceId = sequenceId;
        mPayload = payload;
    }

    /**
     * @return Returns the packets current TransmissionType.
     */
    public TransmissionType getCurrentType()
    {
        return mCurrentType;
    }

    /**
     * @return Returns the packets current payload.
     */
    public byte[] getPayload()
    {
        return mPayload;
    }

    /**
     * @param payload Sets the packets current payload.
     */
    public void setPayload(byte[] payload)
    {
        this.mPayload = payload;
    }

    /**
     * @return Returns the packets sequence identifier.
     */
    public int getSequenceId()
    {
        return mSequenceId;
    }

    /**
     * @param sequenceId Sets the packets sequence identifier.
     */
    public void setSequenceId(int sequenceId)
    {
        this.mSequenceId = sequenceId;
    }

    /**
     * @return Returns the packets checksum value.
     */
    public int getChecksum()
    {
        return mChecksum;
    }

    /**
     * @param checksum Sets the packets checksum value.
     */
    public void setChecksum(int checksum)
    {
        this.mChecksum = checksum;
    }

    /**
     * Returns the VoicePacket object as an array of bytes.
     * @return The byte array which represents the VoicePacket object.
     * @throws IOException Arises when an error occurs in the ByteArrayOutputStream.
     */
    public byte [] toByteArray() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(intToBytes(mSequenceId));
        outputStream.write(mCurrentType.getCode());
        outputStream.write(mChecksum);
        outputStream.write(mPayload);

        byte [] data = outputStream.toByteArray();

        return  data;
    }

    //Converts an integer to an array of bytes.
    private byte [] intToBytes(int i)
    {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    /**
     * @return Returns the VoicePacket object represented as a string.
     */
    @Override
    public String toString()
    {
        return ""+ mSequenceId +"/"+mCurrentType+"/"+mPayload.length+"/"+mChecksum;
    }

    /**
     * Compares the VoicePacket object with the specified VoicePacket for order. Returns a zero, or a
     * positive integer as this object is equal to, or greater than the specified VoicePacket object.
     * @param vp The VoicePacket object to be compared.
     * @return A zero, or a positive integer as this object equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(VoicePacket vp)
    {
        if (this.getSequenceId() > vp.getSequenceId())
            return 0;
        else
            return 1;
    }

    /**
     * A comparison function, which imposes a total ordering on a collection of VoicePacket objects.
     */
    public static Comparator<VoicePacket> COMPARE_BY_SEQUENCE = Comparator.comparingInt(VoicePacket::getSequenceId);
}
