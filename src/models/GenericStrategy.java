package models;

import interfaces.IStrategy;
import java.util.Vector;

/**
 * Created by thomaspachico on 11/02/2017.
 */
public class GenericStrategy implements IStrategy
{
    protected Vector<VoicePacket> mVoiceVector = new Vector<VoicePacket>();

    @Override
    public Vector<VoicePacket> getVoiceVector() {
        return null;
    }

    @Override
    public void addPacket(VoicePacket packet)
    {

    }

    @Override
    public void handlePacketLoss() {

    }
}
