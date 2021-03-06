package dataVisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JSplitPane;

import dataCache.DataCache_File;
import utils.dbg;

public class DataPanelContainer extends javax.swing.JPanel implements DataChannelListProvider
{
    static int windowIdxMax = 0;
    int windowIdx;
    DataPanelContainer(DataPanelMain parent, DataCache_File file, DataChannelList dcl)
    {
        super(new BorderLayout());
        windowIdx = windowIdxMax;
        windowIdxMax++;
        dataPanelLegend = new DataPanelLegend(parent, file, dcl);
        //dataPanelLegend = new DataPanelLegendValue(parent, file, dcl);
        dataPanel = new DataPanel(this, parent, file, dcl);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataPanelLegend, dataPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(150);
        splitPane.setResizeWeight(0.5);
        //Provide minimum sizes for the two components in the split pane
        dataPanelLegend.setMinimumSize(new Dimension(100, 50));
        dataPanelLegend.setMaximumSize(new Dimension(200, 9999));
        dataPanel.setMinimumSize(new Dimension(400, 200));
        add(splitPane, BorderLayout.CENTER);
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

    public void setDataCursor(int cursorIdx, int x, int hPos) {
        dataPanel.setDataCursor(cursorIdx, x);
        dataPanelLegend.setDataCursor(hPos);
    }

    public void setHorizontalZoom(int hPosMinNew, int hPosMaxNew) {
        dataPanel.setHorizontalZoom(hPosMinNew, hPosMaxNew);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(9, "DataPanelContainer - paintComponent");
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        if (dbg.get(9))
        {
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
            //g2.setBackground(Color.GREEN);
            //g.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 5, getHeight() / 2 - 5);
        }
    }

    DataPanelLegendBase dataPanelLegend;
    DataPanel dataPanel;
    JSplitPane splitPane;

    private static final long serialVersionUID = 5934515992545813218L;
}
