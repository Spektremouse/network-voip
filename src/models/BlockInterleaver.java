package models;

import java.security.InvalidParameterException;
import java.util.Vector;

/**
 * Re-orders the sequence of packets before transmission.
 * The idea is that if a burst of loss occurs in the network, consecutive data will not be lost.
 */
public class BlockInterleaver
{
    private VoicePacket[][] mBlock;
    private int mSize;

    /**
     *
     * @param packetList
     * @throws InvalidParameterException
     */
    public void setBlock(Vector<VoicePacket> packetList) throws InvalidParameterException
    {
        double root = Math.sqrt(packetList.size());

        double rem = packetList.size()%root;

        if(rem != 0)
        {
            throw new InvalidParameterException("Packet list size must be a perfect square.");
        }

        mSize = (int)root;

        mBlock = new VoicePacket[mSize][mSize];

        int count = 0;

        for(int row = 0; row < mSize; row++)
        {
            for(int col = 0; col < mSize; col++)
            {
                mBlock[row][col] = packetList.elementAt(count);
                count++;
            }
        }
    }

    /**
     *
     * @return
     */
    public Vector<VoicePacket> rotateRight()
    {
        VoicePacket[][] rotated = new VoicePacket[mSize][mSize];

        for(int row = 0; row < mSize; row++)
        {
            for(int col = 0; col < mSize; col++)
            {
                rotated[col][mSize-1-row] = mBlock[row][col];
            }
        }

        Vector<VoicePacket> packetList = new Vector<>();

        for(int row = 0; row < mSize; row++)
        {
            for(int col = 0; col < mSize; col++)
            {
                packetList.add(rotated[row][col]);
            }
        }
        return packetList;
    }

    /**
     *
     * @return
     */
    public Vector<VoicePacket> rotateLeft()
    {
        VoicePacket[][] rotated = new VoicePacket[mSize][mSize];

        for(int row = 0; row < mSize; row++)
        {
            for(int col = 0; col < mSize; col++)
            {
                rotated[mSize-1-col][row] = mBlock[row][col];
            }
        }

        Vector<VoicePacket> packetList = new Vector<>();

        for(int row = 0; row < mSize; row++)
        {
            for(int col = 0; col < mSize; col++)
            {
                packetList.add(rotated[row][col]);
            }
        }
        return packetList;
    }
}
