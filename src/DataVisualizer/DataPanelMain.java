package dataVisualizer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import dataCache.DataCache_File;
import dataCache.DataCache_State;
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
        dataPanels.clear();
        updateLayout();
    }

    public void loadFile(String filename)
    {
        dbg.println(9, "DataPanelMain.loadFile " + filename);
        reinit();
        dataPanels.clear();
        file = new DataCache_File();
        file.addActionListener(this);
        file.open(filename);
        //DataPanel dataPanel = new DataPanel(this, file);
        //dataPanels.add(dataPanel);
//        setLayout(new java.awt.BorderLayout());
        //removeAll();
        //add(dataPanel);
        updateLayout();
    }

    public DataCache_File getDataFile()
    {
        return file;
    }

    void updateLayout()
    {
        removeAll();
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(new java.awt.BorderLayout());

        int num = dataPanels.size();
        for (int i = 0; i < num; i++)
        {
            DataPanel panel = dataPanels.get(i);
            panel.setLocation(0, i * getHeight() / num);
            panel.setSize(getWidth(), getHeight() / num);
            add(panel);
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
        g.setColor(Color.GREEN);
        g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 5, getHeight() / 2 - 5);
        ctr++;
        // cursor drawing
        g.setColor(Color.BLACK);
        g.drawString("DataPanelMain ctr=" + ctr, 5, 10);
        String state;
        if (file == null)
            state = "No file is selected!";
        else
            state = file.getStateString();
        g.drawString(state, 5, getHeight() / 2);
    }
    int ctr = 0;

    Vector<DataPanel> dataPanels = new Vector<>();
    DataCache_File file;

    /**
     * 
     */
    private static final long serialVersionUID = -1520781722850105662L;

    public DataVisualizerUI getMainFrame() {
        return parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (file.getState() == DataCache_State.DataCache_Ready)
        {
            DataVisualizerLayoutFileLoader dvlf = new DataVisualizerLayoutFileLoader(file.getName());
            DataPanel dataPanel = new DataPanel(this, file);
            dataPanels.add(dataPanel);
            updateLayout();
        }
    }
}
