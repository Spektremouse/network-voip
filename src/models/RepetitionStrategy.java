package models;

import interfaces.IStrategy;

/**
 *
 */
public class RepetitionStrategy extends GenericStrategy implements IStrategy
{

    /**
     * Default constructor which calls GenericStrategy parent constructor.
     */
    public RepetitionStrategy()
    {
        super();
    }

    /**
     * Adds a packet to the RepetitionStrategy buffer.
     * @param packet The packet to be added to the buffer.
     */
    @Override
    public void addPacket(VoicePacket packet)
    {
        mVoiceVector.add(packet);
    }

    /**
     * Adds a copy of the last added packet to the buffer.
     * @return True if the buffer can continue being read.
     */
    @Override
    public boolean handlePacketLoss()
    {
        if(mVoiceVector.size() != 0 )
        {
            mVoiceVector.add(mVoiceVector.lastElement());
        }
        return true;
    }
}
