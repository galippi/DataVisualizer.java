package dataVisualizer;

public class DataChannelGroup {
    DataChannelGroup(String _name)
    {
        name = _name;
        offset = 0;
        factor = -1e99;
    }

    public boolean isFactorDefault() {
        return (factor < -1e97);
    }

    String name;
    double offset = 0;
    double factor = -1e99;
    double valMin = 1e99;
    double valMax = -1e99;
}
