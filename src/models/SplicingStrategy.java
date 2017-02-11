package models;
import interfaces.IStrategy;

import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public class SplicingStrategy extends GenericStrategy implements IStrategy {


    public SplicingStrategy()
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
        return false;
    }

}
