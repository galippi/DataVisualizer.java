package dataVisualizer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import dataCache.DataCache_FileBase;
import dataVisualizer.interfaces.DataChannelListChangeEventHandler;

public abstract class DataPanelLegendBase extends JPanel implements DataChannelListChangeEventHandler
{
    DataChannelList dataChannelList;
    DataPanelMain parent;

    public DataPanelLegendBase(DataPanelMain parent, DataCache_FileBase file, DataChannelList dcl)
    {
        super(new BorderLayout());
        this.setMinimumSize(new Dimension(100, 100));
        this.parent = parent;
        dataChannelList = dcl;
        dataChannelList.addDataChannelListChangeEventHandler(this);
    }

    public void setDataCursor(double cursorIdx) {
        // do nothing
    }

    private static final long serialVersionUID = 5780266995631515871L;
}
