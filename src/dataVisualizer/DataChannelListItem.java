package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_ChannelBase;
import utils.dbg;

public class DataChannelListItem {
    public DataChannelListItem(DataCache_ChannelBase _ch)
    {
        ch = _ch;
        dbg.dprintf(9, "DataChannelListItem.ctor ch=%s\n", ch.getName());
        group = new DataChannelGroup(ch.getName());
        color = getNextColor();
    }

    public DataChannelListItem(String chName, double factor, double offset, Color color2, String groupName) {
        // TODO Auto-generated constructor stub
    }

    public String getSignalName() {
        return ch.getName();
    }

    Color getNextColor()
    {
        return Color.BLACK;
    }

    DataCache_ChannelBase ch;
    Color color;
    DataChannelGroup group;
}
