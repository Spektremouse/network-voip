package models;

import java.security.InvalidParameterException;

public class VoicePacket
{
    private byte[] mPayload;
    private TransmissionType mCurrentType;
    private int mChecksum;

    public static final int TEST = 0;
    public static final int VOICE = 1;

    public VoicePacket(int checksum, byte [] payload, TransmissionType type)
    {
        if(type != TransmissionType.TEST || type != TransmissionType.VOICE)
        {
            throw new InvalidParameterException("Invalid packet type parameter.");
        }
        mCurrentType = type;
        mChecksum = checksum;
        mPayload = payload;
    }

    public TransmissionType getCurrentType() { return mCurrentType; }

    public byte[] getPayload() { return mPayload; }

    public void setPayload(byte[] payload) { this.mPayload = payload; }

    public int getChecksum() { return mChecksum; }

    public void setChecksum(int checksum) { this.mChecksum = checksum; }
}
