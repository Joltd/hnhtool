import com.evgenltd.hnhtools.util.ByteUtil;
import org.junit.jupiter.api.Test;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-02-2019 00:11</p>
 */
public class NumericTest {

    @Test
    public void testCoordMul() {

        final double x = 0x1.0p-10;
        System.out.println(x);
        System.out.println(x * 11);

    }

    @Test
    public void test() {
        System.out.println(String.format("%02X", 255));

//        check(0x0);
//        check(0x5000);
//        check(0x7FFF);
//        check(0x8000);
//        check(0x8001);
//        check(0x9000);
//        check(0xFFFF);
    }

    private void check(final int value) {
        System.out.println(String.format(
                "%x; value & 0x8000 [%x]; ~0x8000 [%x]; value & ~0x8000 [%x]",
                value,
                value & 0x8000,
                ~0x8000,
                value & ~0x800
        ));
    }

    @Test
    public void testHandleRel() {
        checkHandleRel(0, 32800);
    }

    private void checkHandleRel(final int messageSequence, final int expectedSequence) {
        if (messageSequence == expectedSequence) {
            System.out.println("Regular handling, expectedSequence will be incremented");
        } else if (floormod(messageSequence - expectedSequence, 65536) < 32768) {
            System.out.println("Put in awaiting");
        } else {
            System.out.println("Ignore");
        }
    }

    private int floormod(int a, int b) {
        int r = a % b;
        System.out.println("r = " + r);
        if (r < 0) {
            r += b;
            System.out.println("r = " + r);
        }
        return (r);
    }

    @Test
    public void divTest() {
        System.out.println(ByteUtil.toShort(-70000));
        System.out.println(ByteUtil.toShort(-5));
    }

}
