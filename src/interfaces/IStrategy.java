package interfaces;

import models.VoicePacket;
import java.util.Vector;

/**
 *
 */
public interface IStrategy
{
    Vector<VoicePacket> getVoiceVector();
    void setVoiceVector(Vector<VoicePacket> buffer);
    void addPacket(VoicePacket packet);
    boolean handlePacketLoss();
}
