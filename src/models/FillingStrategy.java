package models;

import interfaces.IStrategy;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public class FillingStrategy extends GenericStrategy implements IStrategy {

    private Vector<VoicePacket> mVoiceVector = new Vector<VoicePacket>();

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
        //mVoiceVector.add(new VoicePacket());
    }
}
