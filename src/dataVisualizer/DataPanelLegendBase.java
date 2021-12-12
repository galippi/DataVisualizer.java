package dataVisualizer;

import javax.swing.JPanel;

import dataCache.DataCache_File;

public class DataPanelLegendBase extends JPanel
{
    DataChannelList dataChannelList;
    DataPanelMain parent;

    public DataPanelLegendBase(DataPanelMain parent, DataCache_File file, DataChannelList dcl)
    {
        this.parent = parent;
        dataChannelList = dcl;
    }

    private static final long serialVersionUID = 5780266995631515871L;
}
