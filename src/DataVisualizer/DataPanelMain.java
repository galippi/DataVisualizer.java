package dataVisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JSplitPane;

import dataCache.DataCache_FileBase;
import dataCache.DataCache_FileCanBlf;
import dataCache.DataCache_FileDiaDat;
import dataCache.DataCache_State;
import dataVisualizer.DataVisualizerLayoutFileLoader.Status;
import lippiWare.utils.dbg;

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
        if (DataCache_FileDiaDat.fastCheck(filename))
            file = new DataCache_FileDiaDat();
        else if (DataCache_FileCanBlf.fastCheck(filename))
            file = new DataCache_FileCanBlf();
        else
            throw new Error("Not supported file format '" + filename + "'!");
        file.addActionListener(this);
        file.open(filename);
        updateLayout();
    }

    public DataCache_FileBase getDataFile()
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
            javax.swing.JComponent prev = null;
            for (int i = 0; i < num; i++)
            {
                DataPanelContainer panel = dataPanels.get(i);
                //panel.setLocation(0, i * getHeight() / num);
                //panel.setSize(getWidth(), getHeight() / num);
                //add(panel);
                if (prev != null)
                {
                    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, prev, panel);
                    //splitPane.setDividerLocation(150);
                    splitPane.setResizeWeight(1.0 - (1.0 / (i + 1)));
                    if (true)
                    {
                        //splitPane.setMinimumSize(new java.awt.Dimension(100, 100));
                        prev = splitPane;
                    }else
                    {
                        javax.swing.JPanel collector = new javax.swing.JPanel();
                        collector.add(splitPane, BorderLayout.CENTER);
                        prev = collector;
                    }
                }else
                    prev = panel;
            }
            this.setLayout(new BorderLayout());
            add(prev, BorderLayout.CENTER);
            //parent.setMinimumSize(new java.awt.Dimension(400, 50 * (num == 0 ? 1 : num)));
            parent.setMinimumSize(new java.awt.Dimension(400, 50 * num));
            parent.revalidate();
            cursorsTogether = dvlf.cursorsMoveTogether;
            parent.m_ViewCursorModeTogether.setState(cursorsTogether);
            parent.m_ViewCursorModeTogether.setEnabled(true);
        }else
        {
            parent.setMinimumSize(new java.awt.Dimension(400, 100));
            if (parent.m_ViewChannel != null)
                parent.m_ViewChannel.setEnabled(false);
        }
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
    DataCache_FileBase file;
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
            boolean layoutIsUsed = file.setLayout(dvlf);
            parent.m_DataSourceConfig.setEnabled(layoutIsUsed);
            updateLayout();
        }else {
            dbg.println(1, "DataPanelMain.actionPerformed e=" + e.toString());
            dvlf = null;
            javax.swing.JOptionPane.showMessageDialog(this, "Unable to load file " + file.getName() + "!\nDetailed info: " + e.toString());
        }
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
        cursorsTogether = parent.m_ViewCursorModeTogether.getState();
        DataVisualizerLayoutFileLoader.saveLayoutFile(file.getName(), dataPanelsLocal, cursorsTogether, dvlf);
        //dvlf.saveLayoutFile(file.getName());
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

    void setDataCursor(int windowIdx, int cursorIdx, int x, double hPos)
    {
        for (DataPanelContainer dataPanel: dataPanels)
        {
            if ((cursorsTogether) || (dataPanel.windowIdx == windowIdx))
                dataPanel.setDataCursor(cursorIdx, x, hPos);
        }
    }

    public void setHorizontalZoom(double hPosMinNew, double hPosMaxNew)
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
