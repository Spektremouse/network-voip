package models;

import interfaces.IStrategy;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public class RepititionStrategy extends GenericStrategy implements IStrategy {


    public RepititionStrategy()
    {
        super();
    }

    @Override
    public void addPacket(VoicePacket packet)
    {
        mVoiceVector.add(packet);
    }

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
