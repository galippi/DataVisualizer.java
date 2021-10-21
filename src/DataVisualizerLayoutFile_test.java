
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dataVisualizer.DataVisualizerLayoutFileLoader;

class DataVisualizerLayoutFileTest
{
    @Test
    void test_00()
    {
        DataVisualizerLayoutFileLoader dvlf = new DataVisualizerLayoutFileLoader("TestData/test06.dvl");
        assertEquals(dvlf.getStatus(), DataVisualizerLayoutFileLoader.Status.LoadingOk);
        assertEquals(dvlf.size(), 1);
    }

    @Test
    void test_01()
    {
        DataVisualizerLayoutFileLoader dvlf = new DataVisualizerLayoutFileLoader("TestData/test06_.dvl");
        assertEquals(dvlf.getStatus(), DataVisualizerLayoutFileLoader.Status.LoadingError);
    }

    @Test
    void test_02()
    {
        DataVisualizerLayoutFileLoader dvlf = new DataVisualizerLayoutFileLoader("TestData/demo3.dvl");
        assertEquals(dvlf.getStatus(), DataVisualizerLayoutFileLoader.Status.LoadingOk);
        assertEquals(dvlf.size(), 1);
    }

    @Test
    void test_03()
    {
        DataVisualizerLayoutFileLoader dvlf = new DataVisualizerLayoutFileLoader("TestData/demo4.dvl");
        assertEquals(dvlf.getStatus(), DataVisualizerLayoutFileLoader.Status.LoadingOk);
        assertEquals(dvlf.size(), 2);
    }

}
