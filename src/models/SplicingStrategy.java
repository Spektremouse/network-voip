package models;
import interfaces.IStrategy;

public class SplicingStrategy extends GenericStrategy implements IStrategy
{

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
        return true;
    }

}
