package models;
;
import interfaces.IStrategy;

import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public class RepititionStrategy extends GenericStrategy implements IStrategy {


    @Override
    public Vector<VoicePacket> getVoiceVector()
    {
        return mVoiceVector;
    }

    @Override
    public void addPacket(VoicePacket packet)
    {
        mVoiceVector.add(packet);
    }

    @Override
    public void handlePacketLoss()
    {
        if(mVoiceVector.size() != 0 )
        {
            mVoiceVector.add(mVoiceVector.lastElement());

        }
    }
}
