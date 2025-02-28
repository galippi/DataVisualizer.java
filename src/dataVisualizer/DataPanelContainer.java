package dataVisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JSplitPane;

import dataCache.DataCache_FileBase;
import lippiWare.utils.dbg;

public class DataPanelContainer extends javax.swing.JPanel implements DataChannelListProvider
{
    static int windowIdxMax = 0;
    int windowIdx;
    DataPanelContainer(DataPanelMain parent, DataCache_FileBase file, DataChannelList dcl)
    {
        super(new BorderLayout());
        windowIdx = windowIdxMax;
        windowIdxMax++;
        dataPanelLegend = new DataPanelLegendContainer(parent, file, dcl);
        //dataPanelLegend = new DataPanelLegendValue(parent, file, dcl);
        if (!file.isPointBasedFile())
            dataPanel = new DataPanelTimeBased(this, parent, file, dcl);
        else
            dataPanel = new DataPanelPointBased(this, parent, file, dcl);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataPanelLegend, dataPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        //splitPane.setDividerLocation(150);
        splitPane.setResizeWeight(0.0);
        //Provide minimum sizes for the two components in the split pane
        dataPanelLegend.setMinimumSize(new Dimension(100, 50));
        dataPanelLegend.setMaximumSize(new Dimension(200, 9999));
        dataPanel.setMinimumSize(new Dimension(200, 50));
        splitPane.setMinimumSize(new Dimension(200, 100));
        add(splitPane, BorderLayout.CENTER);
        //setMinimumSize(new Dimension(400, 50));
        parent.revalidate();
    }

    public int getWindowIdx() {
        return windowIdx;
    }

    @Override
    public DataChannelList getDataChannelList() {
        return dataPanel.getDataChannelList();
    }

    public void repaintRequest() {
        dataPanel.dataImage.repaint();
    }

    public void setDataCursor(int cursorIdx, int x, double hPos) {
        dataPanel.setDataCursor(cursorIdx, x);
        dataPanelLegend.setDataCursor(hPos);
    }

    public void setHorizontalZoom(double hPosMinNew, double hPosMaxNew) {
        dataPanel.setHorizontalZoom(hPosMinNew, hPosMaxNew);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if (dbg.get(19))
        {
            dbg.println(19, "DataPanelContainer - paintComponent");
            //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
            //g2.setBackground(Color.GREEN);
            //g.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 5, getHeight() / 2 - 5);
        }
    }

    public void setLegendValue(int i, String valStr) {
        dataPanelLegend.setLegendValue(i, valStr);
    }

    DataPanelLegendBase dataPanelLegend;
    DataPanel dataPanel;
    JSplitPane splitPane;

    private static final long serialVersionUID = 5934515992545813218L;
}
