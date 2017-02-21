package models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class VoicePacket implements Comparable<VoicePacket>
{
    private TransmissionType mCurrentType;
    private byte [] mPayload;
    private int mSequenceId;

    /**
     *
     * @param sequenceId
     * @param payload
     * @param type
     */
    public VoicePacket(int sequenceId, byte [] payload, TransmissionType type)
    {
        mCurrentType = type;
        mSequenceId = sequenceId;
        mPayload = payload;
    }

    /**
     *
     * @return
     */
    public TransmissionType getCurrentType()
    {
        return mCurrentType;
    }

    /**
     *
     * @return
     */
    public byte[] getPayload()
    {
        return mPayload;
    }

    /**
     *
     * @param payload
     */
    public void setPayload(byte[] payload)
    {
        this.mPayload = payload;
    }

    /**
     *
     * @return
     */
    public int getSequenceId()
    {
        return mSequenceId;
    }

    /**
     *
     * @param checksum
     */
    public void setSequenceId(int checksum)
    {
        this.mSequenceId = checksum;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public byte [] toByteArray() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(intToBytes(mSequenceId));
        outputStream.write(mCurrentType.getCode());
        outputStream.write(mPayload);

        byte [] data = outputStream.toByteArray();

        return  data;
    }

    //
    private byte [] intToBytes(int i)
    {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        return ""+ mSequenceId +"/"+mCurrentType+"/"+mPayload.length;
    }

    /**
     *
     * @param vp
     * @return
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
     *
     */
    public static Comparator<VoicePacket> COMPARE_BY_SEQUENCE = Comparator.comparingInt(VoicePacket::getSequenceId);
}
