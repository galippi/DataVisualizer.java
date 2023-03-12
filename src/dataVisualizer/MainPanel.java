package dataVisualizer;

import java.awt.Color;

import lippiWare.utils.dbg;

public class MainPanel extends javax.swing.JPanel
{
    public MainPanel()
    {
        super();
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        dbg.println(9, "MainPanel - paintComponent");
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.drawOval(getWidth()/2, getHeight() / 2, getWidth()/2, getHeight() / 2);
        ctr++;
        // cursor drawing
        g.setColor(Color.BLACK);
        g.drawString("MainPanel ctr=" + ctr, 5, 10);
    }
    int ctr = 0;
    private static final long serialVersionUID = 3056582303989070070L;
}
