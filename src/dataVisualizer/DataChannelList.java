package dataVisualizer;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import java.util.Vector;

import dataCache.DataCache_File;
import utils.dbg;
import dataCache.DataCache_ChannelBase;

public class DataChannelList {
    DataCache_ChannelBase horizontalAxle;
    Vector<DataChannelListItem> dataChannels = new Vector<>();
    TreeMap<String, DataChannelListItem> mapName = new TreeMap<>();
    Vector<DataChannelListUpdateCallback> updateCallbacks = new Vector<>();
    Vector<ActionListener> actionListeners = new Vector<>();
    DataCache_File file;
    int groupCnt = -1;
    Vector<DataChannelGroup> groups = new Vector<>();
    TreeMap<String, DataChannelGroup> groupMap = new TreeMap<>();

    public DataChannelList(DataCache_File _file)
    {
        file = _file;
        clear();
        horizontalAxle = file.getIndexChannel();
        pointIndexMin = 0;
        try {
            pointIndexMax = file.getLength();
        } catch (Exception e) {
            dbg.dprintf(1, "file.getLength exception e=%s!\n", e.toString());
        }
        updateGroupData();
    }

    public DataChannelList(DataCache_File _file, Vector<DataChannelListItem> dcl, String horizontalAxleChannelName, int piMin, int piMax)
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
        pointIndexMin = piMin;
        pointIndexMax = piMax;
        updateGroupData();
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
        addGroup(groupName);
    }

    private boolean addGroup(String groupName) {
        DataChannelGroup cgm = groupMap.get(groupName);
        if (cgm == null)
        {
            DataChannelGroup cg = new DataChannelGroup(groupName);
            groupMap.put(cg.name, cg);
            groups.add(cg);
            return false;
        }
        return true;
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
        for(ActionListener al: actionListeners)
        {
            al.actionPerformed(null);
        }
    }

    public Object getChName(int index) {
        return dataChannels.get(index).getSignalName();
    }

    public boolean isReady() {
        return true;
    }

    public int getDataPointIndexMin() {
        return pointIndexMin;
    }

    public int getDataPointIndexMax() {
        return pointIndexMax;
    }

    int pointIndexMin = -1, pointIndexMax = -1;
    public DataCache_ChannelBase getHorizontalAxle() {
        return horizontalAxle;
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void updateGroupData() {
        groupCnt = -1;
        groups.clear();
        groupMap.clear();
        for(DataChannelListItem dcli: dataChannels)
        {
            String groupName = dcli.group;
            if (addGroup(groupName))
            {
                DataChannelGroup cgm = groupMap.get(groupName);
                if (!groupName.equals(cgm.name))
                {
                    dbg.println(1, "DataChannelList.updateGroupData cg != cgm name="+groupName+" signal="+dcli.getSignalName());
                    System.exit(2);
                    //throw new Exception("DataChannelList.updateGroupData cg != cgm name="+cg.name);
                }
            }
        }
        groupCnt = groups.size();
        int grpIdx = 0;
        for(DataChannelGroup cg: groups)
        {
            if (cg.isFactorDefault())
            {
                double valMin = 1e99;
                double valMax = -1e99;
                for(DataChannelListItem dcli: dataChannels)
                {
                    if (dcli.group == cg.name)
                    {
                        try {
                            double valMinLocal = dcli.ch.getDoubleMin();
                            double valMaxLocal = dcli.ch.getDoubleMax();
                            if (valMin > valMinLocal)
                                valMin = valMinLocal;
                            if (valMax < valMaxLocal)
                                valMax = valMaxLocal;
                        } catch (Exception e) {
                            dbg.println(1, "Exception DataChannelList.updateGroupData getDoubleMin/getDoubleMax name="+cg.name+" signal="+dcli.getSignalName() + " e=" + e.toString());
                            System.exit(2);
                            //throw new Exception("DataChannelList.updateGroupData cg != cgm name="+cg.name);
                        }
                    }
                }
                cg.valMin = valMin;
                cg.valMax = valMax;
                double deltaVal = (cg.valMax - cg.valMin);
                if (Math.abs(deltaVal) < 1e-19)
                {
                    deltaVal = 1;
                    cg.valMax = cg.valMin + deltaVal;
                }
                cg.factor = 1.0 / deltaVal / groupCnt;
                cg.offset = cg.valMin - (grpIdx / cg.factor / groupCnt);
                cg.ySize = 1.0 / groupCnt;
                cg.yOffset = grpIdx * cg.ySize;
            }
            grpIdx++;
        }
    }

    public DataChannelList copy()
    {
        DataChannelList result = new DataChannelList(file);
        for(DataChannelListItem dcli: dataChannels)
        {
            result.addSignal(dcli.getSignalName(), dcli.color, dcli.group);
        }
        result.updateGroupData();
        return result;
    }

    public void remove(DataChannelListItem dcli)
    {
        dataChannels.remove(dcli);
        mapName.remove(dcli.getSignalName());
    }

    public DataChannelGroup getGroup(String groupName) {
        return groupMap.get(groupName);
    }

    public Vector<DataChannelGroup> getGroups() {
        return groups;
    }
}
