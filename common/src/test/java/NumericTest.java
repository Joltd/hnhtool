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

}
