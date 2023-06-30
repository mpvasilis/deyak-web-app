package gr.uowm.deyakwebapp.helpers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Decoder {

    public static float decode(int register1, int register2) {
        int intValue = (register2 << 16) | register1;
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(intValue).array();
        float floatValue = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        return floatValue;

//        int intValue = (register2 << 16) | register1;
//        return ByteBuffer.allocate(4).putInt(intValue).flip().getFloat();
    }

    public static int decodeInt(int register1, int register2) {
        int reg1UInt16 = register1 & 0xFFFF;
        int reg2UInt16 = register2 & 0xFFFF;
        int result = (reg1UInt16 << 16) + reg2UInt16;
        return result;
    }

    public static float decodeFourRegs(int register1, int register2, int register3, int register4) {
        long longValue = ((long) register4 << 48) | ((long) register3 << 32) | ((long) register2 << 16) | register1;
        byte[] bytes = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(longValue).array();
        float floatValue = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        return floatValue;
    }

}