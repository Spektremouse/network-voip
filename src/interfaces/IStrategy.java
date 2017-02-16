package interfaces;

import models.VoicePacket;

import java.util.List;

/**
 *
 */
public interface IStrategy
{
    List<VoicePacket> getVoiceVector();
    void setVoiceVector(List<VoicePacket> buffer);
    void addPacket(VoicePacket packet);
    boolean handlePacketLoss();
}
