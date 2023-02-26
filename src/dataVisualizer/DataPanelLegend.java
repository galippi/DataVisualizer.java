package dataVisualizer;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import dataCache.DataCache_FileBase;
import utils.Sprintf;
import utils.dbg;

public class DataPanelLegend extends DataPanelLegendBase
{
    public DataPanelLegend(DataPanelMain parent, DataCache_FileBase file, DataChannelList dcl)
    {
        super(parent, file, dcl);
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
        java.awt.FontMetrics metrics = g.getFontMetrics();
        int scaleMax = Math.max(2, Math.min(10, (diagHeight / 20) / groups.size()));
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
                    String signalName = dcli.getSignalName();
                    int textWidth = metrics.stringWidth(signalName);
                    //dbg.println(9, signalName + "=" + textWidth);
                    g2.drawString(signalName, -((int)((1.0 - (cg.ySize / 2 + cg.yOffset)) * diagHeight) + (textWidth / 2)), x);
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
            ScaleData sd = new ScaleData(vMin, vMax, 2, scaleMax, false);
            int verticalStep = sd.num;
            double dv = sd.step;
            boolean scaleValInteger;
            if (sd.num * sd.step >= 100)
                scaleValInteger = true;
            else
                scaleValInteger = false;
            final int scaleValWidth = 30;
            if (sd.scale != 0)
            { // draw scale factor
                String scaleFactor = "x 1e" + sd.scale;
                int textWidth = metrics.stringWidth(scaleFactor);
                //int y = -(int)((1.0 - (cg.ySize / 2 + cg.yOffset)) * diagHeight);
                int y = getY(cg, (vMin + vMax) / 2);
                g2.drawString(scaleFactor, y + (textWidth / 2), x);
                x += 8;
            }
            final int scaleLineX = x + scaleValWidth;
            double val = vMin;
            for(int j = 0; j <= verticalStep; val += dv, j++)
            {
                int y = -getY(cg, val);
                String scaleVal;
                if (scaleValInteger)
                    scaleVal = "" + (int)val;
                else
                    scaleVal = Sprintf.sprintf("%4.2f", val);
                int textWidth = metrics.stringWidth(scaleVal);
                g2.setTransform(defaultAt);
                g2.drawString(scaleVal, scaleLineX - textWidth - 2, -y + 4);
                g2.setTransform(at);
                final int scaleSize = 3;
                g2.drawLine(y, scaleLineX - scaleSize, y, scaleLineX + scaleSize);
            }
            x += scaleValWidth;
            g2.drawLine(-getY(cg, vMin), x, -getY(cg, vMax), x);
        }
        g2.setTransform(defaultAt);
        if (dbg.get(19))
        {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), diagHeight);
            //g2.setBackground(Color.GREEN);
            //g.clearRect(0, 0, getWidth(), diagHeight);
            g.setColor(Color.GREEN);
            g.fillOval(getWidth() / 2, diagHeight / 2, getWidth() / 2 - 5, diagHeight / 2 - 5);
        }
    }

    private Color getDiagramBackgroundColor() {
        return parent.bgColor;
    }

    private int getY(DataChannelGroup dcg, double val) {
        return diagHeight - (int)(((val - dcg.offset) * dcg.factor) * diagHeight + 0.5);
    }

    @Override
    public void dataChannelListChangeEventHandler(DataChannelList dcl)
    {
        repaint();
    }

    private static final long serialVersionUID = 6804775315236158088L;
}
