package models;

import interfaces.IStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic strategy that does not handle gaps in voice transmission.
 */
public class GenericStrategy implements IStrategy
{
    protected List<VoicePacket> mVoiceVector;

    /**
     * Default constructor for a GenericStrategy
     */
    public GenericStrategy()
    {
        mVoiceVector = new ArrayList<>();
    }

    /**
     * Returns the current buffer.
     * @return The current buffer.
     */
    @Override
    public List<VoicePacket> getVoiceVector()
    {
        return mVoiceVector;
    }

    /**
     * Sets the buffer to be used by the strategy.
     * @param buffer The new buffer to be used.
     */
    @Override
    public void setVoiceVector(List<VoicePacket> buffer)
    {
        mVoiceVector = buffer;
    }

    /**
     Adds a packet to the GenericStrategy buffer.
     * @param packet The packet to be added to the buffer.
     */
    @Override
    public void addPacket(VoicePacket packet)
    {
        mVoiceVector.add(packet);
    }

    /**
     * Default behaviour for handling packet loss.
     * @return True if the buffer can continue being read.
     */
    @Override
    public boolean handlePacketLoss()
    {
        mVoiceVector.add(new VoicePacket(mVoiceVector.get(mVoiceVector.size()-1).getSequenceId()+1,
                new byte[512], TransmissionType.VOICE));
        return true;
    }
}
