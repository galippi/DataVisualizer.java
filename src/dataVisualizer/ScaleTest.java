package dataVisualizer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ScaleTest {

    @Test
    void test_00()
    {
        assertEquals(new ScaleData(0, 1, 2, 3).equals(new ScaleData(0, 1, 2, 3)), true);
        assertEquals(new ScaleData(0, 1, 2, 3).equals(new ScaleData(4, 1, 2, 3)), false);
        assertEquals(new ScaleData(0, 1, 2, 3).equals(new ScaleData(0, 2, 2, 3)), false);
        assertEquals(new ScaleData(0, 1, 2, 3).equals(new ScaleData(0, 1, 3, 3)), false);
        assertEquals(new ScaleData(0, 1, 2, 3).equals(new ScaleData(0, 1, 2, 4)), false);
    }

    @Test
    void test_01()
    {
        assertEquals(new ScaleData(0, 0, 2, 10, false).equals(new ScaleData(0, 1, 0, 2)), true);
        assertEquals(new ScaleData(5, 5, 2, 10, false).equals(new ScaleData(5, 1, 0, 2)), true);
        assertEquals(new ScaleData(-55, -55, 2, 10, false).equals(new ScaleData(-55, 1, 0, 2)), true);
        assertEquals(new ScaleData(555, 555, 2, 10, false).equals(new ScaleData(5.5, 0.1, 2, 2)), true);
    }

    @Test
    void test_02()
    {
        assertEquals(new ScaleData(0, 10, 2, 10, false).equals(new ScaleData(0, 1, 0, 10)), true);
        assertEquals(new ScaleData(0, 9, 2, 10, false).equals(new ScaleData(0, 1, 0, 9)), true);
        assertEquals(new ScaleData(0, 5.1, 2, 10, false).equals(new ScaleData(0, 1, 0, 6)), true);
        assertEquals(new ScaleData(0, 5, 2, 10, false).equals(new ScaleData(0, 0.5, 0, 10)), true);
        assertEquals(new ScaleData(0, 2.1, 2, 10, false).equals(new ScaleData(0, 0.5, 0, 5)), true);
        assertEquals(new ScaleData(0, 2.0, 2, 10, false).equals(new ScaleData(0, 0.2, 0, 10)), true);
        assertEquals(new ScaleData(0, 1.1, 2, 10, false).equals(new ScaleData(0, 0.2, 0, 6)), true);
        assertEquals(new ScaleData(0, 1.0, 2, 10, false).equals(new ScaleData(0, 0.1, 0, 10)), true);
    }

    @Test
    void test_02_1()
    {
        assertEquals(new ScaleData(0, 100, 2, 10, false).equals(new ScaleData(0, 10, 0, 10)), true);
        assertEquals(new ScaleData(0, 1000, 2, 10, false).equals(new ScaleData(0, 100, 0, 10)), true);
        assertEquals(new ScaleData(0, 9999, 2, 10, false).equals(new ScaleData(0, 1000, 0, 10)), true);
    }

    @Test
    void test_03()
    {
        assertEquals(new ScaleData(5.5, 5.9, 2, 10, false).equals(new ScaleData(5.5, 0.05, 0, 8)), true);
        assertEquals(new ScaleData(5.5, 90, 2, 10, false).equals(new ScaleData(5.5, 10, 0, 9)), true);
        assertEquals(new ScaleData(5.5, 5.5002, 2, 10, false).equals(new ScaleData(5.5, 0.1, 0, 2)), true);
    }

    @Test
    void test_04()
    {
        assertEquals(new ScaleData(-5, 5, 2, 10, false).equals(new ScaleData(-5.0, 1, 0, 10)), true);
        assertEquals(new ScaleData(-55, 45, 2, 10, false).equals(new ScaleData(-55.0, 10, 0, 10)), true);
        assertEquals(new ScaleData(-0.5, 0.5, 2, 10, false).equals(new ScaleData(-0.5, 0.1, 0, 10)), true);
    }

}
