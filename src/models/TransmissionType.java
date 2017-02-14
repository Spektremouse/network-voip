package models;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TransmissionType
{
    VOICE(0),
    TEST(1);

    private static final Map<Integer,TransmissionType> lookup = new HashMap<>();

    static
    {
        for(TransmissionType w : EnumSet.allOf(TransmissionType.class))
            lookup.put(w.getCode(), w);
    }

    private int code;


    TransmissionType(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static TransmissionType get(int code)
    {
        return lookup.get(code);
    }
}
