package gr.uowm.deyakwebapp.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
public class DecoderTest {
    @Test
    public void testDecode() {
        int reg1 = 1231;
        int reg2 = 59413;
        float expectedValue = 1.070050048828125f;

        float result = Decoder.decode(reg1, reg2);
        assertEquals(expectedValue, result, 0.0001);
    }

    @Test
    public void testDecodeInt() {
        int reg1 = 1231;
        int reg2 = 59413;
        int expectedValue = 80872781;

        int result = Decoder.decodeInt(reg1, reg2);
        assertEquals(expectedValue, result);
    }

    @Test
    public void testDecodeFourRegs() {
        int reg1 = 1231;
        int reg2 = 59413;
        int reg3 = 98765;
        int reg4 = 1234567;
        float expectedValue = 6.3615532E-5f;

        float result = Decoder.decodeFourRegs(reg1, reg2, reg3, reg4);
        assertEquals(expectedValue, result, 0.0000001);
    }
}