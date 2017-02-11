package models;

import interfaces.IStrategy;
import java.util.Vector;

/**
 * Created by thomaspachico on 11/02/2017.
 */
public class GenericStrategy implements IStrategy
{
    protected Vector<VoicePacket> mVoiceVector;

    public GenericStrategy()
    {
        mVoiceVector = new Vector<>();
    }

    @Override
    public Vector<VoicePacket> getVoiceVector() {
        return mVoiceVector;
    }

    @Override
    public void setVoiceVector(Vector<VoicePacket> buffer) {

    }

    @Override
    public void addPacket(VoicePacket packet)
    {

    }

    @Override
    public void handlePacketLoss() {

    }
}
