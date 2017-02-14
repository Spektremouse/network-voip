package models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class VoicePacket implements Comparable<VoicePacket>
{
    private byte[] mPayload;
    private TransmissionType mCurrentType;
    private int mChecksum;

    public static final int TEST = 0;
    public static final int VOICE = 1;

    public VoicePacket(int checksum, byte [] payload, TransmissionType type)
    {
        mCurrentType = type;
        mChecksum = checksum;
        mPayload = payload;
    }

    public TransmissionType getCurrentType() { return mCurrentType; }

    public byte[] getPayload() { return mPayload; }

    public void setPayload(byte[] payload) { this.mPayload = payload; }

    public int getChecksum() { return mChecksum; }

    public void setChecksum(int checksum) { this.mChecksum = checksum; }

    public byte [] toByteArray() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(intToBytes(mChecksum));
        outputStream.write(mCurrentType.getCode());
        outputStream.write(mPayload);

        byte [] data = outputStream.toByteArray();

        return  data;
    }

    private byte [] intToBytes(int i)
    {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    @Override
    public String toString()
    {
        return ""+mChecksum+"/"+mCurrentType+"/"+mPayload.length;
    }

    @Override
    public int compareTo(VoicePacket vp)
    {
        if (this.getChecksum() > vp.getChecksum())
            return 0;
        else
            return 1;
    }

    public static Comparator<VoicePacket> COMPARE_BY_CHECKSUM = new Comparator<VoicePacket>()
    {
        @Override
        public int compare(VoicePacket one, VoicePacket other)
        {
            return one.getChecksum() - other.getChecksum();
        }
    };
}
