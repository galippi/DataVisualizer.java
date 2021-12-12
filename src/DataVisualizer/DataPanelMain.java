package dataVisualizer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import dataCache.DataCache_File;
import dataCache.DataCache_State;
import dataVisualizer.DataVisualizerLayoutFileLoader.Status;
import utils.dbg;

public class DataPanelMain extends javax.swing.JPanel implements ActionListener {
    public DataPanelMain(DataVisualizerUI _parent)
    {
        parent = _parent;
        reinit();
    }
    DataVisualizerUI parent;

    public void reinit()
    {
        file = null;
        dataPanels.clear();
        dvlf = null;
        updateLayout();
    }

    public void loadFile(String filename)
    {
        dbg.println(9, "DataPanelMain.loadFile " + filename);
        reinit();
        file = new DataCache_File();
        file.addActionListener(this);
        file.open(filename);
        updateLayout();
    }

    public DataCache_File getDataFile()
    {
        return file;
    }

    void updateLayout()
    {
        dbg.println(11, "DataPanelMain.updateLayout");
        removeAll();
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //setLayout(new java.awt.BorderLayout());

        if (dvlf != null)
        {
            setLayout(new java.awt.GridLayout(dvlf.size(), 1));
            if (dataPanels.size() != dvlf.size())
            {
                if (dataPanels.size() == 0)
                { // dvlf is loaded -> create new DataPanels
                    for (int i = 0; i < dvlf.size(); i++)
                    {
                        try {
                            dataPanels.add(new DataPanelContainer(this, file, dvlf.getDataChannelList(i, file)));
                        } catch (Exception e) {
                            dbg.dprintf(1, "Exception: updateLayout i=%d!\n", i);
                        }
                    }
                    parent.setTitle("DataVisualizer - " + file.getName());
                }else
                { // dataPanels is updated -> update dvlf
                    
                }
            }
            int num = dataPanels.size();
            parent.m_ViewChannel.setEnabled(num == 1 ? true : false);
            for (int i = 0; i < num; i++)
            {
                DataPanelContainer panel = dataPanels.get(i);
                panel.setLocation(0, i * getHeight() / num);
                panel.setSize(getWidth(), getHeight() / num);
                add(panel);
            }
            cursorsTogether = dvlf.cursorsMoveTogether;
            parent.m_ViewCursorModeTogether.setState(cursorsTogether);
            parent.m_ViewCursorModeTogether.setEnabled(true);
        }else
            if (parent.m_ViewChannel != null)
                parent.m_ViewChannel.setEnabled(false);
        //pack();
        //doLayout();
        repaint();
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        dbg.println(9, "DataPanelMain - paintComponent num=" + dataPanels.size());
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (dbg.get(19))
        {
            g.setColor(Color.GREEN);
            g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 5, getHeight() / 2 - 5);
        }
        if (dbg.get(19))
        {
            g.setColor(Color.BLUE);
            g.drawRect(0, 0, getWidth(), getHeight());
        }
        ctr++;
        // cursor drawing
        g.setColor(Color.BLACK);
        if (dbg.get(19))
            g.drawString("DataPanelMain ctr=" + ctr, 5, 10);
        if (dbg.get(19))
        {
            String state;
            if (file == null)
                state = "No file is selected!";
            else
                state = file.getStateString();
            g.drawString(state, 5, getHeight() / 2);
        }
    }
    int ctr = 0;

    Vector<DataPanelContainer> dataPanels = new Vector<>();
    DataCache_File file;
    DataVisualizerLayoutFileLoader dvlf;
    boolean cursorsTogether = true;

    private static final long serialVersionUID = -1520781722850105662L;

    public DataVisualizerUI getMainFrame() {
        return parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (file.getState() == DataCache_State.DataCache_Ready)
        {
            dvlf = new DataVisualizerLayoutFileLoader(file.getName());
            if ((dvlf == null) || (dvlf.status != Status.LoadingOk) || (!DataVisualizerLayout.checkConsistency(file, dvlf)))
            {
                dbg.println(9, "DataPanelMain.actionPerformed dvlf error!");
                dvlf = new DataVisualizerLayoutFileLoader(file);
            }
            updateLayout();
        }else
            dvlf = null;
    }

    public void saveDataLayoutFile() {
        dbg.dprintf(9, "DataPanelMain.saveDataLayoutFile\n");
        if ((file == null) || (file.getState() != DataCache_State.DataCache_Ready))
        {
            dbg.dprintf(9, "saveDataLayoutFile - no file is loaded\n");
            return;
        }
        Vector<DataChannelListProvider> dataPanelsLocal = new Vector<>();
        for (DataPanelContainer dataPanel : dataPanels)
            dataPanelsLocal.add(dataPanel);
        //cursorsTogether = parent.m_ViewCursorModeTogether.getState();
        DataVisualizerLayoutFileLoader.saveLayoutFile(file.getName(), dataPanelsLocal, cursorsTogether);
    }

    public void createNewWindow() {
        dataPanels.add(new DataPanelContainer(this, file, new DataChannelList(file)));
        updateLayout();
    }

    public void closeWindow(DataPanel dataPanel) {
        dataPanels.remove(dataPanel);
        updateLayout();
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        bgColor = backgroundColor;
        for (DataPanelContainer dataPanel : dataPanels)
            dataPanel.repaintRequest();
    }

    Color bgColor = DataVisualizerPrefs.getBackgroundColor(new Color(0, 0, 0));

    void setDataCursor(int windowIdx, int cursorIdx, int x, int hPos)
    {
        for (DataPanelContainer dataPanel: dataPanels)
        {
            if ((cursorsTogether) || (dataPanel.windowIdx == windowIdx))
                dataPanel.setDataCursor(cursorIdx, x, hPos);
        }
    }

    public void setHorizontalZoom(int hPosMinNew, int hPosMaxNew)
    {
        for (DataPanelContainer dataPanel: dataPanels)
        {
            dataPanel.setHorizontalZoom(hPosMinNew, hPosMaxNew);
        }
    }

    public void setCursorsTogether(boolean state)
    {
        cursorsTogether = state;
    }
}
