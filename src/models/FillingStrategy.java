package models;

/**
 * Fills gaps in voice transmission with silence.
 */
public class FillingStrategy extends GenericStrategy
{

    /**
     * Default constructor which calls GenericStrategy parent constructor.
     */
    public FillingStrategy()
    {
        super();
    }

    /**
     * Adds a packet to the FillingStrategy buffer.
     * @param packet The packet to be added to the buffer.
     */
    @Override
    public void addPacket(VoicePacket packet)
    {
        mVoiceVector.add(packet);
    }


    /**
     * Adds an empty packet to the buffer.
     * @return True if the buffer can continue being read.
     */
    @Override
    public boolean handlePacketLoss()
    {
        mVoiceVector.add(new VoicePacket(mVoiceVector.get(mVoiceVector.size()-1).getSequenceId()+1,
                new byte[512], TransmissionType.VOICE));
        return true;
    }
}
