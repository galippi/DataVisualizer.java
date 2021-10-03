package dataVisualizer;

import java.util.TreeMap;
import java.util.Vector;

import dataCache.DataCache_File;
import utils.dbg;
import dataCache.DataCache_ChannelBase;

public class DataChannelList {
    DataCache_ChannelBase horizontalAxle;
    Vector<DataChannelListItem> dataChannels = new Vector<>();
    TreeMap<String, DataChannelListItem> mapName = new TreeMap();
    Vector<DataChannelListUpdateCallback> updateCallbacks = new Vector<>();
    DataCache_File file;
    public DataChannelList(DataCache_File _file)
    {
        file = _file;
        clear();
        horizontalAxle = file.getIndexChannel();
    }
    public int size() {
        return dataChannels.size();
    }
    public DataChannelListItem get(int i) {
        return dataChannels.get(i);
    }
    public DataChannelListItem get(String chName) {
        return mapName.get(chName);
    }
    public void clear()
    {
        dataChannels.clear();
        horizontalAxle = null;
        mapName.clear();
    }
    public void addSignal(String colName)
    {
        DataCache_ChannelBase ch = file.getChannel(colName);
        if (ch == null)
            dbg.dprintf(1, "DataChannelList.addSignal colName=%s\n", colName);
        DataChannelListItem dcli = new DataChannelListItem(ch);
        dataChannels.add(dcli);
        mapName.put(colName, dcli);
    }
    public void setHorizontalAxle(String colName)
    {
        horizontalAxle = file.getChannel(colName);
    }
    public void updateCallbacksExecute()
    {
        for(int i = 0; i < updateCallbacks.size(); i++)
        {
            updateCallbacks.get(i).callBack(this);
        }
        
    }
}
