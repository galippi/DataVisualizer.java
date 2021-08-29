package DataVisualizer;

import java.awt.Color;
import java.util.Vector;

import javax.swing.BoxLayout;

import utils.dbg;

public class DataPanelMain extends javax.swing.JPanel {
    public DataPanelMain()
    {
        reinit();
    }

    public void reinit()
    {
        dataPanels.clear();
        updateLayout();
    }

    public void loadFile(String _filename)
    {
        dbg.println(9, "DataPanelMain.loadFile " + _filename);
        reinit();
        dataPanels.clear();
        DataPanel dataPanel = new DataPanel();
        dataPanels.add(dataPanel);
//        setLayout(new java.awt.BorderLayout());
        //removeAll();
        //add(dataPanel);
        updateLayout();
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
    }
    int ctr = 0;

    Vector<DataPanel> dataPanels = new Vector<>();

    /**
     * 
     */
    private static final long serialVersionUID = -1520781722850105662L;
}
