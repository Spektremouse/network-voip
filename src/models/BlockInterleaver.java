package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Re-orders the sequence of packets before transmission.
 * The idea is that if a burst of loss occurs in the network, consecutive data will not be lost.
 */
public class BlockInterleaver
{
    private VoicePacket[][] mBlock;
    private boolean mIsBlockSet = false;
    private int mSize;

    /**
     * Determines if the BlockInterleaver instance has a block.
     * @return True if the block has been set.
     */
    public boolean isBlockSet()
    {
        return  mIsBlockSet;
    }

    /**
     * Sets the block as a 2D array of VoicePackets given a List.
     * @param packetList The list to convert into an NxN matrix.
     * @throws IllegalArgumentException Occurs when the list size is not a perfect square root.
     */
    public void setBlock(List<VoicePacket> packetList) throws IllegalArgumentException
    {
        double root = Math.sqrt(packetList.size());

        double rem = packetList.size()%root;

        if(rem != 0)
        {
            throw new IllegalArgumentException("Packet list size must be a perfect square.");
        }

        mSize = (int)root;

        mBlock = new VoicePacket[mSize][mSize];

        int count = 0;

        for(int row = 0; row < mSize; row++)
        {
            for(int col = 0; col < mSize; col++)
            {
                mBlock[row][col] = packetList.get(count);
                count++;
            }
        }
        mIsBlockSet = true;
    }

    /**
     * Rotates the currently set block 90 degrees to the right.
     * @return Returns the rotated block as a linear list.
     * @throws IllegalArgumentException Occurs when the block has not been set.
     */
    public List<VoicePacket> rotateRight() throws IllegalArgumentException
    {
        if(mIsBlockSet == true)
        {
            VoicePacket[][] rotated = new VoicePacket[mSize][mSize];

            for(int row = 0; row < mSize; row++)
            {
                for(int col = 0; col < mSize; col++)
                {
                    rotated[col][mSize-1-row] = mBlock[row][col];
                }
            }

            List<VoicePacket> packetList = new ArrayList<>();

            for(int row = 0; row < mSize; row++)
            {
                for(int col = 0; col < mSize; col++)
                {
                    packetList.add(rotated[row][col]);
                }
            }
            return packetList;
        }
        else
        {
            throw new IllegalArgumentException("Block has not been set.");
        }
    }

    /**
     * Rotates the currently set block 90 degrees to the left.
     * @return Returns the rotated block as a linear list.
     * @throws IllegalArgumentException Occurs when the block has not been set.
     */
    public List<VoicePacket> rotateLeft() throws IllegalArgumentException
    {
        if(mIsBlockSet == true)
        {
            VoicePacket[][] rotated = new VoicePacket[mSize][mSize];

            for(int row = 0; row < mSize; row++)
            {
                for(int col = 0; col < mSize; col++)
                {
                    rotated[mSize-1-col][row] = mBlock[row][col];
                }
            }

            List<VoicePacket> packetList = new ArrayList<>();

            for(int row = 0; row < mSize; row++)
            {
                for(int col = 0; col < mSize; col++)
                {
                    packetList.add(rotated[row][col]);
                }
            }
            return packetList;
        }
        else
        {
            throw new IllegalArgumentException("Block has not been set.");
        }
    }
}
