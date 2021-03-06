package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_File;
import utils.dbg;

public class DataChannelListItem {
    public DataChannelListItem(DataCache_ChannelBase _ch)
    {
        ch = _ch;
        dbg.dprintf(9, "DataChannelListItem.ctor ch=%s\n", ch.getName());
        group = ch.getName();
        color = getNextColor();
    }

    public DataChannelListItem(DataCache_File dcf, String chName, double factor, double offset, Color _color, String groupName) throws Exception {
        ch = dcf.getChannel(chName);
        if (ch == null)
            throw new Exception("DataChannelListItem.ctor chName="+chName+"!");
        color = _color;
        //group = new DataChannelGroup(groupName, factor, offset);
        group = groupName;
    }

    public String getSignalName() {
        return ch.getName();
    }

    Color getNextColor()
    {
        return Color.BLACK;
    }

    public double getDouble(int hIdx) {
        try {
            return ch.getDouble(hIdx);
        } catch (Exception e) {
            dbg.println(1, "Exception: DataChannelListItem.getDouble("+ch.getName()+","+hIdx+") e="+e.toString());
            return 0;
        }
    }

    public void update(Color _color, String groupName)
    {
        color = _color;
        if (!group.equals(groupName))
            group = groupName;
    }

    DataCache_ChannelBase ch;
    Color color;
    String group;
}
