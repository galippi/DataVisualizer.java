package dataVisualizer;

public class DataChannelGroup {
    DataChannelGroup(String _name)
    {
        name = _name;
        offset = 0;
        factor = 1.0;
    }
    String name;
    double offset;
    double factor;
}
