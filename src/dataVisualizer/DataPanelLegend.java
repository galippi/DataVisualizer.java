package dataVisualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import dataCache.DataCache_File;
import utils.dbg;

public class DataPanelLegend extends JPanel {
    DataChannelList dataChannelList;
    DataPanelMain parent;
    public DataPanelLegend(DataPanelMain parent, DataCache_File file, DataChannelList dcl)
    {
        this.parent = parent;
        dataChannelList = dcl;
    }

    int diagHeight = 1;
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(9, "DataPanelLegend - paintComponent");
        java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        diagHeight = getHeight();
        g.setColor(getDiagramBackgroundColor());
        g.fillRect(0, 0, getWidth(), diagHeight);
        // drawing legend
        AffineTransform defaultAt = g2.getTransform();
        AffineTransform at = AffineTransform.getQuadrantRotateInstance(3);
        g2.setTransform(at);
        g.setColor(Color.BLACK);
        //g2.drawString("Vertical string", -100, 10);
        java.util.Vector<DataChannelGroup> groups = dataChannelList.getGroups();
        for(DataChannelGroup cg: groups)
        {
            final int xStep = 10;
            int x = xStep;
            double vMin = 1e99;
            double vMax = -1e99;
            for (int i = 0; i < dataChannelList.size(); i++)
            {
                DataChannelListItem dcli = dataChannelList.get(i);
                if (cg.name.equals(dcli.group))
                {
                    g.setColor(dcli.color);
                    java.awt.FontMetrics metrics = g.getFontMetrics();
                    String signalName = dcli.getSignalName();
                    int textWidth = metrics.stringWidth(signalName);
                    //dbg.println(9, signalName + "=" + textWidth);
                    g2.drawString(signalName, -((int)((1.0 - (cg.ySize / 2 + cg.yOffset)) * getHeight()) + (textWidth / 2)), x);
                    x += xStep;
                    double vMinSignal = vMin;
                    double vMaxSignal = vMax;
                    try {
                        vMinSignal = dcli.ch.getDoubleMin();
                        vMaxSignal = dcli.ch.getDoubleMax();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (vMin > vMinSignal)
                        vMin = vMinSignal;
                    if (vMax < vMaxSignal)
                        vMax = vMaxSignal;
                }
            }
            // drawing vertical axle
            int verticalStep = 10;
            double dv = (vMax - vMin);
            if (Math.abs(dv) < 1e-19)
            {
                dv = 1;
                vMax = vMin + dv;
                verticalStep = 2;
            }
            g2.drawLine(-getY(cg, vMin), x, -getY(cg, vMax), x);
            dv = dv / verticalStep;
            double val = vMin;
            for(int y = 0; y < verticalStep; val += dv, y++)
            {
                final int scaleSize = 3;
                g2.drawLine(-getY(cg, val), x - scaleSize, -getY(cg, val), x + scaleSize);
            }
        }
        g2.setTransform(defaultAt);
        if (dbg.get(19))
        {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
            //g2.setBackground(Color.GREEN);
            //g.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.GREEN);
            g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 5, getHeight() / 2 - 5);
        }
    }

    private Color getDiagramBackgroundColor() {
        return parent.bgColor;
    }

    private int getY(DataChannelGroup dcg, double val) {
        return diagHeight - (int)(((val - dcg.offset) * dcg.factor) * diagHeight + 0.5);
    }

    private static final long serialVersionUID = 6804775315236158088L;
}
