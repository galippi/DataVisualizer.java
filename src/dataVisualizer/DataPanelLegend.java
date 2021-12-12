package dataVisualizer;

import java.awt.Color;

import javax.swing.JPanel;

import dataCache.DataCache_File;
import utils.dbg;

public class DataPanelLegend extends JPanel {
    public DataPanelLegend(DataPanelMain parent, DataCache_File file, DataChannelList dcl)
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(9, "DataPanelLegend - paintComponent");
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        if (dbg.get(9))
        {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
            //g2.setBackground(Color.GREEN);
            //g.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.GREEN);
            g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 5, getHeight() / 2 - 5);
        }
    }

    private static final long serialVersionUID = 6804775315236158088L;
}
