package models;
import interfaces.IStrategy;

import java.util.Vector;


/**
 * Created by thomaspachico on 07/02/2017.
 */
public class FillingStrategy implements IStrategy {

    private Vector<byte[]> mVoiceVector = new Vector<byte[]>();

    @Override
    public Vector<byte[]> getVoiceVector()
    {
        return mVoiceVector;
    }

    @Override
    public void addPacket(byte[] data)
    {
        mVoiceVector.add(data);
    }

    @Override
    public void handlePacketLoss()
    {
        mVoiceVector.add(new byte[512]);
    }
}
