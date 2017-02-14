package models;

import interfaces.IStrategy;
import java.util.Vector;

/**
 * A generic strategy that does not handle gaps in voice transmission.
 */
public class GenericStrategy implements IStrategy
{
    protected Vector<VoicePacket> mVoiceVector;

    /**
     * Default constructor for a GenericStrategy
     */
    public GenericStrategy()
    {
        mVoiceVector = new Vector<>();
    }

    /**
     * Returns the current buffer.
     * @return The current buffer.
     */
    @Override
    public Vector<VoicePacket> getVoiceVector()
    {
        return mVoiceVector;
    }

    /**
     * Sets the buffer to be used by the strategy.
     * @param buffer The new buffer to be used.
     */
    @Override
    public void setVoiceVector(Vector<VoicePacket> buffer)
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
    public boolean handlePacketLoss() {
        return true;
    }
}
