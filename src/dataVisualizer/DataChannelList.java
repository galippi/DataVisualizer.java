package dataVisualizer;

import java.util.Vector;

import dataCache.DataCache_File;
import dataCache.DataCache_ChannelBase;

public class DataChannelList {
    DataCache_ChannelBase horizontalAxle;
    Vector<DataChannelListItem> dataChannels = new Vector<>();
    Vector<DataChannelListUpdateCallback> updateCallbacks = new Vector<>();
    DataCache_File file;
    public DataChannelList(DataCache_File _file)
    {
        file = _file;
        clear();
    }
    public int size() {
        return dataChannels.size();
    }
    public DataChannelListItem get(int i) {
        return dataChannels.get(i);
    }
    public void clear()
    {
        dataChannels.clear();
        horizontalAxle = null;
    }
    public void addSignal(String colName)
    {
        dataChannels.add(new DataChannelListItem(file.getChannel(colName)));
        
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
