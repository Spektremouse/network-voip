package models;

import java.security.InvalidParameterException;
import java.util.Vector;

/**
 * Created by thomaspachico on 07/02/2017.
 */
public class Interleaver
{

    private int mSize;

    private VoicePacket[][] mBlock;

    public void setBlock(Vector<VoicePacket> packetList)
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

        //rows
        for(int row = 0; row < mSize; row++)
        {
            //columns
            for(int col = 0; col < mSize; col++)
            {
                mBlock[row][col] = packetList.elementAt(count);
                count++;
            }
        }
    }

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
            //columns
            for(int col = 0; col < mSize; col++)
            {
                packetList.add(rotated[row][col]);
            }
        }
        return packetList;
    }

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
            //columns
            for(int col = 0; col < mSize; col++)
            {
                packetList.add(rotated[row][col]);
            }
        }
        return packetList;
    }
}
