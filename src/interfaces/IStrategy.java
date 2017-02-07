package interfaces;

import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public interface IStrategy
{

    public Vector<byte[]> getVoiceVector();
    public void addPacket(byte[] data);
    public void handlePacketLoss();
}
