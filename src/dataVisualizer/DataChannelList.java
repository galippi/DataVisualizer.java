package dataVisualizer;

import java.awt.Color;
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

    public DataChannelList(DataCache_File _file, Vector<DataChannelListItem> dcl, String horizontalAxleChannelName)
    {
        file = _file;
        clear();
        dataChannels = dcl;
        for (DataChannelListItem dcli: dcl)
        {
            String colName = dcli.getSignalName();
            mapName.put(colName, dcli);
        }
        horizontalAxle = file.getChannel(horizontalAxleChannelName);
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
    public void addSignal(String signalName, Color color, String groupName)
    {
        DataChannelListItem dcli;
        try {
            dcli = new DataChannelListItem(file, signalName, 1.0, 0.0, color, groupName);
        } catch (Exception e) {
            dbg.dprintf(1, "Exception: addSignal signalName=%s e=%s!\n", signalName, e.toString());
            return;
        }
        dataChannels.add(dcli);
        mapName.put(signalName, dcli);
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

    public Object getChName(int index) {
        return dataChannels.get(index).getSignalName();
    }

    public boolean isReady() {
        return true;
    }
}
