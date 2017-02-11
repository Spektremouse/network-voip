package interfaces;

import models.VoicePacket;

import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public interface IStrategy
{
    public Vector<VoicePacket> getVoiceVector();
    public void addPacket(VoicePacket packet);
    public void handlePacketLoss();
}
