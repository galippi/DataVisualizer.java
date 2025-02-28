package dataVisualizer;

import java.awt.BorderLayout;

import javax.swing.JSplitPane;

import dataCache.DataCache_FileBase;

public class DataPanelLegendContainer extends DataPanelLegendBase {

    public DataPanelLegendContainer(DataPanelMain parent, DataCache_FileBase file, DataChannelList dcl) {
        super(parent, file, dcl);
        legend = new DataPanelLegend(parent, file, dcl);
        value = new DataPanelLegendValue(parent, file, dcl);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, legend, value);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void dataChannelListChangeEventHandler(DataChannelList dcl) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDataCursor(double cursorIdx) {
        legend.setDataCursor(cursorIdx);
        value.setDataCursor(cursorIdx);
    }

    @Override
    public void setLegendValue(int i, String valStr) {
        value.setLegendValue(i, valStr);
    }

    DataPanelLegendBase legend;
    DataPanelLegendBase value;

    private static final long serialVersionUID = -5157593322106379556L;
}
