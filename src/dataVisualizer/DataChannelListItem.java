package dataVisualizer;

import dataCache.DataCache_ChannelBase;

public class DataChannelListItem {
    public DataChannelListItem(DataCache_ChannelBase _ch)
    {
        ch = _ch;
    }

    public String getSignalName() {
        return ch.getName();
    }
    DataCache_ChannelBase ch;
}
